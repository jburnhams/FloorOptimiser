package org.burnhams.optimiser;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.burnhams.optimiser.StringOptimiserTest.stringToList;

public class SolutionTest {

    @Test
    public void shouldCreateSolution() {
        Solution<String> solution1 = new Solution<>("hello", "world");
        Solution<String> solution2 = new Solution<>(newArrayList("hello", "world"));
        assertThat(solution1).isEqualTo(solution2);
        Solution<String> solution3 = new Solution<>(solution2);
        assertThat(solution3).isEqualTo(solution2);
    }


    @Test
    public void shouldSwapElements() {
        Solution<String> solution1 = new Solution<>("hello", "world");
        Solution<String> solution2 = new Solution<>(newArrayList("hello", "world"));
        solution2.swap(0,1);
        assertThat(solution1).isNotEqualTo(solution2);
        solution2.swap(1,0);
        assertThat(solution1).isEqualTo(solution2);
    }

    @Test
    public void shouldSwapRange() {
        Solution<Character> solution1 = new Solution<>(stringToList("hello world!"));
        Solution<Character> solution2 = new Solution<>(stringToList("world! hello"));
        solution1.swap(0,5,6,12);
        assertThat(solution1).isEqualTo(solution2);
    }




}
