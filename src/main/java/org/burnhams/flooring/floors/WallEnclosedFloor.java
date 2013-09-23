package org.burnhams.flooring.floors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class WallEnclosedFloor implements Floor {

    public static enum Corner {
        RIGHT,
        LEFT
    }

    public static class CornerWallLength {
        private final int length;
        private final Corner direction;

        private CornerWallLength(int length, Corner direction) {
            this.length = length;
            this.direction = direction;
        }

        public int getLength() {
            return length;
        }

        public Corner getDirection() {
            return direction;
        }

        public static CornerWallLength right(int length) {
            return new CornerWallLength(length, Corner.RIGHT);
        }

        public static CornerWallLength left(int length) {
            return new CornerWallLength(length, Corner.LEFT);
        }
    }

    static enum Direction {
        LEFT, UP, RIGHT, DOWN;

        Direction turn(Corner c) {
            int num = ordinal() + 4 + (c == Corner.LEFT ? -1 : 1);
            return values()[num % 4];
        }
    }

    private static int gcd(int a, int b)
    {
        while (b > 0)
        {
            int temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    static int gcd(List<Integer> input)
    {
        int result = input.get(0);
        for(int i = 1; i < input.size(); i++) result = gcd(result, input.get(i));
        return result;
    }

    private final int maxWidth, maxLength;
    private final int horizontalLengthOffsetX, horizontalLengthOffsetY;
    private final int horizontalLength;
    private final CornerWallLength[] lengths;
    private final int floorBitsSize;
    private final BitSet floorBits;

    private final int gcd;

    public WallEnclosedFloor(int horizontalLength, CornerWallLength... lengths) {
        this.horizontalLength = horizontalLength;
        this.lengths = lengths;
        int x = horizontalLength, y = 0;
        int minX=0, minY=0, maxX=horizontalLength, maxY=0;
        Direction direction = Direction.RIGHT;
        List<Integer> ls = new ArrayList<>();
        ls.add(horizontalLength);

        for (CornerWallLength c : lengths) {
            ls.add(c.getLength());
            direction = direction.turn(c.direction);
            int[] xy = getNewXY(x, y, direction, c.getLength());
            x=xy[0];
            y=xy[1];
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        if (x!=0 || y!=0) {
            throw new IllegalArgumentException("Lengths do not end at start: "+x+", "+y);
        }

        horizontalLengthOffsetX = -minX;
        horizontalLengthOffsetY = -minY;
        maxLength = maxX - minX;
        maxWidth = maxY - minY;

        gcd = 1;//gcd(ls);
        floorBitsSize = maxWidth/gcd * maxLength/gcd;
        floorBits = new BitSet(floorBitsSize);
        setFloorBits();
    }

    private void setFloorBit(int x, int y) {
        floorBits.set(getBitIndex(x, y));
    }

    private int getBitIndex(int x, int y) {
        return Math.min(maxWidth-1,y) * maxLength/gcd + Math.min(maxLength-1,x) / gcd;
    }

    public boolean getFloorBit(int x, int y) {
        return floorBits.get(getBitIndex(x, y));
    }


    private void setFloorBits() {
        int x = horizontalLengthOffsetX;
        int y = horizontalLengthOffsetY;
        for (;x<horizontalLengthOffsetX + horizontalLength;x+=gcd) {
            setFloorBit(x, y);
        }

        Direction direction = Direction.RIGHT;

        for (CornerWallLength length : lengths) {
            direction = direction.turn(length.direction);
            int[] xy = getNewXY(x, y, direction, length.getLength());
            int step = direction == Direction.RIGHT || direction == Direction.DOWN ? gcd : -gcd;
            if (x == xy[0]) {
                for (;y!=xy[1];y+=step) {
                    setFloorBit(x, y);
                }
            } else {
                for (;x!=xy[0];x+=step) {
                    setFloorBit(x, y);
                }
            }
        }
    }

    public int getGcd() {
        return gcd;
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

    static int[] getNewXY(int x, int y, Direction direction, int length) {
        switch (direction) {
            case LEFT:
                return new int[] {x-length,y};
            case UP:
                return new int[] {x,y-length};
            case RIGHT:
                return new int[] {x+length,y};
            case DOWN:
            default:
                return new int[] {x,y+length};

        }
    }

    @Override
    public void drawBoundary(Graphics graphics, double xMultiple, double yMultiple) {
        int currentX = horizontalLengthOffsetX;
        int currentY = horizontalLengthOffsetY;
        int nextX = horizontalLengthOffsetX + horizontalLength;
        int nextY = currentY;
        Direction direction = Direction.RIGHT;

        drawLine(graphics, xMultiple, yMultiple, currentX, currentY, nextX, nextY);

        for (CornerWallLength length : lengths) {
            direction = direction.turn(length.direction);
            currentX = nextX;
            currentY = nextY;
            int[] xy = getNewXY(currentX, currentY, direction, length.getLength());
            nextX = xy[0];
            nextY = xy[1];
            drawLine(graphics, xMultiple, yMultiple, currentX, currentY, nextX, nextY);
        }
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
