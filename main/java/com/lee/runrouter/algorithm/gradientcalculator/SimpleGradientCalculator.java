package com.lee.runrouter.algorithm.gradientcalculator;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("SimpleGradientCalculator")
public class SimpleGradientCalculator implements GradientCalculator {
    /**
     * Returns the gradient between two pairs of coordinates using a
     * simple rise over run calculation
     *
     * @param currentNode the Node containing the starting coordinates
     * @param startingWay the Way containing the starting Node
     * @param visitedNode the Node containing the end coordinates
     * @param selectedWay the Way containing the end Node
     * @param distance the distance travelled between each Node/pair
     *                 of coordinates
     * @return A double representing the gradient
     */
    public double calculateGradient(Node currentNode, Way startingWay,
                                    Node visitedNode, Way selectedWay, double distance) {
        if (distance == 0) {
            return 0;
        }

        double startElevation = getStartElevation(currentNode, startingWay);

        double endElevation = getEndElevation(visitedNode, selectedWay);

        double elevationDelta = endElevation - startElevation;

        return elevationDelta / distance;
    }


    /**
     * Determines which endpoint of the Way the current Node is closest
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
     * Determines which endpoint of the Way the current Node is closest
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
