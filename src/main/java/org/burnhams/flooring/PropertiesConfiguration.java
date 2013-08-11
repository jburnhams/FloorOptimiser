package org.burnhams.flooring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfiguration {

    private final Properties properties;

    public PropertiesConfiguration() throws IOException {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("configuration.properties"));
    }

    public int getPlankWidth() {
        return Integer.valueOf(properties.getProperty("plank.width"));

    }

    public int getFloorWidth() {
        return Integer.valueOf(properties.getProperty("floor.width"));

    }

    public int getFloorLength() {
        return Integer.valueOf(properties.getProperty("floor.length"));
    }

    public int getHillClimbChoices() {
        return Integer.valueOf(properties.getProperty("hillclimb.choices"));
    }


    public Map<Integer, Integer> getPlankLengths() {
        Map<Integer, Integer> result = new HashMap<>();
        String[] lengths = properties.getProperty("plank.lengths").split(",");
        for (String length : lengths) {
            int l = Integer.valueOf(length);
            int c = Integer.valueOf(properties.getProperty("plank."+l));
            result.put(l,c);
        }
        return result;
    }

}
