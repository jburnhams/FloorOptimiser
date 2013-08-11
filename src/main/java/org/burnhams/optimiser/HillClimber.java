package org.burnhams.optimiser;

import org.apache.log4j.Logger;

import java.util.Random;

public class HillClimber<T> {

    private static Logger logger = Logger.getLogger(HillClimber.class);

    private Random random = new Random();

    private final Evaluator<T> evaluator;

    private final int choices;

    public HillClimber(Evaluator<T> evaluator, int choices) {
        this.evaluator = evaluator;
        this.choices = choices;
    }

    public Solution<T> optimise(Solution<T> candidate) {
        int run = 0;
        double cost = evaluator.evaluate(candidate);
        boolean improved = true;
        while (improved) {
            Solution<T> newBest = findBest(candidate, cost);
            if (newBest == null) {
                improved = false;
            } else {
                candidate = newBest;
                cost = evaluator.evaluate(candidate);
            }
            logger.info("Run: "+run+", cost: "+cost+", Solution: "+candidate);
            run++;
        }
        return candidate;
    }


    private Solution<T> findBest(Solution<T> candidate, double currentCost) {
        int size = candidate.size();
        Solution<T> best = null;
        double bestCost = currentCost;
        for (int i = 0; i < choices; i++) {
            int from = random.nextInt(size);
            int to = random.nextInt(size);
            candidate.swap(from, to);
            double newCost = evaluator.evaluate(candidate);
            if (newCost < bestCost) {
                bestCost = newCost;
                best = new Solution<>(candidate);
            }
            candidate.swap(from, to);
        }
        return best;
    }

}
