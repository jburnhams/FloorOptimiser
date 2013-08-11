package org.burnhams.flooring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FloorEvaluatorTest {

    private final FloorEvaluator floorEvaluator = new FloorEvaluator();

    @Mock
    private FloorSolution floorSolution;

    @Test
    public void shouldThinkNegativeSurplusIsWorseThanWaste() {
        given(floorSolution.getTotalWaste()).willReturn(0);
        given(floorSolution.getSurplusLength()).willReturn(-10);
        double negativeSurplusCost = floorEvaluator.evaluate(floorSolution);
        given(floorSolution.getTotalWaste()).willReturn(10);
        given(floorSolution.getSurplusLength()).willReturn(0);
        double wasteCost = floorEvaluator.evaluate(floorSolution);
        assertThat(negativeSurplusCost).isGreaterThan(wasteCost);
    }


}
