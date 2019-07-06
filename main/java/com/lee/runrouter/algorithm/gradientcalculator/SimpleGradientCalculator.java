package com.lee.runrouter.algorithm.gradientcalculator;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.ArrayList;
import java.util.List;

public class SimpleGradientCalculator implements GradientCalculator {
    // simple rise over run calculation
    public double calculateGradient(Node currentNode, Way startingWay,
                                    Node visitedNode, Way selectedWay, double distance) {
        double startElevation = getStartElevation(currentNode, startingWay);
        double endElevation = getEndElevation(visitedNode, selectedWay);

        double elevationDelta = endElevation - startElevation;

        return ((elevationDelta) / distance);
    }


    /**
     * Test which endpoint of the Way the current Node is closest
     * to and return the corresponding elevation.
     */
    private double getStartElevation(Node currentNode, Way startingWay) {
        List<Node> wayEndpoints = new ArrayList<>();
        wayEndpoints.add(startingWay.getNodeContainer().getStartNode());
        wayEndpoints.add(startingWay.getNodeContainer().getEndNode());

        Node closest = AlgoHelpers.findClosest(currentNode, wayEndpoints);

        if (closest.getId() == startingWay.getNodeContainer().getStartNode().getId()) {
            return startingWay.getElevationPair().getStartElevation();
        } else {
            return startingWay.getElevationPair().getEndElevation();
        }
    }

    /**
     * Test which endpoint of the Way the current Node is closest
     * to and return the corresponding elevation.
     */
    public double getEndElevation(Node visitedNode, Way selectedWay) {
        List<Node> wayEndpoints = new ArrayList<>();
        wayEndpoints.add(selectedWay.getNodeContainer().getStartNode());
        wayEndpoints.add(selectedWay.getNodeContainer().getEndNode());

        Node closest = AlgoHelpers.findClosest(visitedNode, wayEndpoints);

        if (closest.getId() == selectedWay.getNodeContainer().getStartNode().getId()) {
            return selectedWay.getElevationPair().getStartElevation();
        } else {
            return selectedWay.getElevationPair().getEndElevation();
        }
    }
}
