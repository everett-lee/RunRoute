package com.lee.runrouter.algorithm.gradientcalculator;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface GradientCalculator {
    public double calculateGradient(Node currentNode, Way startingWay,
                                    Node visitedNode, Way selectedWay, double distance);
}
