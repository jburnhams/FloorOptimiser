package org.burnhams.flooring.floors.wallenclosed;

import org.burnhams.flooring.floors.Floor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class WallEnclosedFloor implements Floor {

    private final int maxWidth, maxLength;
    private final int horizontalLengthOffsetX, horizontalLengthOffsetY;
    private final int horizontalLength;
    private final CornerWallLength[] lengths;
    private final int floorBitsSize;
    private final BitSet floorBits;

    public WallEnclosedFloor(int horizontalLength, CornerWallLength... lengths) {
        this.horizontalLength = horizontalLength;
        this.lengths = lengths;

        final int[] minXYmaxXY = new int[]{0,0,0,0};
        Utils.traceWalls(0,0, horizontalLength,lengths, new WallTrace() {
            @Override
            public void trace(int x1, int y1, int x2, int y2, Direction direction, int length) {
                if (x2 < minXYmaxXY[0]) minXYmaxXY[0] = x2;
                if (y2 < minXYmaxXY[1]) minXYmaxXY[1] = y2;
                if (x2 > minXYmaxXY[2]) minXYmaxXY[2] = x2;
                if (y2 > minXYmaxXY[3]) minXYmaxXY[3] = y2;
            }
        });

        horizontalLengthOffsetX = -minXYmaxXY[0];
        horizontalLengthOffsetY = -minXYmaxXY[1];
        maxLength = minXYmaxXY[2] - minXYmaxXY[0];
        maxWidth = minXYmaxXY[3] - minXYmaxXY[1];

        floorBitsSize = maxWidth * maxLength;
        floorBits = new BitSet(floorBitsSize);
        setFloorBits();
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
        Utils.traceWalls(horizontalLengthOffsetX,horizontalLengthOffsetY, horizontalLength,lengths, tracer);
    }

    private void traceWallPoints(WallTracePoint tracer) {
        Utils.traceWallPoints(horizontalLengthOffsetX, horizontalLengthOffsetY, horizontalLength, lengths, tracer);
    }

    private void setFloorBits() {
        traceWallPoints(new WallTracePoint() {
            @Override
            public void trace(int x, int y) {
                setFloorBit(x,y);
            }
        });
        fillFloorBits(horizontalLengthOffsetX + 1, horizontalLengthOffsetY + 1);
        traceWallPoints(new WallTracePoint() {
            @Override
            public void trace(int x, int y) {
                if (x > 0 && y > 0 && x < maxLength && y < maxWidth && isRightEdge(x, y)) {
                    unsetFloorBit(x, y);
                }
            }
        });
    }

    private boolean isRightEdge(int x, int y) {
        return getFloorBit(x-1,y) && !getFloorBit(x+1, y);
    }

    private void fillFloorBits(int x, int y) {
        if (x >= 0 && y >= 0 && x < maxLength && y < maxWidth && !getFloorBit(x, y)) {
            setFloorBit(x, y);
            fillFloorBits(x-1, y);
            fillFloorBits(x, y-1);
            fillFloorBits(x+1, y);
            fillFloorBits(x, y+1);
        }
    }

    int getFloorBitsSize() {
        return floorBitsSize;
    }

    @Override
    public int getWidth() {
        return maxWidth;
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    public int getHorizontalLengthOffsetX() {
        return horizontalLengthOffsetX;
    }

    public int getHorizontalLengthOffsetY() {
        return horizontalLengthOffsetY;
    }

    @Override
    public int getLength(int widthOffset) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getLength(int widthStartOffset, int widthEndOffset) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void drawLine(Graphics graphics, double xMultiple, double yMultiple, int x1, int y1, int x2, int y2) {
        graphics.drawLine((int)Math.round(xMultiple*x1), (int)Math.round(yMultiple*y1), (int)Math.round(xMultiple*x2), (int)Math.round(yMultiple*y2));
    }

    @Override
    public void drawBoundary(final Graphics graphics, final double xMultiple, final double yMultiple) {
        traceWalls(new WallTrace() {
            @Override
            public void trace(int x1, int y1, int x2, int y2, Direction direction, int length) {
                drawLine(graphics, xMultiple, yMultiple, x1, y1, x2, y2);
            }
        });
    }

    public void drawBits(Graphics graphics, double xMultiple, double yMultiple) {
        for (int x = 0; x < maxLength; x++) {
            for (int y = 0; y < maxWidth; y++) {
                if (getFloorBit(x,y)) {
                    drawLine(graphics, xMultiple, yMultiple, x, y, x+1,y+1);
                }
            }
        }
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
