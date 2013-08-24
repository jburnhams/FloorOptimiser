package org.burnhams.optimiser;

import org.apache.log4j.Logger;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.burnhams.utils.StringUtils.twoSf;

public class HillClimber<T, U extends Solution<T>> extends Optimiser<T, U> {

    private static Logger logger = Logger.getLogger(HillClimber.class);

    private final int choices;

    private final int maxNonImprovingMoves;

    private final int threads;

    private ExecutorService executorService;

    public HillClimber(Evaluator<T, U> evaluator, Configuration configuration, int choices, int maxNonImprovingMoves, NeighbourhoodFunction<T, U>... neighbourhoodFunctions) {
        super(configuration, evaluator, neighbourhoodFunctions);
        this.choices = choices;
        this.maxNonImprovingMoves = maxNonImprovingMoves;
        threads = configuration.getThreads();
    }

    public HillClimber(Evaluator<T, U> evaluator, Configuration configuration, NeighbourhoodFunction<T, U>... neighbourhoodFunctions) {
        this(evaluator, configuration, configuration.getHillClimbChoices(), configuration.getHillClimbMaxNonImprovingMoves(), neighbourhoodFunctions);
    }

    public U optimise(U candidate) {
        executorService = Executors.newFixedThreadPool(threads);
        int run = 0;
        double cost = evaluate(candidate);
        boolean improved = false;
        int nonImprovedMoves = 0;
        while (improved || nonImprovedMoves < maxNonImprovingMoves) {
            U newBest = threads > 1 ? findBestMultiThreaded(run, candidate, cost) : findBestSingleThreaded(run, candidate, cost);
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
            run++;
        }
        executorService.shutdown();
        return candidate;
    }


    private U findBestMultiThreaded(int run, final U candidate, double currentCost) {
       final boolean preEvaluate = candidate instanceof PreEvaluatable;
        List<Future<U>> futures = new ArrayList<>(choices);
        Callable<U> callable = new Callable<U>() {
            @Override
            public U call() throws Exception {
                U neighbour = getNeighbour(candidate);
                if (preEvaluate) {
                    ((PreEvaluatable)neighbour).preEvaluate();
                }
                return neighbour;
            }
        };
        for (int i = 0; i < choices; i++) {
            futures.add(executorService.submit(callable));
        }
        U best = getBestFromFutures(futures);
        double bestCost = evaluate(best);
        logger.info("Run: "+run+", neighbour cost: "+twoSf(bestCost)+", current: "+twoSf(currentCost)+", Solution: "+candidate);
        return bestCost <= currentCost ? best : null;
    }

    private U findBestSingleThreaded(int run, U candidate, double currentCost) {
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
        logger.debug("Run: " + run + ", neighbour cost: " + twoSf(bestCost) + ", current: " + twoSf(currentCost) + ", Solution: " + candidate);
        return bestCost <= currentCost ? best : null;
    }

}
