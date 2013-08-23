package org.burnhams.flooring;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FloorSolutionTest {

    @Test
    public void shouldCalculateRows() {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8);
        assertThat(solution.getRows()).isEqualTo(4);
        assertThat(solution.size()).isEqualTo(1);
        assertThat(solution.getPlankWidth()).isEqualTo(3);
        assertThat(solution.getFloorLength()).isEqualTo(10);
        assertThat(solution.getFloorWidth()).isEqualTo(10);
        assertThat(solution.getAreaLength()).isEqualTo(10);
        assertThat(solution.getAreaWidth()).isEqualTo(12);
        assertThat(solution.isEvaluated()).isFalse();
    }

    @Test
    public void shouldEvaluateNegativeSurplusLength() {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8);
        assertThat(solution.isEvaluated()).isFalse();
        solution.evaluate();
        assertThat(solution.isEvaluated()).isTrue();
        assertThat(solution.getSurplusLength()).isEqualTo(-40 + 8);
        assertThat(solution.getSurplusPlanks()).isEqualTo(0);
        assertThat(solution.getPlanksUsed()).isEqualTo(1);
        assertThat(solution.getRowOffsets()).isEqualTo(new int[]{0,0,0,0,0});
        assertThat(solution.getRowWaste()).isEqualTo(new int[]{0,0,0,0});
        assertThat(solution.getAreaLength()).isEqualTo(10);
        assertThat(solution.getAreaWidth()).isEqualTo(12);
        assertThat(solution.getTotalWaste()).isEqualTo(0);
    }

    @Test
    public void shouldEvaluatePositiveSurplusLength() {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8,3, 6,5, 3,6,2, 8,2 ,9);
        assertThat(solution.isEvaluated()).isFalse();
        assertThat(solution.getRows()).isEqualTo(4);
        solution.evaluate();
        assertThat(solution.isEvaluated()).isTrue();
        assertThat(solution.getPlanksUsed()).isEqualTo(9);
        assertThat(solution.getRowOffsets()).isEqualTo(new int[]{0, 2, 4, 7, 9});
        assertThat(solution.getRowWaste()).isEqualTo(new int[]{1,1,1,0});
        assertThat(solution.getTotalWaste()).isEqualTo(3);
        assertThat(solution.getSurplusLength()).isEqualTo(9);
        assertThat(solution.getAreaLength()).isEqualTo(11);
        assertThat(solution.getAreaWidth()).isEqualTo(12);
        assertThat(solution.getSurplusPlanks()).isEqualTo(1);
    }

    @Test
    public void shouldCreateFloorImage() throws IOException {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8,3, 6,5, 3,6,2, 8,5 ,9);
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
        FloorSolution solution = new FloorSolution(10, 10, 3, 8,3, 6,5, 3,6,2, 8,5 ,9);
        testRowSwap(solution, 0,2);
        testRowSwap(solution, 0,0);
        testRowSwap(solution, 0,1);
        testRowSwap(solution, 3,2);
        testRowSwap(solution, 1,2);
    }

    @Test
    public void shouldGetLengthsList() {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8,3, 6,5, 3,6,2, 8,5 ,9);
        solution.evaluate();
        assertThat(solution.getLengthsList()).isEqualTo("8, 3\n6, 5\n3, 6, 2\n8, 5\n9");
    }


    @Test
    public void shouldGetDistanceToClosestBelowGap() throws IOException {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8,3, 6,5, 3,6,2, 8,5 ,9);
        solution.evaluate();
        assertThat(solution.getDistanceToEndClosestBelowGap(0,8)).isEqualTo(2);
        assertThat(solution.getDistanceToEndClosestBelowGap(1,6)).isEqualTo(3);
        assertThat(solution.getDistanceToEndClosestBelowGap(2,3)).isEqualTo(5);
        assertThat(solution.getDistanceToEndClosestBelowGap(2,9)).isEqualTo(1);
        assertThat(solution.getDistanceToClosestGap()).isEqualTo(1);
        assertThat(solution.getAverageDistanceToClosestGap()).isEqualTo(2.75);
    }

}
