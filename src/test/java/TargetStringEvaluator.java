import org.burnhams.optimiser.Evaluator;
import org.burnhams.optimiser.Solution;

public class TargetStringEvaluator implements Evaluator<Character> {

    private final String targetString;

    public TargetStringEvaluator(String targetString) {
        this.targetString = targetString;
    }

    @Override
    public double evaluate(Solution<Character> t) {
        double cost = 0;
        for (int i = 0; i<targetString.length(); i++) {
            cost += Math.abs(targetString.charAt(i)-t.get(i).charValue());
        }
        return cost;
    }
}
