package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.List;

/**
 * Scores the Way under consideration based on the distance from the starting/origin Way.
 * This is used to favour returning routes.
 */
public class DistanceFromOriginToMidHeuristic implements Heuristic {
    private ElementRepo repo;
    private DistanceCalculator distanceCalculator;
    private final double NUMERATOR = 500; // magnitude scaled with number of points
    // attributed to finding return ways


    public DistanceFromOriginToMidHeuristic(ElementRepo repo, DistanceCalculator distanceCalculator) {
        this.repo = repo;
        this.distanceCalculator = distanceCalculator;
    }

    /**
     *
     * @param currentNode the current Node
     * @param visitedNode the Node connecting the current Way to the selectedWay
     * @param selectedWay the Way being assessed
     * @return a score corresponding to the distance from either endpoint of the way
     * back to the route origin
     */
    @Override
    public double getScore(Way selectedWay) {
        List<Node> selectedWaynodes = selectedWay.getNodeContainer().getNodes();
        Node midSelectedNode = selectedWaynodes.get(selectedWaynodes.size()/2);

        List<Node> startingWaynodes = repo.getOriginWay().getNodeContainer().getNodes();
        Node midStartNode = startingWaynodes.get(startingWaynodes.size()/2);

        double distance = distanceCalculator.calculateDistance(midSelectedNode,
                midStartNode);

        return NUMERATOR / distance;
    }
}