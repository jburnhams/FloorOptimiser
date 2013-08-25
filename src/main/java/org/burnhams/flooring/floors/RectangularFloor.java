package org.burnhams.flooring.floors;

import java.awt.*;

public class RectangularFloor implements Floor {

    private final int width, length;

    public RectangularFloor(int width, int length) {
        this.width = width;
        this.length = length;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getMaxLength() {
        return length;
    }

    @Override
    public int getLength(int widthOffset) {
        return length;
    }

    @Override
    public int getLength(int widthStartOffset, int widthEndOffset) {
        return length;
    }

    @Override
    public void drawBoundary(Graphics graphics, double xMultiple, double yMultiple) {
        graphics.drawRect(0,0,(int)Math.round(xMultiple*length), (int)Math.round(yMultiple*width));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RectangularFloor that = (RectangularFloor) o;

        if (length != that.length) return false;
        if (width != that.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + length;
        return result;
    }

    @Override
    public String toString() {
        return "RectangularFloor{" +
                "length=" + length +
                ", width=" + width +
                '}';
    }
}
