package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public class DistanceFromOriginHeuristic implements Heuristic {
    private ElementRepo repo;
    private DistanceCalculator distanceCalculator;
    private final double NUMERATOR = 10; // magnitude scaled with number of points
    // attributed to finding return ways


    public DistanceFromOriginHeuristic(ElementRepo repo, DistanceCalculator distanceCalculator) {
        this.repo = repo;
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public double getScore(Node currentNode, Way selectedWay) {
        Node startNode = selectedWay.getNodeContainer().getStartNode();
        Node endNode = selectedWay.getNodeContainer().getEndNode();

        double startNodeDistance = distanceCalculator.calculateDistance(startNode,
                repo.getOriginWay().getNodeContainer().getStartNode());


        double endNodeDistance = distanceCalculator.calculateDistance(endNode,
                repo.getOriginWay().getNodeContainer().getStartNode());


        return NUMERATOR / Math.min(startNodeDistance, endNodeDistance);
    }
}
