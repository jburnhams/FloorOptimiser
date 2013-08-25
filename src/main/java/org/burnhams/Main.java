package org.burnhams;

import org.burnhams.flooring.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
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
            writePlankTypesCount(out, "Planks Used", solution.getPlankTypesUsed());
            writePlankTypesCount(out, "Planks Spare", solution.getPlankTypesSpare());
        }
        finally {
            if (out != null) out.close();
        }
    }

    private static void writePlankTypesCount(PrintStream out, String title, Map<Plank, Integer> plankTypesSpare) {
        out.println();
        out.println(title);
        for (Map.Entry<Plank, Integer> entry : plankTypesSpare.entrySet()) {
            out.print(entry.getKey().getLength());
            out.print("=");
            out.println(entry.getValue());
        }
    }

}
