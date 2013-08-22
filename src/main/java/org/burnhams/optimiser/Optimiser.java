package org.burnhams.optimiser;

import java.util.Random;

public abstract class Optimiser<T, U extends Solution<T>> {

    protected Optimiser(Configuration configuration, Evaluator<T, U> evaluator) {
        this.configuration = configuration;
        this.evaluator = evaluator;
    }

    public abstract U optimise(U candidate);

    protected final Configuration configuration;

    private final Random random = new Random();

    private final Evaluator<T, U> evaluator;

    protected double evaluate(U solution) {
        return evaluator.evaluate(solution);
    }

    protected U getNeighbour(U candidate) {
        U result = (U)candidate.clone();
        int size = candidate.size();
        boolean swapped = false;
        while (!swapped) {
            int from = random.nextInt(size);
            int to = random.nextInt(size);
            swapped = result.swap(from, to);
        }
        return result;
    }

}
