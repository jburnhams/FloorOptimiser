package org.burnhams.flooring;

import org.burnhams.flooring.floors.Floor;
import org.burnhams.flooring.floors.MultiLengthFloor;
import org.burnhams.flooring.floors.RectangularFloor;
import org.burnhams.flooring.floors.wallenclosed.CornerWallLength;
import org.burnhams.flooring.floors.wallenclosed.WallEnclosedFloor;
import org.burnhams.optimiser.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfiguration implements Configuration {

    private final Properties properties;

    public PropertiesConfiguration() throws IOException {
        properties = new Properties();
        InputStream resource = getClass().getResourceAsStream("/configuration.properties");
        try {
            properties.load(resource);
        } finally {
            resource.close();
        }
    }

    public int getPlankWidth() {
        return getInteger("plank.width");
    }

    public int[] getFixedPlanks() {
        return getIntegers("plank.fixed");
    }


    private WallEnclosedFloor getWallEnclosedFloor() {
        if (properties.containsKey("floor.horizontalLength")) {
            return new WallEnclosedFloor(getInteger("floor.horizontalLength"), CornerWallLength.parse(properties.getProperty("floor.walls")));
        } else {
            return null;
        }
    }

    public Floor getFloor() {
        WallEnclosedFloor floor = getWallEnclosedFloor();
        if (floor == null) {
            int[] widths = getIntegers("floor.widths");
            int[] lengths = getIntegers("floor.lengths");
            if (widths.length == 1) {
                return new RectangularFloor(widths[0], lengths[1]);
            } else {
                return new MultiLengthFloor(widths, lengths);
            }
        } else {
            return floor;
        }
    }

    public int getThreads() {
        return getInteger("threads");
    }

    private int getInteger(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    private int[] getIntegers(String key) {
        String[] strings = properties.getProperty(key).split(",");
        int[] result = new int[strings.length];
        for (int i = 0; i<strings.length; i++) {
            result[i] = Integer.valueOf(strings[i].trim());
        }
        return result;
    }

    private Double getDouble(String key) {
        return Double.valueOf(properties.getProperty(key));
    }

    @Override
    public double getStartingTemperature() {
        return getDouble("starting.temperature");
    }

    @Override
    public long getMaxIterations() {
        return Long.valueOf(properties.getProperty("max.iterations"));
    }

    public int getHillClimbChoices() {
        return getInteger("hillclimb.choices");
    }

    public int getHillClimbMaxNonImprovingMoves() {
        return getInteger("hillclimb.maxNonImprovingMoves");
    }


    public Map<Integer, Integer> getPlankLengths() {
        Map<Integer, Integer> result = new HashMap<>();
        String[] lengths = properties.getProperty("plank.lengths").split(",");
        for (String length : lengths) {
            int l = Integer.valueOf(length);
            int c = getInteger("plank."+l);
            result.put(l,c);
        }
        return result;
    }

}
