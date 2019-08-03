package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
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
    DistanceFromOriginNodeHeursitic distanceFromOriginHeursitic;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    Heuristic distanceFromOriginHeuristic;
    double maxGradient;
    boolean avoidUnlit;

    public SearchAlgorithm(ElementRepo repo,
                           DistanceFromOriginNodeHeursitic distanceFromOriginHeursitic,
                           Heuristic featuresHeuristic,
                           EdgeDistanceCalculator edgeDistanceCalculator,
                           GradientCalculator gradientCalculator,
                           ElevationHeuristic elevationHeuristic) {
        this.repo = repo;
        this.distanceFromOriginHeursitic = distanceFromOriginHeursitic;
        this.gradientCalculator = gradientCalculator;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;
        this.maxGradient = 1;
        this.avoidUnlit = false;

    }

    protected double addScores(Way selectedWay, double gradient, double RANDOM_REDUCER) {
        double score = 0;

        // add score reflecting correspondence of terrain features to user selections
        score += featuresHeuristic.getScore(selectedWay);

        score += elevationHeuristic.getScore(gradient);

        // add a small random value to break ties
        score += (Math.random() / RANDOM_REDUCER);

        return score;
    }

    public void setMaxGradient(double maxGradient) {
        this.maxGradient = maxGradient;
    }

    public double getMaxGradient() {
        return this.maxGradient;
    }

    public void setAvoidUnlit(boolean avoidUnlit) {
        this.avoidUnlit = avoidUnlit;
    }

    public boolean getAvoidUnlit() {
        return this.avoidUnlit;
    }
}
