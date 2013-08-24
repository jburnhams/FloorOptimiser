package org.burnhams.flooring;

import org.burnhams.optimiser.PreEvaluatable;
import org.burnhams.optimiser.Solution;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import static org.burnhams.utils.StringUtils.twoSf;

public class FloorSolution extends Solution<Plank> implements PreEvaluatable {

    private final int plankWidth;
    private final int floorWidth;
    private final int floorLength;
    private final int rows;
    private final int maxPlankLength;


    private int[] rowOffsets;
    private int[] rowWaste;
    private int surplusPlanks;
    private int surplusLength;
    private int totalWaste;
    private int planksUsed;
    private int longestLength;

    private int minJoinGap;
    private int minJoinGapCount;
    private double averageJoinGap;
    private double averageWeightedJoinGap;
    private double averageWeightedFurtherJoinGap;

    private double averageWeightedLength;

    public FloorSolution(FloorSolution floorSolution) {
        super(floorSolution);
        plankWidth = floorSolution.getPlankWidth();
        floorWidth = floorSolution.getFloorWidth();
        floorLength = floorSolution.getFloorLength();
        longestLength = floorSolution.getAreaLength();
        rows = floorSolution.getRows();
        maxPlankLength = floorSolution.getMaxPlankLength();
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
        int maxLength = 0;
        for (Plank p : planks) {
            if (p.getLength() > maxLength) {
                maxLength = p.getLength();
            }
        }
        maxPlankLength = maxLength;
    }

    public void evaluate() {
        if (!hasChanged) {
            return;
        }
        rowOffsets = new int[rows+1];
        rowWaste = new int[rows];
        surplusLength = 0;
        surplusPlanks = 0;
        totalWaste = 0;
        longestLength = floorLength;

        evaluatePlanks();
        evaluateJoinGaps();

        hasChanged = false;
    }

    private void evaluatePlanks() {
        int currentRow = 0;
        double currentRowMidPoint = 0.5 * plankWidth;
        int currentRowLength = 0;
        double horizontalMidpoint = floorLength / 2;
        double verticalMidpoint = floorWidth / 2;
        double pythagMidpoint = Math.sqrt(horizontalMidpoint*horizontalMidpoint+verticalMidpoint*verticalMidpoint);

        double totalWeight = 0;

        for (int i = 0; i < size(); i++) {
            Plank plank = get(i);
            if (currentRow >= rows) {
                surplusLength += plank.getLength();
                surplusPlanks++;
            } else {
                double horizontalDistance = Math.abs(horizontalMidpoint - (currentRowLength + 0.5 * plank.getLength()));

                double verticalDistance = Math.abs(verticalMidpoint - currentRowMidPoint);

                double pythagDistance = Math.sqrt(horizontalDistance*horizontalDistance+verticalDistance*verticalDistance);

                totalWeight += plank.getLength() * plank.getLength() / maxPlankLength * pythagDistance/pythagMidpoint;

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
                    currentRowMidPoint += plankWidth;
                    rowOffsets[currentRow] = i+1;
                    currentRowLength = 0;
                }
            }
        }
        for (; currentRow < rows; currentRow++) {
            surplusLength += currentRowLength - floorLength;
            currentRowLength = 0;
        }

        averageWeightedLength = totalWeight / planksUsed;
    }

    private void evaluateJoinGaps() {
        int plankNum = 0;
        minJoinGap = Integer.MAX_VALUE;
        double totalWeightedJoinGap = 0;
        double totalWeightedFurtherJoinGap = 0;
        int furtherCount = 0;

        long totalJoinGap = 0;
        int count = 0;
        for (int row = 0; row < rows-1 && rowOffsets[row+2] > 0; row++) {
            int rowDistance = 0;
            for (; plankNum < rowOffsets[row+1]-1; plankNum++) {
                rowDistance += get(plankNum).getLength();
                int distance = getDistanceToEndClosestBelowGap(row+1, rowDistance);

                totalJoinGap += distance;
                double weightedGap = (double) maxPlankLength / Math.max(distance, 0.5);
                totalWeightedJoinGap += weightedGap * weightedGap;

                if (row < rows-2 && rowOffsets[row+3] > 0) {
                    int furtherDistance = getDistanceToEndClosestBelowGap(row+2, rowDistance);
                    weightedGap = (double) maxPlankLength / Math.max(furtherDistance, 0.5);
                    totalWeightedFurtherJoinGap += weightedGap * weightedGap;
                    furtherCount++;
                }

                count++;
                if (distance < minJoinGap) {
                    minJoinGap = distance;
                    minJoinGapCount = 1;
                } else if (distance == minJoinGap) {
                    minJoinGapCount++;
                }
            }
            plankNum++;
        }
        averageJoinGap = (double)totalJoinGap / count;
        averageWeightedJoinGap = totalWeightedJoinGap / count;
        averageWeightedFurtherJoinGap = totalWeightedFurtherJoinGap / furtherCount;
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

    int[] getRowOffsets() {
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
        return "FS{" +
                "surpPs=" + surplusPlanks +
                ", waste=" + totalWaste +
                ", planks=" + planksUsed +
                ", surpL=" + surplusLength +
                ", wL="+twoSf(averageWeightedLength)+
                ", minGap=" + minJoinGap +
                ", minGapC=" + minJoinGapCount +
                ", avWGap=" + twoSf(averageWeightedJoinGap) +
                ", avGap=" + twoSf(averageJoinGap) +
                ", avWFGap=" + twoSf(averageWeightedFurtherJoinGap) +
                '}';
    }

    @Override
    public FloorSolution clone() {
        return new FloorSolution(this);
    }

    public int getDistanceToClosestGap() {
        return minJoinGap;
    }

    public int getDistanceToClosestGapCount() {
        return minJoinGapCount;
    }

    public double getAverageWeightedLength() {
        return averageWeightedLength;
    }

    public double getAverageDistanceToClosestGap() {
        return averageJoinGap;
    }

    public double getAverageWeightedDistanceToClosestGap() {
        return averageWeightedJoinGap;
    }

    public double getAverageWeightedDistanceToClosestFurtherGap() {
        return averageWeightedFurtherJoinGap;
    }


    public int getDistanceToEndClosestBelowGap(int nextRow, int distanceToPlankEnd) {
        if (nextRow >= rows) {
            throw new IllegalArgumentException("Must not be called for end row");
        }
        if (rowOffsets[nextRow+1]==0) {
            throw new IllegalArgumentException("Must not be called for last complete row");
        }
        int offset = rowOffsets[nextRow];
        if (distanceToPlankEnd >= floorLength) {
            throw new IllegalArgumentException("Must not be called for end plank");
        }
        int currentDistance = -1;
        int nextPlankEnd = 0;
        for (int i = offset; i < rowOffsets[nextRow+1]-1; i++) {
            nextPlankEnd += get(i).getLength();
            int nextDistance = Math.abs(nextPlankEnd-distanceToPlankEnd);
            if (currentDistance >= 0 && nextDistance > currentDistance) {
                return currentDistance;
            } else {
                currentDistance = nextDistance;
            }
        }
        return currentDistance;
    }

    public BufferedImage createImage(int width) {
        double xMultiple = (double)width/getAreaLength();
        int height = (int)Math.round(((double)getAreaWidth()/getAreaLength())*width);
        double yMultiple = (double)height/getAreaWidth();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = result.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setStroke(new BasicStroke(4));
        graphics.clearRect(0, 0, width, height);
        double plankHeight = yMultiple * plankWidth;
        graphics.setFont(new Font("Arial", Font.PLAIN, (int)Math.round(plankHeight*0.95)));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < rows && (i == 0 || rowOffsets[i]>0); i++) {
            int yFrom = (int)Math.round(plankHeight * i);
            int yTo = (int)Math.round(plankHeight * i + plankHeight);
            double xFrom = 0;
            int rowEnd = rowOffsets[i+1];
            if (rowEnd == 0) {
                rowEnd = size();
            }
            for (int j = rowOffsets[i]; j < rowEnd; j++) {
                Plank p = get(j);
                double xTo = xFrom + xMultiple*p.getLength();

                float plankLengthFraction = (float)p.getLength() / maxPlankLength;
                float brightness = plankLengthFraction*0.3f + 0.35f;

                graphics.setBackground(Color.getHSBColor(plankLengthFraction, 0.3f, brightness));
                graphics.clearRect((int) xFrom, yFrom, (int) xTo - (int) xFrom, yTo - yFrom);
                graphics.setColor(Color.DARK_GRAY);
                graphics.drawRect((int) xFrom, yFrom, (int) xTo - (int) xFrom, yTo - yFrom);

                graphics.setColor(Color.LIGHT_GRAY);
                drawCenteredString(String.valueOf(p.getLength()), (int)Math.round(xFrom + xMultiple*p.getLength()*0.5), (int)Math.round(yTo-plankHeight*0.5), graphics);
                xFrom=xTo;
            }
        }
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0,0,(int)Math.round(xMultiple*floorLength), (int)Math.round(yMultiple*floorWidth));
        graphics.dispose();
        return result;
    }


    private void drawCenteredString(String s, int x, int y, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, x - fm.stringWidth(s) / 2, fm.getAscent() + y - (fm.getAscent() + fm.getDescent()) / 2);
    }


    public int getFullRows() {
        int result = rows;
        while (rowOffsets[result]==0) {
            result--;
        }
        return result;
    }

    public int getRowStart(int row) {
        return rowOffsets[row];
    }

    public int getRowEnd(int row) {
        return rowOffsets[row+1];
    }

    public int getMaxPlankLength() {
        return maxPlankLength;
    }

    public FloorSolution swapRows(int row1, int row2) {
        FloorSolution result = clone();
        int rowEnd2 = rowOffsets[row2 + 1];
        int rowEnd1 = rowOffsets[row1 + 1];
        if (rowEnd1 == 0 || rowEnd2 == 0) {
            throw new IllegalArgumentException("Must only swap full rows");
        }
        result.swap(rowOffsets[row1], rowEnd1, rowOffsets[row2], rowEnd2);
        return result;
    }

    public String getLengthsList() {
        StringBuilder result = new StringBuilder();
        int row = 1;
        for (int i = 0; i<size(); i++) {
            if (row < rowOffsets.length && i==rowOffsets[row]) {
                result.append("\n");
                row++;
            } else if (i > 0) {
                result.append(", ");
            }
            result.append(get(i).getLength());
        }
        return result.toString();
    }

    @Override
    public void preEvaluate() {
        evaluate();
    }
}
