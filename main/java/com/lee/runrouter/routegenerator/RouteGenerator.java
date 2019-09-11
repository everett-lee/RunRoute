package com.lee.runrouter.routegenerator;

import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface RouteGenerator {
    PathTuple generateRoute(double[] coords,
                            double distance) throws PathNotGeneratedException;

}
