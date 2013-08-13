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

}
