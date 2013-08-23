package org.burnhams.optimiser;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StringOptimiserTest {

    public static final String HELLO_WORLD = "Hello World";
    private static Logger logger = Logger.getLogger(StringOptimiserTest.class);

    private Configuration configuration = new Configuration() {
        @Override
        public int getMaxIterations() {
            return 1000;
        }

        public double getStartingTemperature() {
            return 100;
        }

        @Override
        public int getHillClimbChoices() {
            return 100;
        }

        @Override
        public int getHillClimbMaxNonImprovingMoves() {
            return 5;
        }
    };

    @Test
    public void shouldRearrangeHelloWorldUsingHillClimber() {
        HillClimber<Character, Solution<Character>> hillClimber = new HillClimber<>(new TargetStringEvaluator(HELLO_WORLD), configuration);
        shouldRearrangeHelloWorld(hillClimber);
    }

    @Test
    public void shouldRearrangeHelloWorldUsingSimulatedAnnealing() {
        SimulatedAnnealing<Character, Solution<Character>> sa = new SimulatedAnnealing<>(new TargetStringEvaluator(HELLO_WORLD), configuration);
        shouldRearrangeHelloWorld(sa);
    }

    public void shouldRearrangeHelloWorld(Optimiser<Character, Solution<Character>> optimiser) {
        String target = HELLO_WORLD;
        List<Character> chars = stringToList(target);
        List<Character> correct = new ArrayList<>(chars);
        Collections.shuffle(chars);
        Solution<Character> solution = optimiser.optimise(new Solution<>(chars));
        assertThat(solution.getList()).isEqualTo(correct);
    }

    public static List<Character> stringToList(String input) {
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
        HillClimber<Character, Solution<Character>> hillClimber = new HillClimber<>(new TargetStringEvaluator(target), configuration);
        Solution<Character> solution = hillClimber.optimise(new Solution<>(chars));
        assertThat(solution.getList()).isNotEqualTo(correct);
    }


}
