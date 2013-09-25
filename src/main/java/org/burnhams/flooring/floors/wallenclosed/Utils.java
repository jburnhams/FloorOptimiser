package org.burnhams.flooring.floors.wallenclosed;

public final class Utils {

    private Utils() {}

    public static void traceWallPoints(int x, int y, int horizontalLength, CornerWallLength[] lengths, final WallTracePoint tracer) {
        traceWalls(x,y,horizontalLength,lengths, new WallTrace() {
            @Override
            public void trace(int x1, int y1, int x2, int y2, Direction direction, int length) {
                switch (direction) {
                    case LEFT:
                        for (int x = x1; x > x2; x--) tracer.trace(x,y1, direction, x==x1);
                        break;
                    case UP:
                        for (int y = y1; y > y2; y--) tracer.trace(x1,y, direction, y==y1);
                        break;
                    case RIGHT:
                        for (int x = x1; x < x2; x++) tracer.trace(x,y1, direction, x==x1);
                        break;
                    case DOWN:
                        for (int y = y1; y < y2; y++) tracer.trace(x1,y, direction, y==y1);
                        break;
                }
            }
        });
    }

    public static int[] traceWalls(int x, int y, int horizontalLength, CornerWallLength[] lengths, WallTrace tracer) {
        Direction direction = Direction.RIGHT;
        int[] xy = trace(new int[]{x,y},direction,horizontalLength,tracer);
        for (CornerWallLength cornerWallLength : lengths) {
            direction = direction.turn(cornerWallLength.getDirection());
            xy = trace(xy, direction, cornerWallLength.getLength(), tracer);
        }
        return xy;
    }

    private static int[] trace(int[] xy, Direction direction, int length, WallTrace tracer) {
        int x = xy[0];
        int y = xy[1];
        int[] newXY = direction.getNewXY(x,y,length);
        tracer.trace(x,y,newXY[0],newXY[1],direction,length);
        return newXY;
    }

}
