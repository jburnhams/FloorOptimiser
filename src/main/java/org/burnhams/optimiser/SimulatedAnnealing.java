package org.burnhams.optimiser;

import org.apache.log4j.Logger;

public class SimulatedAnnealing<T, U extends Solution<T>> extends Optimiser<T, U> {

    private static Logger logger = Logger.getLogger(SimulatedAnnealing.class);

    public SimulatedAnnealing(Evaluator<T, U> evaluator, Configuration configuration) {
        super(configuration, evaluator);
    }

    @Override
    public U optimise(U candidate) {
        double startingTemperature = configuration.getStartingTemperature();

        int maxIterations = configuration.getMaxIterations();
        double temperatureMultiple = Math.pow(1d/startingTemperature, 1d / maxIterations);

        double temperature = startingTemperature;
        U current = candidate;
        U best = current;
        double currentCost = evaluate(current);
        double maxCost = currentCost;
        double bestCost = currentCost;
        for (int i = 0; i < configuration.getMaxIterations(); i++) {
            U neighbour = getNeighbour(current);
            double neighbourCost = evaluate(neighbour);
            if (neighbourCost > maxCost) {
                maxCost = neighbourCost;
            }
            double d = ((currentCost - neighbourCost)/maxCost)*startingTemperature;
            double acceptance = Math.exp(d / temperature);
            double p = Math.random();
            logger.info("Iteration "+i+", Temperature "+temperature+", Current Cost "+currentCost+", Neighbour Cost "+neighbourCost+", Diff "+d+", P "+p+", Acceptance Prob "+acceptance+", Best Cost "+bestCost+", "+best);
            if (p < acceptance) {
                if (neighbourCost < bestCost) {
                    bestCost = neighbourCost;
                    best = neighbour;
                }
                currentCost = neighbourCost;
                current = neighbour;
            }
            temperature *= temperatureMultiple;
        }
        return best;
    }
}
