package org.burnhams.flooring.floors.wallenclosed;

public enum Direction {
    LEFT, UP, RIGHT, DOWN;

    public Direction turn(Corner c) {
        int num = ordinal() + 4 + (c == Corner.LEFT ? -1 : 1);
        return values()[num % 4];
    }

    public int[] getNewXY(int x, int y, int length) {
        switch (this) {
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
}
