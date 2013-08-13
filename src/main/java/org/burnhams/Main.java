package org.burnhams;

import org.burnhams.flooring.FloorEvaluator;
import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.Plank;
import org.burnhams.flooring.PropertiesConfiguration;
import org.burnhams.optimiser.HillClimber;

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
        HillClimber<Plank, FloorSolution> hillClimber = new HillClimber<>(evaluator, configuration.getHillClimbChoices(), configuration.getHillClimbMaxNonImprovingMoves());
        FloorSolution solution = hillClimber.optimise(initialSolution);
        BufferedImage image = solution.createImage(2000);
        ImageIO.write(image, "PNG", new File("solution.png"));
    }

}
