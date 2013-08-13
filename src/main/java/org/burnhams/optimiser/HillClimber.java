package org.burnhams.optimiser;

import org.apache.log4j.Logger;

import java.util.Random;

public class HillClimber<T, U extends Solution<T>> {

    private static Logger logger = Logger.getLogger(HillClimber.class);

    private Random random = new Random();

    private final Evaluator<T, U> evaluator;

    private final int choices;

    private final int maxNonImprovingMoves;

    public HillClimber(Evaluator<T, U> evaluator, int choices, int maxNonImprovingMoves) {
        this.evaluator = evaluator;
        this.choices = choices;
        this.maxNonImprovingMoves = maxNonImprovingMoves;
    }

    public U optimise(U candidate) {
        int run = 0;
        candidate.shuffle();
        double cost = evaluator.evaluate(candidate);
        boolean improved = false;
        int nonImprovedMoves = 0;
        while (improved || nonImprovedMoves < maxNonImprovingMoves) {
            U newBest = findBest(candidate, cost);
            nonImprovedMoves++;
            improved = false;
            if (newBest != null) {
                candidate = newBest;
                double newCost = evaluator.evaluate(candidate);
                if (newCost < cost) {
                    improved = true;
                    cost = newCost;
                    nonImprovedMoves = 0;
                }
            }
            logger.info("Run: "+run+", cost: "+cost+", Solution: "+candidate);
            run++;
        }
        evaluator.evaluate(candidate);
        return candidate;
    }


    private U findBest(U candidate, double currentCost) {
        int size = candidate.size();
        U best = null;
        double bestCost = Double.MAX_VALUE;
        for (int i = 0; i < choices; i++) {
            int from = random.nextInt(size);
            int to = random.nextInt(size);
            if (candidate.swap(from, to)) {
                double newCost = evaluator.evaluate(candidate);
                if (newCost < bestCost) {
                    bestCost = newCost;
                    best = (U)candidate.clone();
                }
                candidate.swap(from, to);
            }
        }
        logger.info("Found "+bestCost+" after "+choices+" attempts. Current cost "+currentCost);
        return bestCost <= currentCost ? best : null;
    }

}
