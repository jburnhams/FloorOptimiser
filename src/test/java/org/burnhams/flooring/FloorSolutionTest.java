package org.burnhams.flooring;

import org.junit.Test;

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
        assertThat(solution.isEvaluated()).isFalse();
    }

    @Test
    public void shouldEvaluateNegativeSurplusLength() {
        FloorSolution solution = new FloorSolution(10, 10, 3, 8);
        assertThat(solution.isEvaluated()).isFalse();
        solution.evaluate();
        assertThat(solution.isEvaluated()).isTrue();
        assertThat(solution.getSurplusLength()).isEqualTo(-40+8);
        assertThat(solution.getSurplusPlanks()).isEqualTo(0);
        assertThat(solution.getPlanksUsed()).isEqualTo(1);
        assertThat(solution.getRowOffsets()).isEqualTo(new int[]{0,0,0,0,0});
        assertThat(solution.getRowWaste()).isEqualTo(new int[]{0,0,0,0});
        assertThat(solution.getTotalWaste()).isEqualTo(0);
    }

}
