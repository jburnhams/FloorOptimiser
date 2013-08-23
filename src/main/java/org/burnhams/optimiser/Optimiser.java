package org.burnhams.optimiser;

import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;
import org.burnhams.optimiser.neighbourhood.RandomSwapNeighbour;

import java.util.Random;

public abstract class Optimiser<T, U extends Solution<T>> {

    protected Optimiser(Configuration configuration, Evaluator<T, U> evaluator, NeighbourhoodFunction<T, U>[] neighbourhoodFunctions) {
        this.configuration = configuration;
        this.evaluator = evaluator;
        if (neighbourhoodFunctions == null || neighbourhoodFunctions.length == 0) {
            this.neighbourhoodFunctions = new NeighbourhoodFunction[]{new RandomSwapNeighbour(configuration)};
        } else {
            this.neighbourhoodFunctions = neighbourhoodFunctions;
        }
    }

    public abstract U optimise(U candidate);

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

}
