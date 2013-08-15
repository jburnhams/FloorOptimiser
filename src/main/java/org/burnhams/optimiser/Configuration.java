package org.burnhams.optimiser;

public interface Configuration {

    public double getStartingTemperature();

    public int getMaxIterations();

    public int getHillClimbChoices();

    public int getHillClimbMaxNonImprovingMoves();
}
