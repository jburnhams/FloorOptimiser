package org.burnhams.optimiser;

import org.apache.log4j.Logger;

public class SimulatedAnnealing<T, U extends Solution<T>> extends Optimiser<T, U> {

    private static Logger logger = Logger.getLogger(SimulatedAnnealing.class);

    protected SimulatedAnnealing(Configuration configuration, Evaluator<T, U> evaluator) {
        super(configuration, evaluator);
    }

    @Override
    public U optimise(U candidate) {
        double startingTemperature = configuration.getStartingTemperature();
        int maxIterations = configuration.getMaxIterations();
        double temperatureMultiple = Math.log(startingTemperature) / Math.log(maxIterations);

        double temperature = startingTemperature;
        U current = candidate;
        U best = current;
        double currentCost = evaluate(current);
        double bestCost = currentCost;
        for (int i = 0; i < configuration.getMaxIterations(); i++) {
            U neighbour = getNeighbour(current);
            double neighbourCost = evaluate(neighbour);
            if (neighbourCost < currentCost) {
                if (neighbourCost < bestCost) {
                    bestCost = neighbourCost;
                    best = neighbour;
                }
                currentCost = neighbourCost;
                current = neighbour;
            } else {
                double d = currentCost - neighbourCost;
                if (Math.random() < Math.exp(d / temperature)) {
                    currentCost = neighbourCost;
                    current = neighbour;
                }
            }
            logger.info("Iteration "+i+", Temperature "+temperature+", Current Cost "+currentCost+", Neighbour Cost "+neighbourCost+", Best Cost "+bestCost);
            temperature *= temperatureMultiple;
        }
        return best;
    }
}
