package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A template for the various graph search algorithms.
 * Encapsulates dependencies and the scoring method.
 */
@Component
public abstract class SearchAlgorithm {
    ElementRepo repo; // the repository of Ways and Nodes
    DistanceFromOriginNodeHeursitic distanceFromOriginHeuristic;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristic;
    double maxGradient;
    boolean avoidUnlit;

    private final double RANDOM_REDUCER = 50000; // divides into random number added to the
    // score

    public SearchAlgorithm(ElementRepo repo,
                           DistanceFromOriginNodeHeursitic distanceFromOriginHeursitic,
                           FeaturesHeuristic featuresHeuristic,
                           EdgeDistanceCalculator edgeDistanceCalculator,
                           GradientCalculator gradientCalculator,
                           ElevationHeuristic elevationHeuristic) {
        this.repo = repo;
        this.distanceFromOriginHeuristic = distanceFromOriginHeursitic;
        this.gradientCalculator = gradientCalculator;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;
        this.maxGradient = 1;
        this.avoidUnlit = false;

    }

    // a helper method for calculating the score of way Way making up a path
    protected double addScores(Way selectedWay, double distanceTravelled, double gradient) {
        double score = 0;

        // add score reflecting correspondence of terrain features to user selections
        score += featuresHeuristic.getScore(selectedWay, distanceTravelled);

        // add score reflecting gradient of the Way
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

    public void setFeaturesHeuristicOptions(List<String> preferredHighways, List<String> preferredSurfaces,
                                            List<String> dislikedSurfaces) {
        this.featuresHeuristic.setPreferredHighways(preferredHighways);
        this.featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
        this.featuresHeuristic.setDislikedSurfaces(dislikedSurfaces);
    }

    public void setElevationHeuristicOptions(boolean preferUphill) {
        if (preferUphill) {
            this.elevationHeuristic.setOptions(true);
        } else {
            this.elevationHeuristic.setOptions(false);
        }
    }
}
