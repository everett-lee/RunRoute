package com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;

/**
 * Interface for calculating a score derived from the
 * elevation change between two Nodes
 */
public interface ElevationHeuristic {
    double getScore(double gradient, double distance);

    void setOptions(boolean preferUpHill);

}
