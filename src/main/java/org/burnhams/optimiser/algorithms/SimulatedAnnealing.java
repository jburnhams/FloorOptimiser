package org.burnhams.optimiser.algorithms;

import org.apache.log4j.Logger;
import org.burnhams.optimiser.Configuration;
import org.burnhams.optimiser.Evaluator;
import org.burnhams.optimiser.Solution;
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

        long maxIterations = configuration.getMaxIterations();
        double temperatureMultiple = Math.pow(1d/startingTemperature, 1d / maxIterations);

        double temperature = startingTemperature;
        U current = candidate;
        U best = current;
        double currentCost = evaluate(current);
        double maxCost = currentCost;
        double bestCost = currentCost;

        long logEvery = Math.max(1, maxIterations/1000);

        for (long i = 0; i < maxIterations; i++) {
            U neighbour = getNeighbour(current);
            double neighbourCost = evaluate(neighbour);
            if (neighbourCost > maxCost) {
                maxCost = neighbourCost;
            }
            boolean accepted = isAccepted(startingTemperature, temperature, currentCost, maxCost, neighbourCost);
            if (i % logEvery == 0 || i == configuration.getMaxIterations()-1) {
                logger.info("Run: "+i+", Temp: "+twoSf(temperature)+", Current: "+twoSf(currentCost)+", Neighbour: "+twoSf(neighbourCost)+", Accepted: "+accepted+", Best: "+twoSf(bestCost)+", "+best);
            }
            if (accepted) {
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

    private boolean isAccepted(double startingTemperature, double temperature, double currentCost, double maxCost, double neighbourCost) {
        double d = ((currentCost - neighbourCost)/maxCost)*startingTemperature;
        double acceptance = Math.exp(d / temperature);
        double p = Math.random();
        return p < acceptance;
    }
}
