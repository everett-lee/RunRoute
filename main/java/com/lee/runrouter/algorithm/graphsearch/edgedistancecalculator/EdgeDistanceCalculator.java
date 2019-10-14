package com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

/**
 *  Provides an interface for measuring the distance between
 *  two pairs of coordinates, contained in a Node, along
 *  a given Way.
 */
public interface EdgeDistanceCalculator {
    double calculateDistance(Node currentNode, Node connectingNode, Way currentWay);
}
