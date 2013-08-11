package org.burnhams.flooring;

import java.util.ArrayList;
import java.util.List;

public class Plank {
    private final int width, length;

    public Plank(int width, int length) {
        this.width = width;
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public static List<Plank> createPlanks(int width, int... lengths) {
        List<Plank> result = new ArrayList<>(lengths.length);
        for (int length : lengths) {
            result.add(new Plank(width, length));
        }
        return result;
    }

}
