package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

public class ElevationHeuristicMain implements ElevationHeuristic {
    boolean preferDownhill;

    public ElevationHeuristicMain(boolean preferDownhill) {
        this.preferDownhill = preferDownhill;
    }

    @Override
    public double getScore(Node currentNode, Node visitedNode, Way startingWay, Way selectedWay, double distance) {

        double startElevation = getStartElevation(currentNode, startingWay);
        double endElevation = getEndElevation(visitedNode, selectedWay);

        return calculateGradient(startElevation, endElevation, distance);
    }

    private double getStartElevation(Node currentNode, Way startingWay) {
        return currentNode == startingWay.getNodeContainer().getStartNode()?
                startingWay.getElevationPair().getStartElevation(): // if the Node is the start of this Way
                startingWay.getElevationPair().getEndElevation(); // if the Node is the end of this Way
    }

    public double getEndElevation(Node visitedNode, Way selectedWay) {
        return visitedNode == selectedWay.getNodeContainer().getStartNode()?
                selectedWay.getElevationPair().getStartElevation(): // if the Node is the start of this Way
                selectedWay.getElevationPair().getEndElevation(); // if the Node is the end of this Way
    }

    // simple rise over run calculation
    private double calculateGradient(double startElevation, double endElevation, double distance) {
        double modifier = 1; // where uphill is preferred

        if (preferDownhill) {
            modifier = -1;
        }

        double elevationDelta = endElevation - startElevation;

        if (elevationDelta == 0) {
            return 0;
        }

        return ((endElevation - startElevation) / distance) * modifier;
    }

}
