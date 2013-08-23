package org.burnhams.optimiser;

import org.apache.log4j.Logger;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;

import static org.burnhams.utils.StringUtils.twoSf;

public class SimulatedAnnealing<T, U extends Solution<T>> extends Optimiser<T, U> {

    private static Logger logger = Logger.getLogger(SimulatedAnnealing.class);

    public SimulatedAnnealing(Evaluator<T, U> evaluator, Configuration configuration, NeighbourhoodFunction<T, U>... neighbourhoodFunctions) {
        super(configuration, evaluator, neighbourhoodFunctions);
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
            if (i % 100 == 0 || i == configuration.getMaxIterations()-1) {
                logger.info("Iteration "+i+", Temperature "+twoSf(temperature)+", Current Cost "+twoSf(currentCost)+", Neighbour Cost "+twoSf(neighbourCost)+", Diff "+twoSf(d)+", P "+twoSf(p)+", Acceptance Prob "+twoSf(acceptance)+", Best Cost "+twoSf(bestCost)+", "+best);
            }
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
