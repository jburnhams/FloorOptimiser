package org.burnhams.optimiser.neighbourhood;

import org.burnhams.optimiser.Configuration;
import org.burnhams.optimiser.Solution;

public class ShuffleNeighbour<T, U extends Solution<T>> extends NeighbourhoodFunction<T,U> {

    public ShuffleNeighbour(Configuration configuration) {
        super(configuration);
    }

    @Override
    public U getNeighbour(U candidate) {
        U result = (U)candidate.clone();
        result.shuffle();
        return result;
    }
}
