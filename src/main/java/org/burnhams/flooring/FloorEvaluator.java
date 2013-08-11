package org.burnhams.flooring;

import org.burnhams.optimiser.Evaluator;

public class FloorEvaluator implements Evaluator<Plank, FloorSolution> {
    @Override
    public double evaluate(FloorSolution solution) {
        double cost = 0;
        solution.evaluate();
        cost += 1000 * -solution.getSurplusLength();
        cost += 100 * solution.getTotalWaste();
        cost -= 10 * solution.getSurplusPlanks();
        return cost;
    }
}
