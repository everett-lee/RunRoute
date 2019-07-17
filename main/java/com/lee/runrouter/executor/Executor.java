package com.lee.runrouter.executor;

import com.lee.runrouter.routegenerator.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.List;

public interface Executor {
    public List<Node> executeQuery(double[] coords, double maxGradient,
                                   double length, boolean[] options) throws PathNotGeneratedException;
}
