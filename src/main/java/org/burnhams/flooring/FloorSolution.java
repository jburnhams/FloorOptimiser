package org.burnhams.flooring;

import org.burnhams.optimiser.Solution;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class FloorSolution extends Solution<Plank> {

    private final int plankWidth;
    private final int floorWidth;
    private final int floorLength;
    private final int rows;


    private int[] rowOffsets;
    private int[] rowWaste;
    private int surplusPlanks;
    private int surplusLength;
    private int totalWaste;
    private int planksUsed;
    private int longestLength;

    public FloorSolution(FloorSolution floorSolution) {
        super(floorSolution);
        plankWidth = floorSolution.getPlankWidth();
        floorWidth = floorSolution.getFloorWidth();
        floorLength = floorSolution.getFloorLength();
        longestLength = floorSolution.getAreaLength();
        rows = floorSolution.getRows();
    }

    public FloorSolution(int floorWidth, int floorLength, int plankWidth, int... plankLengths) {
        this(Plank.createPlanks(plankWidth, plankLengths), floorWidth, floorLength);
    }

    public FloorSolution(int floorWidth, int floorLength, int plankWidth, Map<Integer, Integer> plankLengths) {
        this(Plank.createPlanks(plankWidth, plankLengths), floorWidth, floorLength);
    }

    public FloorSolution(List<Plank> planks, int floorWidth, int floorLength) {
        super(planks);
        this.floorLength = floorLength;
        this.floorWidth = floorWidth;
        longestLength = floorLength;
        plankWidth = planks.get(0).getWidth();
        rows = (int)Math.ceil((double)floorWidth / plankWidth);
    }

    public void evaluate() {
        if (!hasChanged) {
            return;
        }
        rowOffsets = new int[rows+1];
        rowWaste = new int[rows];
        int currentRow = 0;
        int currentRowLength = 0;
        surplusLength = 0;
        surplusPlanks = 0;
        totalWaste = 0;
        longestLength = floorLength;
        for (int i = 0; i < size(); i++) {
            Plank plank = get(i);
            if (currentRow >= rows) {
                surplusLength += plank.getLength();
                surplusPlanks++;
            } else {
                currentRowLength += plank.getLength();
                if (currentRowLength > longestLength) {
                    longestLength = currentRowLength;
                }
                planksUsed++;
                if (currentRowLength >= this.floorLength) {
                    int waste = currentRowLength - floorLength;
                    rowWaste[currentRow] += waste;
                    totalWaste += waste;
                    currentRow++;
                    rowOffsets[currentRow] = i+1;
                    currentRowLength = 0;
                }
            }
        }
        if (currentRow < rows) {
            for (; currentRow < rows; currentRow++) {
                surplusLength += currentRowLength - floorLength;
                currentRowLength = 0;
            }
        }
        hasChanged = false;
    }

    public int getPlankWidth() {
        return plankWidth;
    }

    public int getFloorWidth() {
        return floorWidth;
    }

    public int getFloorLength() {
        return floorLength;
    }

    public int getRows() {
        return rows;
    }

    public boolean isEvaluated() {
        return !hasChanged;
    }

    public int[] getRowOffsets() {
        return rowOffsets;
    }

    public int[] getRowWaste() {
        return rowWaste;
    }

    public int getSurplusPlanks() {
        return surplusPlanks;
    }

    public int getSurplusLength() {
        return surplusLength;
    }

    public int getTotalWaste() {
        return totalWaste;
    }

    public int getPlanksUsed() {
        return planksUsed;
    }

    public int getAreaWidth() {
        return rows * plankWidth;
    }

    public int getAreaLength() {
        return longestLength;
    }

    @Override
    public String toString() {
        return "FloorSolution{" +
                "surplusPlanks=" + surplusPlanks +
                ", totalWaste=" + totalWaste +
                ", planksUsed=" + planksUsed +
                ", surplusLength=" + surplusLength +
                '}';
    }

    @Override
    public FloorSolution clone() {
        return new FloorSolution(this);
    }

    public BufferedImage createImage(int width) {
        double xMultiple = (double)width/getAreaLength();
        int height = (int)Math.round(((double)getAreaWidth()/getAreaLength())*width);
        double yMultiple = (double)height/getAreaWidth();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = result.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setColor(Color.GRAY);
        graphics.setStroke(new BasicStroke(4));
        graphics.clearRect(0, 0, width, height);
        double plankHeight = yMultiple * plankWidth;
        for (int i = 0; i < rows; i++) {
            int yFrom = (int)Math.round(plankHeight * i);
            int yTo = (int)Math.round(plankHeight * i + plankHeight);
            double xFrom = 0;
            for (int j = rowOffsets[i]; j < rowOffsets[i+1]; j++) {
                Plank p = get(j);
                double xTo = xFrom + xMultiple*p.getLength();
                graphics.drawRect((int) xFrom, yFrom, (int) xTo - (int) xFrom, yTo - yFrom);
                xFrom=xTo;
            }
        }
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0,0,(int)Math.round(xMultiple*floorLength), (int)Math.round(yMultiple*floorWidth));
        graphics.dispose();
        return result;
    }

}
