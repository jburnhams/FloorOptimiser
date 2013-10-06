package org.burnhams.flooring;

import org.burnhams.flooring.floors.Floor;
import org.burnhams.optimiser.PreEvaluatable;
import org.burnhams.optimiser.Solution;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.burnhams.utils.StringUtils.twoSf;

public class FloorSolution extends Solution<Plank> implements PreEvaluatable {

    private final Floor floor;

    private final int plankWidth;

    private final int rows;
    private final int maxPlankLength;


    private int[] rowOffsets;
    private int[] plankOffsets;
    private List<Integer> segmentWaste;
    private List<Plank> segmentWastePlanks;
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

    private Map<Plank, Integer> plankTypesUsed;
    private Map<Plank, Integer> plankTypesSpare;

    public FloorSolution(FloorSolution floorSolution) {
        super(floorSolution);
        plankWidth = floorSolution.getPlankWidth();
        floor = floorSolution.getFloor();
        longestLength = floorSolution.getAreaLength();
        rows = floorSolution.getRows();
        maxPlankLength = floorSolution.getMaxPlankLength();
    }

    public FloorSolution(Floor floor, int plankWidth, int... plankLengths) {
        this(null, Plank.createPlanks(plankWidth, plankLengths), floor);
    }

    public FloorSolution(Floor floor, int plankWidth, int[] plankLengths, int... fixedPrefix) {
        this(Plank.createPlanks(plankWidth, fixedPrefix), Plank.createPlanks(plankWidth, plankLengths), floor);
    }

    public FloorSolution(Floor floor, int plankWidth, Map<Integer, Integer> plankLengths, int... fixedLengths) {
        this(Plank.createPlanks(plankWidth, fixedLengths), Plank.createPlanks(plankWidth, plankLengths), floor);
    }

    public FloorSolution(List<Plank> fixed, List<Plank> planks, Floor floor) {
        super(fixed, planks);
        this.floor = floor;
        longestLength = floor.getMaxLength();
        plankWidth = planks.get(0).getWidth();
        rows = (int)Math.ceil((double)floor.getWidth() / plankWidth);
        int maxLength = 0;
        for (Plank p : planks) {
            if (p.getLength() > maxLength) {
                maxLength = p.getLength();
            }
        }
        if (fixed != null) {
            for (Plank p : fixed) {
                if (p.getLength() > maxLength) {
                    maxLength = p.getLength();
                }
            }
        }
        maxPlankLength = maxLength;
    }

    public void evaluate() {
        if (!hasChanged) {
            return;
        }
        rowOffsets = new int[rows+1];
        plankOffsets = new int[super.totalSize()];
        segmentWaste = new ArrayList<>();
        segmentWastePlanks = new ArrayList<>();
        surplusLength = 0;
        surplusPlanks = 0;
        totalWaste = 0;
        longestLength = floor.getMaxLength();

        evaluatePlanks();
        evaluateJoinGaps();

        hasChanged = false;
    }

    private void incrementPlankMap(Map<Plank, Integer> map, Plank plank) {
        Integer existing = map.get(plank);
        map.put(plank, existing == null ? 1 : (existing+1));
    }

    private void evaluatePlanks() {
        int currentRow = 0;

        int currentRowStartOffset = 0;
        double currentRowMidOffset = 0.5d * plankWidth;
        int currentRowEndOffset = currentRowStartOffset+plankWidth;

        int currentSegment = 0;
        int currentRowSegmentCount = floor.getSegments(currentRowStartOffset, currentRowEndOffset);
        int currentSegmentStart = floor.getSegmentStart(currentRowStartOffset, currentRowEndOffset, currentSegment);
        int currentSegmentEnd = currentSegmentStart + floor.getSegmentLength(currentRowStartOffset, currentRowEndOffset, currentSegment);

        int nextPlankStart = currentSegmentStart;
        double horizontalMidpoint = 0.5d * floor.getMaxLength();
        double verticalMidpoint = 0.5d * floor.getWidth();
        double pythagMidpoint = Math.sqrt(horizontalMidpoint*horizontalMidpoint+verticalMidpoint*verticalMidpoint);

        double totalWeight = 0;

        plankTypesUsed = new HashMap<>();
        plankTypesSpare = new HashMap<>();

        for (int i = 0; i < totalSize(); i++) {
            Plank plank = get(i);
            if (currentRow >= rows) {
                surplusLength += plank.getLength();
                surplusPlanks++;
                incrementPlankMap(plankTypesSpare, plank);
            } else {
                double horizontalDistance = Math.abs(horizontalMidpoint - (nextPlankStart + 0.5 * plank.getLength()));

                double verticalDistance = Math.abs(verticalMidpoint - currentRowMidOffset);

                double pythagDistance = Math.sqrt(horizontalDistance*horizontalDistance+verticalDistance*verticalDistance);

                totalWeight += (double)plank.getLength() * plank.getLength() / maxPlankLength * pythagDistance/pythagMidpoint;

                plankOffsets[i] = nextPlankStart;

                nextPlankStart += plank.getLength();
                if (nextPlankStart > longestLength) {
                    longestLength = nextPlankStart;
                }
                incrementPlankMap(plankTypesUsed, plank);
                planksUsed++;


                if (nextPlankStart >= currentSegmentEnd) {
                    int waste = nextPlankStart - currentSegmentEnd;
                    if (currentSegment < currentRowSegmentCount - 1) {
                        int nextSegmentStart = floor.getSegmentStart(currentRowStartOffset, currentRowEndOffset, currentSegment+1);
                        if (nextPlankStart > nextSegmentStart) {
                            waste -= (nextPlankStart - nextSegmentStart);
                        }
                    }
                    segmentWaste.add(waste);
                    segmentWastePlanks.add(plank);
                    totalWaste += waste;

                    currentSegment++;

                    if (currentSegment >= currentRowSegmentCount) {
                        currentSegment = 0;
                        currentRow++;

                        currentRowMidOffset += plankWidth;
                        currentRowStartOffset += plankWidth;
                        currentRowEndOffset += plankWidth;
                        currentRowSegmentCount = floor.getSegments(currentRowStartOffset, currentRowEndOffset);
                        rowOffsets[currentRow] = i+1;
                        nextPlankStart = 0;
                    }
                    currentSegmentStart = floor.getSegmentStart(currentRowStartOffset, currentRowEndOffset, currentSegment);
                    currentSegmentEnd = currentSegmentStart + floor.getSegmentLength(currentRowStartOffset, currentRowEndOffset, currentSegment);

                    if (nextPlankStart < currentSegmentStart) {
                        nextPlankStart = currentSegmentStart;
                    }
                }
            }
        }
        for (; currentRow < rows; currentRow++) {
            currentRowSegmentCount = floor.getSegments(currentRowStartOffset, currentRowEndOffset);
            for (; currentSegment < currentRowSegmentCount; currentSegment++) {
                currentSegmentStart = floor.getSegmentStart(currentRowStartOffset, currentRowEndOffset, currentSegment);
                currentSegmentEnd = currentSegmentStart + floor.getSegmentLength(currentRowStartOffset, currentRowEndOffset, currentSegment);
                if (nextPlankStart < currentSegmentStart) {
                    nextPlankStart = currentSegmentStart;
                }
                surplusLength += nextPlankStart - currentSegmentEnd;
            }
            currentSegment = 0;
            nextPlankStart = 0;
            currentRowStartOffset += plankWidth;
            currentRowEndOffset += plankWidth;
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
            for (; plankNum < rowOffsets[row+1]-1; plankNum++) {
                int plankEnd = plankOffsets[plankNum] + get(plankNum).getLength();
                if (plankEnd == plankOffsets[plankNum+1]) {
                    int distance = getDistanceToEndClosestBelowGap(row+1, plankEnd);

                    totalJoinGap += distance;
                    double weightedGap = (double) maxPlankLength / Math.max(distance, 0.5);
                    totalWeightedJoinGap += weightedGap * weightedGap;

                    if (row < rows-2 && rowOffsets[row+3] > 0) {
                        int furtherDistance = getDistanceToEndClosestBelowGap(row+2, plankEnd);
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

    public int getRows() {
        return rows;
    }

    public boolean isEvaluated() {
        return !hasChanged;
    }

    int[] getRowOffsets() {
        return rowOffsets;
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
        if (distanceToPlankEnd >= floor.getMaxLength()) {
            throw new IllegalArgumentException("Must not be called for end plank");
        }
        int currentDistance = -1;
        for (int i = offset; i < rowOffsets[nextRow+1]-1; i++) {
            int plankEnd = plankOffsets[i] + get(i).getLength();
            int nextDistance = Math.abs(plankEnd-distanceToPlankEnd);
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
            int rowEnd = rowOffsets[i+1];
            if (rowEnd == 0) {
                rowEnd = totalSize();
            }
            for (int j = rowOffsets[i]; j < rowEnd; j++) {
                Plank p = get(j);
                double xFrom = xMultiple * plankOffsets[j];
                double xTo = xFrom + xMultiple*p.getLength();

                float plankLengthFraction = (float)p.getLength() / maxPlankLength;
                float brightness = plankLengthFraction*0.3f + 0.35f;

                graphics.setBackground(Color.getHSBColor(plankLengthFraction, 0.3f, brightness));
                graphics.clearRect((int) xFrom, yFrom, (int) xTo - (int) xFrom, yTo - yFrom);
                graphics.setColor(Color.DARK_GRAY);
                graphics.drawRect((int) xFrom, yFrom, (int) xTo - (int) xFrom, yTo - yFrom);

                graphics.setColor(Color.LIGHT_GRAY);
                drawCenteredString(String.valueOf(p.getLength()), (int)Math.round(xFrom + xMultiple*p.getLength()*0.5), (int)Math.round(yTo-plankHeight*0.5), graphics);
            }
        }
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.BLACK);
        floor.drawBoundary(graphics, xMultiple, yMultiple);
        graphics.dispose();
        return result;
    }


    private void drawCenteredString(String s, int x, int y, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, x - fm.stringWidth(s) / 2, fm.getAscent() + y - (fm.getAscent() + fm.getDescent()) / 2);
    }

    public List<Integer> getSegmentWaste() {
        return segmentWaste;
    }

    public int getFullRows() {
        int result = rows;
        while (rowOffsets[result]==0) {
            result--;
        }
        return result;
    }

    public int getUnswappableRows() {
        int fixedLength = fixedSize();
        for (int i = 0; i <= rows; i++) {
            if (fixedLength - rowOffsets[i] <= 0) {
                return i;
            }
        }
        throw new RuntimeException("Visited all rows but all fixed");
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

    public Map<Plank, Integer> getPlankTypesUsed() {
        return plankTypesUsed;
    }

    public Map<Plank, Integer> getPlankTypesSpare() {
        return plankTypesSpare;
    }

    public FloorSolution swapRows(int row1, int row2) {
        FloorSolution result = clone();
        int rowEnd2 = rowOffsets[row2 + 1];
        int rowEnd1 = rowOffsets[row1 + 1];
        if (rowEnd1 == 0 || rowEnd2 == 0) {
            throw new IllegalArgumentException("Must only swap full rows");
        }
        int fixed = fixedSize();
        result.swap(rowOffsets[row1]-fixed, rowEnd1-fixed, rowOffsets[row2]-fixed, rowEnd2-fixed);
        return result;
    }

    public String getLengthsList() {
        StringBuilder result = new StringBuilder();
        int row = 1;
        for (int i = 0; i< totalSize(); i++) {
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

    public Floor getFloor() {
        return floor;
    }

    private void writePlankTypesCount(PrintStream out, String title, Map<Plank, Integer> plankTypesSpare) {
        out.println();
        out.println(title);
        for (Map.Entry<Plank, Integer> entry : plankTypesSpare.entrySet()) {
            out.print(entry.getKey().getLength());
            out.print("=");
            out.println(entry.getValue());
        }
    }

    public void saveToFile(String filename) throws IOException {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(filename));
            out.println(this.getLengthsList());
            writePlankTypesCount(out, "Planks Used", getPlankTypesUsed());
            writePlankTypesCount(out, "Planks Spare", getPlankTypesSpare());
            out.println();
            out.println("Segment Waste");
            for (int i = 0; i < segmentWastePlanks.size(); i++) {
                out.println(segmentWastePlanks.get(i).getLength()+"="+segmentWaste.get(i));
            }
        }
        finally {
            if (out != null) out.close();
        }

    }

    public static FloorSolution loadFromFile(Floor floor, int plankWidth, String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        List<Plank> planks = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                String[] parts = line.split(",");
                for (String p : parts) {
                    int length = Integer.valueOf(p.trim());
                    planks.add(new Plank(plankWidth, length));
                }
            }
        } finally {
            br.close();
        }
        return new FloorSolution(null, planks, floor);
    }
}
