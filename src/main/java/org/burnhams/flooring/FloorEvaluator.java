package org.burnhams.flooring;

import org.burnhams.optimiser.Evaluator;

public class FloorEvaluator implements Evaluator<Plank, FloorSolution> {
    @Override
    public double evaluate(FloorSolution solution) {
        double cost = 0;
        solution.evaluate();

        cost += 100 * -Math.min(0,solution.getSurplusLength());
        cost += 1 * solution.getTotalWaste();
        cost -= 100 * solution.getSurplusPlanks();

        cost += 0.1 * solution.getAverageWeightedDistanceToClosestFurtherGap();

        cost += 10 * solution.getAverageWeightedDistanceToClosestGap();

        cost += ((double)solution.getMaxPlankLength() / Math.max(0.5, solution.getDistanceToClosestGap())) * 200;

        cost += solution.getAverageWeightedLength();
        return cost;
    }
}
