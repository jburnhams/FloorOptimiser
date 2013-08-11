package org.burnhams.optimiser;

public class TargetStringEvaluator implements Evaluator<Character> {

    private final String targetString;

    public TargetStringEvaluator(String targetString) {
        this.targetString = targetString;
    }

    @Override
    public double evaluate(Solution<Character> t) {
        double cost = 0;
        for (int i = 0; i<targetString.length(); i++) {
            cost += Math.abs(targetString.charAt(i)-t.get(i));
        }
        return cost;
    }
}
