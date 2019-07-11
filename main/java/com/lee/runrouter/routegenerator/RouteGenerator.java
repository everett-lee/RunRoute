package com.lee.runrouter.routegenerator;
import com.lee.runrouter.algorithm.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface RouteGenerator {
    public PathTuple generateRoute(double[] coords,
                                         double distance) throws PathNotGeneratedException;

}
