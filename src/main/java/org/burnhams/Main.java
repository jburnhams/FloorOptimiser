package org.burnhams;

import org.burnhams.flooring.FloorEvaluator;
import org.burnhams.flooring.FloorOptimiser;
import org.burnhams.flooring.FloorSolution;
import org.burnhams.flooring.PropertiesConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String args[]) throws IOException, ExecutionException {
        FloorOptimiser optimiser = new FloorOptimiser(new PropertiesConfiguration(), new FloorEvaluator());
        FloorSolution solution = optimiser.optimise();
        BufferedImage image = solution.createImage(4000);
        ImageIO.write(image, "PNG", new File("solution.png"));

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("solution.csv"));
            out.println(solution.getLengthsList());
        }
        finally {
            if (out != null) out.close();
        }
    }

}
