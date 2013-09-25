package org.burnhams.flooring.floors.wallenclosed;

import org.burnhams.flooring.floors.Floor;
import org.burnhams.utils.GraphicsUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WallEnclosedFloor implements Floor {

    private final int maxWidth, maxLength;
    private final int horizontalLengthOffsetX, horizontalLengthOffsetY;
    private final int horizontalLength;
    private final CornerWallLength[] lengths;
    private final int floorBitsSize;
    private final BitSet floorBits;
    private final Map<Integer, LengthSegments> rowOffsetLengthSegments = new ConcurrentHashMap<>();

    public WallEnclosedFloor(int horizontalLength, CornerWallLength... lengths) {
        this.horizontalLength = horizontalLength;
        this.lengths = lengths;

        final int[] minXYmaxXY = getMinMaxs(horizontalLength, lengths);
        horizontalLengthOffsetX = -minXYmaxXY[0];
        horizontalLengthOffsetY = -minXYmaxXY[1];
        maxLength = minXYmaxXY[2] - minXYmaxXY[0];
        maxWidth = minXYmaxXY[3] - minXYmaxXY[1];

        floorBitsSize = maxWidth * maxLength;
        floorBits = new BitSet(floorBitsSize);
        setFloorBits();
    }

    private static int[] getMinMaxs(int horizontalLength, CornerWallLength[] lengths) {
        final int[] minXYmaxXY = new int[]{0,0,0,0};
        int[] xy = Utils.traceWalls(0, 0, horizontalLength, lengths, new WallTrace() {
            @Override
            public void trace(int x1, int y1, int x2, int y2, Direction direction, int length) {
                if (x2 < minXYmaxXY[0]) minXYmaxXY[0] = x2;
                if (y2 < minXYmaxXY[1]) minXYmaxXY[1] = y2;
                if (x2 > minXYmaxXY[2]) minXYmaxXY[2] = x2;
                if (y2 > minXYmaxXY[3]) minXYmaxXY[3] = y2;
            }
        });
        if (xy[0] != 0 || xy[1] != 0) {
            try {
                saveErrorImage(minXYmaxXY, horizontalLength, lengths);
            } catch (IOException e) {}
            throw new IllegalArgumentException("Lengths do not end at start: "+xy[0]+", "+xy[1]);
        }
        return minXYmaxXY;
    }

    private void setFloorBit(int x, int y) {
        int bitIndex = getBitIndex(x, y);
        if (bitIndex >= 0) {
            floorBits.set(bitIndex);
        }
    }

    private void unsetFloorBit(int x, int y) {
        int bitIndex = getBitIndex(x, y);
        if (bitIndex >= 0) {
            floorBits.set(bitIndex, false);
        }
    }

    private int getBitIndex(int x, int y) {
        if (x >= 0 && y >= 0 && x < maxLength && y < maxWidth) {
            return y * maxLength + x;
        } else {
            return -1;
        }
    }

    public boolean getFloorBit(int x, int y) {
        int bitIndex = getBitIndex(x, y);
        return bitIndex >= 0 && floorBits.get(bitIndex);
    }

    private void traceWalls(WallTrace tracer) {
        Utils.traceWalls(horizontalLengthOffsetX, horizontalLengthOffsetY, horizontalLength, lengths, tracer);
    }

    private void traceWallPoints(WallTracePoint tracer) {
        Utils.traceWallPoints(horizontalLengthOffsetX, horizontalLengthOffsetY, horizontalLength, lengths, tracer);
    }

    private void setFloorBits() {
        traceWallPoints(new WallTracePoint() {
            @Override
            public void trace(int x, int y, Direction direction, boolean corner) {
                setFloorBit(x,y);
            }
        });
        fillFloorBits(horizontalLengthOffsetX + 1, horizontalLengthOffsetY + 1);
        traceWallPoints(new WallTracePoint() {
            @Override
            public void trace(int x, int y, Direction direction, boolean corner) {
                if (isRightEdge(x, y, direction, corner) || isBottomEdge(x,y, direction, corner)) {
                    unsetFloorBit(x, y);
                }
            }
        });
    }

    private boolean isBottomEdge(int x, int y, Direction direction, boolean corner) {
        return y > 0 && y < maxWidth && getFloorBit(x,y-1) && !getFloorBit(x, y+1) && (direction.isHorizontal() || corner);
    }

    private boolean isRightEdge(int x, int y, Direction direction, boolean corner) {
        return x > 0 && x < maxLength && getFloorBit(x-1,y) && !getFloorBit(x+1, y) && (direction.isVertical() || corner);
    }

    private void fillFloorBits(int x, int y) {
        Deque<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x,y});
        do {
            int[] xy = queue.pop();
            x = xy[0];
            y = xy[1];
            if (x >= 0 && y >= 0 && x < maxLength && y < maxWidth && !getFloorBit(x, y)) {
                setFloorBit(x, y);
                queue.add(new int[]{x-1, y});
                queue.add(new int[]{x, y-1});
                queue.add(new int[]{x+1, y});
                queue.add(new int[]{x, y + 1});
                queue.add(new int[]{x-1, y-1});
                queue.add(new int[]{x+1, y-1});
                queue.add(new int[]{x+1, y+1});
                queue.add(new int[]{x-1, y+1});
            }
        } while (!queue.isEmpty());
    }

    int getFloorBitsSize() {
        return floorBitsSize;
    }

    @Override
    public double getArea() {
        int mms = 0;
        for (int i = 0; i < floorBitsSize; i++) {
            if (floorBits.get(i)) {
                mms++;
            }
        }
        return 0.001 * 0.001 * mms;
    }

    @Override
    public int getWidth() {
        return maxWidth;
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    private BitSet getAggregateLengthBits(int widthStartOffset, int widthEndOffset) {
        BitSet result = new BitSet(maxLength);
        for (int x = 0; x < maxLength; x++) {
            for (int y = widthStartOffset; y < widthEndOffset; y++) {
                if (getFloorBit(x,y)) {
                    result.set(x);
                }
            }
        }
        return result;
    }

    private LengthSegments getLengthSegments(int widthStartOffset, int widthEndOffset) {
        LengthSegments result = rowOffsetLengthSegments.get(widthStartOffset);
        if (result == null) {
            BitSet aggregate = getAggregateLengthBits(widthStartOffset, widthEndOffset);
            List<Integer> segmentStartEnds = new ArrayList<>();
            boolean current = false;
            for (int x = 0; x < maxLength; x++) {
                boolean onFloor = aggregate.get(x);
                if (onFloor != current) {
                    segmentStartEnds.add(x);
                    current = onFloor;
                }
            }
            if (current) {
                segmentStartEnds.add(maxLength);
            }
            if (segmentStartEnds.isEmpty()) {
                segmentStartEnds.add(0);
                segmentStartEnds.add(0);
            }
            result = new LengthSegments(segmentStartEnds);
            rowOffsetLengthSegments.put(widthStartOffset, result);
        }
        return result;
    }

    @Override
    public int getSegments(int widthStartOffset, int widthEndOffset) {
        return getLengthSegments(widthStartOffset, widthEndOffset).getSegments();
    }

    @Override
    public int getSegmentStart(int widthStartOffset, int widthEndOffset, int segment) {
        return getLengthSegments(widthStartOffset, widthEndOffset).getSegmentStart(segment);
    }

    @Override
    public int getSegmentLength(int widthStartOffset, int widthEndOffset, int segment) {
        return getLengthSegments(widthStartOffset, widthEndOffset).getSegmentLength(segment);
    }

    public int getHorizontalLengthOffsetX() {
        return horizontalLengthOffsetX;
    }

    public int getHorizontalLengthOffsetY() {
        return horizontalLengthOffsetY;
    }

    @Override
    public void drawBoundary(final Graphics graphics, final double xMultiple, final double yMultiple) {
        traceWalls(new WallTrace() {
            @Override
            public void trace(int x1, int y1, int x2, int y2, Direction direction, int length) {
                GraphicsUtils.drawLine(graphics, xMultiple, yMultiple, x1, y1, x2, y2);
            }
        });
    }

    public void drawBits(Graphics graphics, double xMultiple, double yMultiple) {
        for (int x = 0; x < maxLength; x++) {
            for (int y = 0; y < maxWidth; y++) {
                if (getFloorBit(x,y)) {
                    GraphicsUtils.drawLine(graphics, xMultiple, yMultiple, x, y, x + 1, y + 1);
                }
            }
        }
    }


    public static void saveErrorImage(int[] minXYmaxXY, int horizontalLength, CornerWallLength[] lengths) throws IOException {
        int width = 2000;
        int horizontalLengthOffsetX = -minXYmaxXY[0];
        int horizontalLengthOffsetY = -minXYmaxXY[1];
        int maxLength = minXYmaxXY[2] - minXYmaxXY[0];
        int maxWidth = minXYmaxXY[3] - minXYmaxXY[1];
        final double xMultiple = (double)width/maxLength;
        int height = (int)Math.round(((double)maxWidth/maxLength)*width);
        final double yMultiple = (double)height/maxWidth;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final Graphics2D graphics = result.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.BLACK);
        Utils.traceWalls(horizontalLengthOffsetX, horizontalLengthOffsetY, horizontalLength, lengths, new WallTrace() {
            @Override
            public void trace(int x1, int y1, int x2, int y2, Direction direction, int length) {
                GraphicsUtils.drawLine(graphics, xMultiple, yMultiple, x1, y1, x2, y2);
            }
        });
        graphics.setColor(Color.RED);
        graphics.dispose();
        ImageIO.write(result, "PNG", new File("errorwallenclosedfloor.png"));
    }


    public BufferedImage createImage(int width) {
        double xMultiple = (double)width/getMaxLength();
        int height = (int)Math.round(((double)getWidth()/getMaxLength())*width);
        double yMultiple = (double)height/getWidth();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = result.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.BLACK);
        drawBoundary(graphics, xMultiple, yMultiple);
        graphics.setColor(Color.RED);
        drawBits(graphics, xMultiple, yMultiple);
        graphics.dispose();
        return result;
    }
}
