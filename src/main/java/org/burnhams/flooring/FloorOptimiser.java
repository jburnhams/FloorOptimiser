package org.burnhams.flooring;

import org.apache.log4j.Logger;
import org.burnhams.flooring.floors.Floor;
import org.burnhams.flooring.neighbourhood.RowSwapNeighbour;
import org.burnhams.flooring.neighbourhood.WithinRowSwapNeighbour;
import org.burnhams.optimiser.Configuration;
import org.burnhams.optimiser.algorithms.HillClimber;
import org.burnhams.optimiser.algorithms.Optimiser;
import org.burnhams.optimiser.algorithms.SimulatedAnnealing;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;
import org.burnhams.optimiser.neighbourhood.RandomSwapMultiNeighbour;
import org.burnhams.optimiser.neighbourhood.RandomSwapNeighbour;
import org.burnhams.optimiser.neighbourhood.ShuffleNeighbour;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.burnhams.optimiser.algorithms.Optimiser.getBestFromFutures;

public class FloorOptimiser {
    private static final Logger logger = Logger.getLogger(FloorOptimiser.class);

    private final PropertiesConfiguration configuration;
    private final FloorEvaluator evaluator;

    public FloorOptimiser(PropertiesConfiguration configuration, FloorEvaluator evaluator) {
        this.configuration = configuration;
        this.evaluator = evaluator;
    }


    public FloorSolution optimise() throws ExecutionException {
        Floor floor = configuration.getFloor();
        FloorSolution initialSolution = new FloorSolution(
                floor, configuration.getPlankWidth(), configuration.getPlankLengths(), configuration.getFixedPlanks()
        );
        logger.info("Floor: "+floor.getWidth()+"x"+floor.getMaxLength()+" = "+floor.getArea()+"m2");
        initialSolution.shuffle();
        initialSolution.evaluate();
        Optimiser<Plank, FloorSolution> optimiser;

        optimiser = new HillClimber<>(evaluator, configuration, 1000, 1, new ShuffleNeighbour<Plank, FloorSolution>(configuration));
        final FloorSolution shuffledSolution = optimiser.optimise(initialSolution);

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<FloorSolution>> futures = new ArrayList<>();
        for (int i = 0; i < configuration.getThreads(); i++) {
            final NeighbourhoodFunction[] neighbourhoodFunctions = createNeighbourhoodFunctions(configuration);
            futures.add(executor.submit(new Callable<FloorSolution>() {
                @Override
                public FloorSolution call() throws Exception {
                    Optimiser<Plank, FloorSolution> sa = new SimulatedAnnealing<>(evaluator, configuration, neighbourhoodFunctions);
                    return sa.optimise(shuffledSolution);
                }
            }));
        }

        FloorSolution solution = getBestFromFutures(evaluator, futures);
        executor.shutdown();

        optimiser = new HillClimber<>(evaluator, configuration, createNeighbourhoodFunctions(configuration));
        return optimiser.optimise(solution);

    }

    private static NeighbourhoodFunction[] createNeighbourhoodFunctions(Configuration configuration) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {}
        return new NeighbourhoodFunction[]{
                new RandomSwapMultiNeighbour<Plank, FloorSolution>(configuration),
                new RandomSwapNeighbour<Plank, FloorSolution>(configuration),
                new WithinRowSwapNeighbour(configuration),
                new RowSwapNeighbour(configuration)};
    }
}
