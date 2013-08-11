import org.burnhams.optimiser.HillClimber;
import org.burnhams.optimiser.Solution;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StringOptimiserTest {

    @Test
    public void shouldRearrangeHelloWorld() {
        String target = "Hello World";
        List<Character> chars = new ArrayList<>(target.length());
        for (int i = 0; i < target.length(); i++) {
            chars.add(target.charAt(i));
        }
        List<Character> correct = new ArrayList<>(chars);
        Collections.shuffle(chars);
        HillClimber<Character> hillClimber = new HillClimber<>(new TargetStringEvaluator(target), 50);
        Solution<Character> solution = hillClimber.optimise(new Solution<>(chars));
        assertThat(solution.getList()).isEqualTo(correct);
    }


}
