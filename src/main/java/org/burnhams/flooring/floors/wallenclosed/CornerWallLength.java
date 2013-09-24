package org.burnhams.flooring.floors.wallenclosed;

public class CornerWallLength {
    private final int length;
    private final Corner direction;

    public CornerWallLength(int length, Corner direction) {
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
