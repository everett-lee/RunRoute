package com.lee.runrouter.algorithm.heuristic;

public interface ElevationHeuristic {
    public double getScore(double gradient);
    public void setMaxGradient(double gradient);
    public void setOptions(boolean preferUpHill);

}
