package org.burnhams.optimiser;

public interface Configuration {

    public int getThreads();

    public double getStartingTemperature();

    public long getMaxIterations();

    public int getHillClimbChoices();

    public int getHillClimbMaxNonImprovingMoves();
}
