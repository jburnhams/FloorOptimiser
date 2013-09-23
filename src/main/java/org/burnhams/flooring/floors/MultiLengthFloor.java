package org.burnhams.flooring.floors;


import java.awt.*;
import java.util.Arrays;

public class MultiLengthFloor implements Floor {

    private final int totalWidth, maxLength, count;

    private final int[] widthOffsets;
    private final int[] widths;
    private final int[] lengths;

    public MultiLengthFloor(int... widthLengthPairs) {
        this(getWidths(widthLengthPairs), getLengths(widthLengthPairs));
    }

    private static int[] getWidths(int[] widthLengthPairs) {
        if (widthLengthPairs == null || widthLengthPairs.length < 2 || widthLengthPairs.length % 2 != 0) {
            throw new IllegalArgumentException("Must be pairs of width, length only. Given "+widthLengthPairs);
        }
        int[] widths = new int[widthLengthPairs.length/2];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = widthLengthPairs[i*2];
        }
        return widths;
    }

    private static int[] getLengths(int[] widthLengthPairs) {
        int[] lengths = new int[widthLengthPairs.length/2];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = widthLengthPairs[i*2+1];
        }
        return lengths;
    }

    public MultiLengthFloor(int[] inputWidths, int[] inputLengths) {
        if (inputWidths.length != inputLengths.length) {
            throw new IllegalArgumentException("Must be same number of widths as lengths. Given "+inputWidths+", "+inputLengths);
        }
        int tWidth = 0;
        int mLength = 0;
        count = inputWidths.length;
        widthOffsets = new int[count+1];
        widthOffsets[0] = 0;
        widths = Arrays.copyOf(inputWidths, count);
        lengths = Arrays.copyOf(inputLengths, count);
        for (int i = 0; i < count; i++) {
            tWidth += widths[i];
            if (lengths[i] > mLength) {
                mLength = lengths[i];
            }
            widthOffsets[i+1]=tWidth;
        }
        totalWidth = tWidth;
        maxLength = mLength;
    }

    @Override
    public int getWidth() {
        return totalWidth;
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public int getLength(int widthOffset) {
        for (int i = 0; i < count; i++) {
            if (widthOffset < widthOffsets[i+1]) {
                return lengths[i];
            }
        }
        return 0;
    }

    @Override
    public int getLength(int widthStartOffset, int widthEndOffset) {
        int length1 = 0, length2 = 0;
        for (int i = 0; i < count && (length1 == 0 || length2 ==0); i++) {
            if (length1 ==0 && widthStartOffset < widthOffsets[i+1]) {
                length1 = lengths[i];
            }
            if (length2 ==0 && widthEndOffset < widthOffsets[i+1]) {
                length2 = lengths[i];
            }
        }
        return Math.max(length1, length2);
    }

    private void drawLine(Graphics graphics, double xMultiple, double yMultiple, int x1, int y1, int x2, int y2) {
        graphics.drawLine((int)Math.round(xMultiple*x1), (int)Math.round(yMultiple*y1), (int)Math.round(xMultiple*x2), (int)Math.round(yMultiple*y2));
    }

    @Override
    public void drawBoundary(Graphics graphics, double xMultiple, double yMultiple) {
        drawLine(graphics, xMultiple, yMultiple, 0, 0, lengths[0], 0);
        drawLine(graphics, xMultiple, yMultiple, 0, 0, 0, totalWidth);
        drawLine(graphics, xMultiple, yMultiple, 0, totalWidth, lengths[count - 1], totalWidth);
        for (int i = 0; i < count; i++) {
            drawLine(graphics, xMultiple, yMultiple, lengths[i], widthOffsets[i], lengths[i], widthOffsets[i+1]);
            if (i > 0) {
                drawLine(graphics, xMultiple, yMultiple, lengths[i-1], widthOffsets[i], lengths[i], widthOffsets[i]);
            }
        }
    }
}
