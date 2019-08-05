package com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;

public interface ElevationHeuristic {
    public double getScore(double gradient);
    public void setOptions(boolean preferUpHill);

}
