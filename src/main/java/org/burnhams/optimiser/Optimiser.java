package org.burnhams.optimiser;

import org.apache.log4j.Logger;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;
import org.burnhams.optimiser.neighbourhood.RandomSwapNeighbour;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Future;

public abstract class  Optimiser<T, U extends Solution<T>> {

    private static Logger logger = Logger.getLogger(Optimiser.class);

    public abstract U optimise(U candidate);

    protected Optimiser(Configuration configuration, Evaluator<T, U> evaluator, NeighbourhoodFunction<T, U>[] neighbourhoodFunctions) {
        this.configuration = configuration;
        this.evaluator = evaluator;
        if (neighbourhoodFunctions == null || neighbourhoodFunctions.length == 0) {
            this.neighbourhoodFunctions = new NeighbourhoodFunction[]{new RandomSwapNeighbour(configuration)};
        } else {
            this.neighbourhoodFunctions = neighbourhoodFunctions;
        }
    }

    protected final Configuration configuration;

    private final Random random = new Random();

    private final Evaluator<T, U> evaluator;

    private final NeighbourhoodFunction<T, U>[] neighbourhoodFunctions;

    protected double evaluate(U solution) {
        return evaluator.evaluate(solution);
    }

    protected U getNeighbour(U candidate) {
        if (neighbourhoodFunctions.length == 1) {
            return neighbourhoodFunctions[0].getNeighbour(candidate);
        }
        return neighbourhoodFunctions[random.nextInt(neighbourhoodFunctions.length)].getNeighbour(candidate);
    }

    private static <U> U safeGet(Future<U> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get solution from future", e);
        }
    }

    protected U getBestFromFutures(Collection<Future<U>> futureList) {
        return getBestFromFutures(evaluator, futureList);
    }

    public static <T, U extends Solution<T>> U getBestFromFutures(Evaluator<T, U> evaluator, Collection<Future<U>> futureList) {
        safeGet(futureList.iterator().next());

        U best = null;
        double bestCost = -1;
        for (Future<U> future : futureList) {
            U solution = safeGet(future);
            double newCost = evaluator.evaluate(solution);
            if (best == null || newCost < bestCost) {
                bestCost = newCost;
                best = solution;
            }
        }
        return best;
    }

}
