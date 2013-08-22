package org.burnhams.flooring;

import org.burnhams.optimiser.Evaluator;

public class FloorEvaluator implements Evaluator<Plank, FloorSolution> {
    @Override
    public double evaluate(FloorSolution solution) {
        double cost = 0;
        solution.evaluate();
        cost += 100 * -Math.min(0,solution.getSurplusLength());
        cost += 0.1 * solution.getTotalWaste();
        cost -= 10 * solution.getSurplusPlanks();
        double distance = Math.max(0.5, solution.getDistanceToClosestGap());
        cost += ((double)solution.getFloorLength() / distance) * 20;
        cost += ((double)solution.getFloorLength() / solution.getAverageDistanceToClosestGap()) * 5;
        cost += solution.getSmallJoinGapCount() * 1000;
        return cost;
    }
}
