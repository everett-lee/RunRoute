package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.List;

/**
 * Scores Way under consideration based on its features and attributes.
 * The scores reflect matches against user-supplied preferences.
 */
public class FeaturesHeuristic implements Heuristic {
    private double score = 0.0;
    private Way selectedWay;
    private Node currentNode;

    private List<String> preferredSurfaces;
    private List<String> preferredHighways;


    // Allocated scores for features
    static final double SURFACE_VALUE = 0.02;
    static final double HIGHWAY_VALUE = 0.02;

    public FeaturesHeuristic(List<String> preferredSurfaces, List<String> preferredHighways) {
        this.preferredSurfaces = preferredSurfaces;
        this.preferredHighways = preferredHighways;
    }

    private void calculateScore() {
        for (String surface: preferredSurfaces) {
            if (this.selectedWay.getSurface().equals(surface.toUpperCase())) {
                this.score += SURFACE_VALUE;
            }
        }

        for (String highway: preferredHighways) {
            if (this.selectedWay.getHighway().equals(highway.toUpperCase())) {
                this.score += HIGHWAY_VALUE;
            }
        }
    }

    @Override
    public double getScore (Node currentNode, Node connectingNode, Way selectedWay) {
        this.selectedWay = selectedWay;
        this.currentNode = currentNode;

        calculateScore();
        return this.score;
    }

}
