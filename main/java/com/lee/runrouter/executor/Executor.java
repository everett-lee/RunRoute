package com.lee.runrouter.executor;

import com.lee.runrouter.routegenerator.PathNotGeneratedException;

public interface Executor {
    ResponseObject executeQuery(double[] coords, double maxGradient,
                                   double length, boolean[] options) throws PathNotGeneratedException;
    void executeInitialGraphBuild(double[] coords);
}
