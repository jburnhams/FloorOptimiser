package org.burnhams.optimiser;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StringOptimiserTest {

    private static Logger logger = Logger.getLogger(StringOptimiserTest.class);

    @Test
    public void shouldRearrangeHelloWorld() {
        String target = "Hello World";
        List<Character> chars = stringToList(target);
        List<Character> correct = new ArrayList<>(chars);
        Collections.shuffle(chars);
        HillClimber<Character, Solution<Character>> hillClimber = new HillClimber<>(new TargetStringEvaluator(target), 100, 5);
        Solution<Character> solution = hillClimber.optimise(new Solution<>(chars));
        assertThat(solution.getList()).isEqualTo(correct);
    }

    private List<Character> stringToList(String input) {
        List<Character> chars = new ArrayList<>(input.length());
        for (int i = 0; i < input.length(); i++) {
            chars.add(input.charAt(i));
        }
        return chars;
    }

    @Test
    public void shouldFailToRearrangeExactly() {
        String target = RandomStringUtils.randomNumeric(10);
        String actual = RandomStringUtils.randomNumeric(10);
        List<Character> correct = stringToList(target);
        logger.info("Target: "+correct);
        List<Character> chars = stringToList(actual);
        HillClimber<Character, Solution<Character>> hillClimber = new HillClimber<>(new TargetStringEvaluator(target), 1000, 5);
        Solution<Character> solution = hillClimber.optimise(new Solution<>(chars));
        assertThat(solution.getList()).isNotEqualTo(correct);
    }


}
