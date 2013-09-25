package org.burnhams;

import org.burnhams.flooring.FloorEvaluator;
import org.burnhams.flooring.FloorOptimiser;
import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.PropertiesConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String args[]) throws IOException, ExecutionException {
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        String filename = "solution.csv";

        FloorOptimiser optimiser = new FloorOptimiser(configuration, new FloorEvaluator());
        FloorSolution solution = optimiser.optimise();
        BufferedImage image = solution.createImage(4000);
        ImageIO.write(image, "PNG", new File("solution.png"));
        solution.saveToFile(filename);

        /*FloorSolution solution = FloorSolution.loadFromFile(configuration.getFloor(), configuration.getPlankWidth(), filename);
        solution.evaluate();
        solution.saveToFile(filename);*/
    }

}
