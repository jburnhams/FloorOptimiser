package org.burnhams.flooring;

import org.burnhams.optimiser.Evaluator;

public class FloorEvaluator implements Evaluator<Plank, FloorSolution> {
    @Override
    public double evaluate(FloorSolution solution) {
        double cost = 0;
        solution.evaluate();
        cost += 100 * -Math.min(0,solution.getSurplusLength());
        cost += 0.1 * solution.getTotalWaste();
        cost -= 100 * solution.getSurplusPlanks();
        cost += 10 * solution.getAverageWeightedDistanceToClosestGap();
        double distance = Math.max(0.5, solution.getDistanceToClosestGap());
        cost += ((double)solution.getMaxPlankLength() / distance) * 200;
        cost += ((double)solution.getMaxPlankLength() / solution.getAverageDistanceToClosestGap()) * 50;
        return cost;
    }
}
