package org.burnhams.optimiser;

import org.apache.log4j.Logger;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;

public class HillClimber<T, U extends Solution<T>> extends Optimiser<T, U> {

    private static Logger logger = Logger.getLogger(HillClimber.class);

    private final int choices;

    private final int maxNonImprovingMoves;

    public HillClimber(Evaluator<T, U> evaluator, Configuration configuration, NeighbourhoodFunction<T, U>... neighbourhoodFunctions) {
        super(configuration, evaluator, neighbourhoodFunctions);
        this.choices = configuration.getHillClimbChoices();
        this.maxNonImprovingMoves = configuration.getHillClimbMaxNonImprovingMoves();
    }

    public U optimise(U candidate) {
        int run = 0;
        double cost = evaluate(candidate);
        boolean improved = false;
        int nonImprovedMoves = 0;
        while (improved || nonImprovedMoves < maxNonImprovingMoves) {
            U newBest = findBest(candidate, cost);
            nonImprovedMoves++;
            improved = false;
            if (newBest != null) {
                candidate = newBest;
                double newCost = evaluate(candidate);
                if (newCost < cost) {
                    improved = true;
                    cost = newCost;
                    nonImprovedMoves = 0;
                }
            }
            logger.info("Run: "+run+", cost: "+cost+", Solution: "+candidate);
            run++;
        }
        return candidate;
    }


    private U findBest(U candidate, double currentCost) {
        U best = null;
        double bestCost = Double.MAX_VALUE;
        for (int i = 0; i < choices; i++) {
            U neighbour = getNeighbour(candidate);
            double newCost = evaluate(neighbour);
            if (newCost < bestCost) {
                bestCost = newCost;
                best = neighbour;
            }
        }
        logger.info("Found "+bestCost+" after "+choices+" attempts. Current cost "+currentCost);
        return bestCost <= currentCost ? best : null;
    }

}
