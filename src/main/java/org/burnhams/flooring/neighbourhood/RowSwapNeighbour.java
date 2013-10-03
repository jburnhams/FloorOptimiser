package org.burnhams.flooring.neighbourhood;

import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.Plank;
import org.burnhams.optimiser.Configuration;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;

public class RowSwapNeighbour extends NeighbourhoodFunction<Plank, FloorSolution> {

    public RowSwapNeighbour(Configuration configuration) {
        super(configuration);
    }

    @Override
    public FloorSolution getNeighbour(FloorSolution candidate) {
        int fixedRows = candidate.getUnswappableRows();
        int fullRows = candidate.getFullRows() - fixedRows;
        int row1 = random.nextInt(fullRows);
        int row2 = random.nextInt(fullRows);
        while (row1 == row2) {
            row2 = random.nextInt(fullRows);
        }
        return candidate.swapRows(row1+fixedRows, row2+fixedRows);
    }
}
