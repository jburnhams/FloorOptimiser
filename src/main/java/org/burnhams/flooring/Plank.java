package org.burnhams.flooring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plank plank = (Plank) o;

        if (length != plank.length) return false;
        if (width != plank.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + length;
        return result;
    }

    public static List<Plank> createPlanks(int width, Map<Integer, Integer> lengths) {
        List<Plank> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : lengths.entrySet()) {
            for (int i = 0; i < e.getValue(); i++) {
                result.add(new Plank(width, e.getKey()));
            }
        }
        return result;
    }

    public static List<Plank> createPlanks(int width, int... lengths) {
        List<Plank> result = new ArrayList<>(lengths.length);
        for (int length : lengths) {
            result.add(new Plank(width, length));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Plank{" +
                width +
                " x " + length +
                '}';
    }
}
