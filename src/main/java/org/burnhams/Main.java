package org.burnhams;

import org.burnhams.flooring.FloorEvaluator;
import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.Plank;
import org.burnhams.flooring.PropertiesConfiguration;
import org.burnhams.flooring.neighbourhood.RowSwapNeighbour;
import org.burnhams.flooring.neighbourhood.WithinRowSwapNeighbour;
import org.burnhams.optimiser.HillClimber;
import org.burnhams.optimiser.Optimiser;
import org.burnhams.optimiser.SimulatedAnnealing;
import org.burnhams.optimiser.neighbourhood.NeighbourhoodFunction;
import org.burnhams.optimiser.neighbourhood.RandomSwapNeighbour;
import org.burnhams.optimiser.neighbourhood.ShuffleNeighbour;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        FloorEvaluator evaluator = new FloorEvaluator();
        FloorSolution initialSolution = new FloorSolution(
                configuration.getFloorWidth(), configuration.getFloorLength(),
                configuration.getPlankWidth(), configuration.getPlankLengths()
        );
        initialSolution.shuffle();
        initialSolution.evaluate();
        Optimiser<Plank, FloorSolution> optimiser;
        FloorSolution solution = initialSolution;

        optimiser = new HillClimber<>(evaluator, configuration, 1000, 1, new ShuffleNeighbour<Plank, FloorSolution>(configuration));
        solution = optimiser.optimise(solution);


        NeighbourhoodFunction[] neighbourhoodFunctions = {new RandomSwapNeighbour<Plank, FloorSolution>(configuration),
                new WithinRowSwapNeighbour(configuration),
                new RowSwapNeighbour(configuration)};
        optimiser = new SimulatedAnnealing<>(evaluator, configuration, neighbourhoodFunctions);
        solution = optimiser.optimise(solution);

        optimiser = new HillClimber<>(evaluator, configuration, neighbourhoodFunctions);
        solution = optimiser.optimise(solution);

        BufferedImage image = solution.createImage(2000);
        ImageIO.write(image, "PNG", new File("solution.png"));
    }


}
