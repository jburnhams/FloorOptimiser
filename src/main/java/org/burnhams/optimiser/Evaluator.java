package org.burnhams.optimiser;

public interface Evaluator<U, T extends Solution<U>> {

    double evaluate(T solution);

}
