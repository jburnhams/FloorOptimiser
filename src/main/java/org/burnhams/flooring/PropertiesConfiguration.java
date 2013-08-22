package org.burnhams.flooring;

import org.burnhams.optimiser.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfiguration implements Configuration {

    private final Properties properties;

    public PropertiesConfiguration() throws IOException {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/configuration.properties"));
    }

    public int getPlankWidth() {
        return getInteger("plank.width");

    }

    public int getFloorWidth() {
        return getInteger("floor.width");

    }

    public int getFloorLength() {
        return getInteger("floor.length");
    }

    private Integer getInteger(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    private Double getDouble(String key) {
        return Double.valueOf(properties.getProperty(key));
    }

    @Override
    public double getStartingTemperature() {
        return getDouble("starting.temperature");
    }

    @Override
    public int getMaxIterations() {
        return getInteger("max.iterations");
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
