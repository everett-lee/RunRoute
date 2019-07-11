package com.lee.runrouter.algorithm.cyclegenerator;

import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface CycleGenerator {
    public PathTuple generateCycle(double[] coords, double distance) throws PathNotGeneratedException;
}