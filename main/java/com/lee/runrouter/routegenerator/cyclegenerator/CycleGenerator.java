package com.lee.runrouter.routegenerator.cyclegenerator;

import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface CycleGenerator {
    public PathTuple generateCycle(double[] coords, double distance) throws PathNotGeneratedException;
}