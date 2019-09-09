package com.lee.runrouter.algorithm.gradientcalculator;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.stereotype.Component;

/**
 * Provides an interface for measuring the slope
 * corresponding to the section of road connecting
 * two Nodes
 */
@Component
public interface GradientCalculator {

    double calculateGradient(Node currentNode, Way startingWay,
                                    Node visitedNode, Way selectedWay, double distance);
}
