package com.lee.runrouter.routegenerator.cyclegenerator;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;

public interface CycleGenerator {
    public PathTuple generateCycle(double[] coords, double distance) throws PathNotGeneratedException;
}