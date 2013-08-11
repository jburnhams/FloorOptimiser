package org.burnhams.optimiser;

public interface Evaluator<T> {

    double evaluate(Solution<T> t);

}
