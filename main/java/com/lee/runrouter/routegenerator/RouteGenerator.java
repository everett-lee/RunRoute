package com.lee.runrouter.routegenerator;
import com.lee.runrouter.algorithm.graphsearch.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.pathnode.PathTuple;

public interface RouteGenerator {
    public PathTuple generateRoute(double[] coords,
                                         double distance) throws Exception, PathNotGeneratedException;

}
