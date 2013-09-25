package org.burnhams.flooring.floors.wallenclosed;

import java.util.ArrayList;
import java.util.List;

public class CornerWallLength {
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

    public static CornerWallLength[] parse(String input) {
        List<CornerWallLength> result = new ArrayList<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            String[] cornerLength = part.trim().split(" ");
            result.add(new CornerWallLength(Integer.valueOf(cornerLength[1]), Corner.valueOf(cornerLength[0].toUpperCase())));
        }
        return result.toArray(new CornerWallLength[result.size()]);
    }
}
