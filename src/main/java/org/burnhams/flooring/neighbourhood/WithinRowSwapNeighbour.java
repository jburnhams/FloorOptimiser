package org.burnhams.flooring.neighbourhood;

import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.Plank;
import org.burnhams.optimiser.Configuration;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;

public class WithinRowSwapNeighbour extends NeighbourhoodFunction<Plank, FloorSolution> {

    public WithinRowSwapNeighbour(Configuration configuration) {
        super(configuration);
    }

    @Override
    public FloorSolution getNeighbour(FloorSolution candidate) {
        int fixedRows = candidate.getUnswappableRows();
        int fullRows = candidate.getFullRows();
        int row = random.nextInt(fullRows - fixedRows)+fixedRows;
        int rowStart = candidate.getRowStart(row);
        int rowEnd = candidate.getRowEnd(row);
        int rowLength = rowEnd-rowStart;
        boolean swapped = false;
        rowStart -= candidate.fixedSize();
        FloorSolution result = candidate.clone();
        for (int i = 0; i < rowLength && !swapped; i++) {
            int from = random.nextInt(rowLength)+rowStart;
            int to = random.nextInt(rowLength)+rowStart;
            swapped = result.swap(from, to);
        }
        return result;
    }
}
