package org.burnhams;

import org.burnhams.flooring.FloorEvaluator;
import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.Plank;
import org.burnhams.flooring.PropertiesConfiguration;
import org.burnhams.optimiser.HillClimber;

import java.io.IOException;

public class Main {

    public static void main(Object... arguments) throws IOException {
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        FloorEvaluator evaluator = new FloorEvaluator();
        FloorSolution initialSolution = new FloorSolution(
                configuration.getFloorWidth(), configuration.getFloorLength(),
                configuration.getPlankWidth(), configuration.getPlankLengths()
        );
        HillClimber<Plank, FloorSolution> hillClimber = new HillClimber<>(evaluator, configuration.getHillClimbChoices());
    }

}
