package org.burnhams.flooring;

import org.burnhams.optimiser.Evaluator;

public class FloorEvaluator implements Evaluator<Plank, FloorSolution> {
    @Override
    public double evaluate(FloorSolution solution) {
        double cost = 0;
        solution.evaluate();
        cost += 1 * -solution.getSurplusLength();
        cost += 1 * solution.getTotalWaste();
        cost -= 10000 * solution.getSurplusPlanks();
        return cost;
    }
}
