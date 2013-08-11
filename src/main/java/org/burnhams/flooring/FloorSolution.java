package org.burnhams.flooring;

import org.burnhams.optimiser.Solution;

import java.util.List;

public class FloorSolution extends Solution<Plank> {

    private final int plankWidth;
    private final int floorWidth;
    private final int floorLength;
    private final int rows;


    private boolean evaluated = false;
    private int[] rowOffsets;
    private int[] rowWaste;
    private int surplusPlanks;
    private int surplusLength;
    private int totalWaste;
    private int planksUsed;

    public FloorSolution(int floorWidth, int floorLength, int plankWidth, int... plankLengths) {
        this(Plank.createPlanks(plankWidth, plankLengths), floorWidth, floorLength);

    }

    public FloorSolution(List<Plank> planks, int floorWidth, int floorLength) {
        super(planks);
        this.floorLength = floorLength;
        this.floorWidth = floorWidth;
        plankWidth = planks.get(0).getWidth();
        rows = (int)Math.ceil((double)floorWidth / plankWidth);
    }

    public void evaluate() {
        if (evaluated) {
            return;
        }
        rowOffsets = new int[rows+1];
        rowWaste = new int[rows];
        int currentRow = 0;
        int currentRowLength = 0;
        surplusLength = 0;
        surplusPlanks = 0;
        for (int i = 0; i < size(); i++) {
            Plank plank = get(i);
            if (currentRow >= rows) {
                surplusLength += plank.getLength();
                surplusPlanks++;
            } else {
                currentRowLength += plank.getLength();
                planksUsed++;
                if (currentRowLength > this.floorLength) {
                    rowOffsets[currentRow] = i+1;
                    int waste = currentRowLength - floorLength;
                    rowWaste[currentRow] += waste;
                    totalWaste += waste;
                    currentRow++;
                }
            }
        }
        if (currentRow < rows) {
            for (; currentRow < rows; currentRow++) {
                surplusLength += currentRowLength - floorLength;
                currentRowLength = 0;
            }
        }
        evaluated = true;
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
        return evaluated;
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

    public void swap(int index1, int index2) {
        super.swap(index1, index2);
        evaluated = false;
    }

}
