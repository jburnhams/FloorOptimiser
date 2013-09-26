package org.burnhams.flooring;

import org.assertj.core.data.Offset;
import org.burnhams.flooring.floors.Floor;
import org.burnhams.flooring.floors.MultiLengthFloor;
import org.burnhams.flooring.floors.RectangularFloor;
import org.burnhams.flooring.floors.wallenclosed.WallEnclosedFloor;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.burnhams.flooring.floors.wallenclosed.CornerWallLength.left;
import static org.burnhams.flooring.floors.wallenclosed.CornerWallLength.right;

public class FloorSolutionTest {

    private static final int PLANK_WIDTH = 3;
    private static final int[] PLANKS = new int[]{8, 3, 6, 5, 3, 6, 2, 8, 5, 9};
    private final Floor rectangularFloor = new RectangularFloor(10, 10);

    @Test
    public void shouldCalculateRows() {
        assertThat(rectangularFloor.getArea()).isEqualTo(0.01*0.01);
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, 8);
        assertThat(solution.getRows()).isEqualTo(4);
        assertThat(solution.size()).isEqualTo(1);
        assertThat(solution.getPlankWidth()).isEqualTo(PLANK_WIDTH);
        assertThat(solution.getFloor().getWidth()).isEqualTo(10);
        assertThat(solution.getFloor().getMaxLength()).isEqualTo(10);
        assertThat(solution.getAreaLength()).isEqualTo(10);
        assertThat(solution.getAreaWidth()).isEqualTo(12);
        assertThat(solution.isEvaluated()).isFalse();
    }

    @Test
    public void shouldEvaluateNegativeSurplusLength() {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, 8);
        assertThat(solution.isEvaluated()).isFalse();
        solution.evaluate();
        assertThat(solution.isEvaluated()).isTrue();
        assertThat(solution.getSurplusLength()).isEqualTo(-40 + 8);
        assertThat(solution.getSurplusPlanks()).isEqualTo(0);
        assertThat(solution.getPlanksUsed()).isEqualTo(1);
        assertThat(solution.getRowOffsets()).isEqualTo(new int[]{0,0,0,0,0});
        assertThat(solution.getSegmentWaste()).isEmpty();
        assertThat(solution.getAreaLength()).isEqualTo(10);
        assertThat(solution.getAreaWidth()).isEqualTo(12);
        assertThat(solution.getTotalWaste()).isEqualTo(0);
    }

    @Test
    public void shouldEvaluatePositiveSurplusLength() {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, PLANKS);
        assertThat(solution.isEvaluated()).isFalse();
        assertThat(solution.getRows()).isEqualTo(4);
        solution.evaluate();
        assertThat(solution.isEvaluated()).isTrue();
        assertThat(solution.getPlanksUsed()).isEqualTo(9);
        assertThat(solution.getRowOffsets()).isEqualTo(new int[]{0, 2, 4, 7, 9});
        assertThat(solution.getSegmentWaste()).containsExactly(1,1,1,3);
        assertThat(solution.getTotalWaste()).isEqualTo(6);
        assertThat(solution.getSurplusLength()).isEqualTo(9);
        assertThat(solution.getAreaLength()).isEqualTo(13);
        assertThat(solution.getAreaWidth()).isEqualTo(12);
        assertThat(solution.getSurplusPlanks()).isEqualTo(1);
    }

    @Test
    public void shouldCreateFloorImage() throws IOException {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, PLANKS);
        solution.evaluate();
        BufferedImage image = solution.createImage(2000);
        ImageIO.write(image, "PNG", new File("testfloor.png"));
    }


    private void testRowSwap(FloorSolution solution, int row1, int row2) {
        solution.evaluate();
        FloorSolution swap1 = solution.swapRows(row1, row2);
        swap1.evaluate();
        FloorSolution swap2 = swap1.swapRows(row2, row1);
        swap2.evaluate();
        String lengths1 = solution.getLengthsList();
        String lengths2 = swap2.getLengthsList();
        assertThat(lengths2).isEqualTo(lengths1);
    }

    @Test
    public void shouldSwapRows() throws IOException {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, PLANKS);
        testRowSwap(solution, 0,2);
        testRowSwap(solution, 0,0);
        testRowSwap(solution, 0,1);
        testRowSwap(solution, 3,2);
        testRowSwap(solution, 1,2);
    }

    @Test
    public void shouldGetLengthsList() {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, PLANKS);
        solution.evaluate();
        assertThat(solution.getLengthsList()).isEqualTo("8, 3\n6, 5\n3, 6, 2\n8, 5\n9");
    }


    @Test
    public void shouldGetDistanceToClosestBelowGap() throws IOException {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, PLANKS);
        solution.evaluate();
        assertThat(solution.getDistanceToEndClosestBelowGap(1,8)).isEqualTo(2);
        assertThat(solution.getDistanceToEndClosestBelowGap(2,6)).isEqualTo(3);
        assertThat(solution.getDistanceToEndClosestBelowGap(3, 3)).isEqualTo(5);
        assertThat(solution.getDistanceToEndClosestBelowGap(3,9)).isEqualTo(1);
        assertThat(solution.getDistanceToClosestGap()).isEqualTo(1);
        assertThat(solution.getAverageDistanceToClosestGap()).isEqualTo(2.75);
        assertThat(solution.getAverageWeightedDistanceToClosestGap()).isEqualTo(28.3725, Offset.offset(0.001));
        assertThat(solution.getAverageWeightedDistanceToClosestFurtherGap()).isEqualTo(50.625);
    }

    @Test
    public void shouldUseMultiLengthFloor1() throws IOException {
        MultiLengthFloor floor = new MultiLengthFloor(2,10,6,12,2,10);
        FloorSolution solution = new FloorSolution(floor, PLANK_WIDTH, PLANKS);
        solution.evaluate();
        assertThat(solution.getSurplusLength()).isEqualTo(-1);
    }

    @Test
    public void shouldUseMultiLengthFloor2() throws IOException {
        MultiLengthFloor floor = new MultiLengthFloor(4,10,4,12,2,10);
        FloorSolution solution = new FloorSolution(floor, PLANK_WIDTH, PLANKS);
        solution.evaluate();
        BufferedImage image = solution.createImage(2000);
        ImageIO.write(image, "PNG", new File("testmultilengthfloor.png"));
    }

    @Test
    public void shouldUseWallEnclosedFloor() throws IOException {
        WallEnclosedFloor floor = new WallEnclosedFloor(10,
                right(11),
                right(3), right(5), left(4), left(4), right(3),
                right(10));

        FloorSolution solution = new FloorSolution(floor, PLANK_WIDTH, new int[]{8, 3, 6, 5, 8, 6, 2, 2, 5, 9});
        solution.evaluate();
        assertThat(solution.getTotalWaste()).isEqualTo(1 + 1 + 4 + 4 + 1 + 2);
        assertThat(solution.getSurplusLength()).isEqualTo(9);
        assertThat(solution.getSurplusPlanks()).isEqualTo(1);
        BufferedImage image = solution.createImage(2000);
        ImageIO.write(image, "PNG", new File("testwallenclosedfloorsolution.png"));
    }

    @Test
    public void shouldUseWallEnclosedFloor2() throws IOException {
        WallEnclosedFloor floor = new WallEnclosedFloor(10,
                right(11),
                right(3), right(5), left(4), left(4), right(3),
                right(10));

        FloorSolution solution = new FloorSolution(floor, PLANK_WIDTH, new int[]{8, 3, 6, 5, 8, 6});
        solution.evaluate();
        assertThat(solution.getTotalWaste()).isEqualTo(1+1+4+4);
        assertThat(solution.getSurplusLength()).isEqualTo(-3-3);
        assertThat(solution.getSurplusPlanks()).isEqualTo(0);
    }


    @Test
    public void shouldSaveAndLoad() throws IOException {
        FloorSolution solution = new FloorSolution(rectangularFloor, PLANK_WIDTH, PLANKS);
        solution.evaluate();
        String filename = "testsolution.csv";
        solution.saveToFile(filename);
        FloorSolution loaded = FloorSolution.loadFromFile(rectangularFloor, PLANK_WIDTH, filename);
        assertThat(loaded).isEqualTo(solution);
    }

}
