package com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public interface EdgeDistanceCalculator {
    public double calculateDistance(Node currentNode, Node connectingNode, Way currentWay);
}
