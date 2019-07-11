package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * A template for the various graph search algorithms.
 * Encapsulates dependencies and the scoring method.
 */
@Component
public abstract class SearchAlgorithm {
    ElementRepo repo; // the repository of Ways and Nodes
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    Heuristic distanceFromOriginHeuristic;
    final Set<Long> visitedWays;

    public SearchAlgorithm(ElementRepo repo,
                           Heuristic distanceHeuristic,
                           Heuristic featuresHeuristic,
                           EdgeDistanceCalculator edgeDistanceCalculator,
                           GradientCalculator gradientCalculator,
                           ElevationHeuristic elevationHeuristic) {
        this.repo = repo;
        this.distanceFromOriginHeuristic = distanceHeuristic;
        this.gradientCalculator = gradientCalculator;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;
        this.visitedWays = new HashSet<>();

    }

    protected double addScores(Way selectedWay, double gradient, double REPEATED_EDGE_PENALTY,
                               double RANDOM_REDUCER) {
        double score = 0;

        // drop the score where this way has already been explored
        if (visitedWays.contains(selectedWay.getId())) {
            score += REPEATED_EDGE_PENALTY;
        }

        // add score reflecting correspondence of terrain features to user selectionss
        score += featuresHeuristic.getScore(selectedWay);

        score += elevationHeuristic.getScore(gradient);

        // add a small random value to break ties
        score+= (Math.random() / RANDOM_REDUCER);

        return score;
    }
}
