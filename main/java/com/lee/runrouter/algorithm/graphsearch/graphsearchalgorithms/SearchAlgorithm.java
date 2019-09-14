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

    private final double MINIMUM_SCORING_DISTANCE = 450; // the minimum distance
    // travelled along a Way before the distance bonus is applied
    private final double MAXIMUM_SCORING_DISTANCE = 1000; //the maximum distance
    // travelled along a Way that will contributed to the distance bonus
    private final double DISTANCE_BONUS = 0.0005;
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

    // a helper method for calculating the score accrued from the Way
    // making up a path
    protected double addScores(Way selectedWay, double distanceTravelled, double gradient) {
        double score = 0;

        // add score reflecting correspondence of terrain features to user selections
        score += featuresHeuristic.getScore(selectedWay, distanceTravelled);

        // add score reflecting gradient of the Way
        score += elevationHeuristic.getScore(gradient, distanceTravelled);

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

    // set the prefer uphill preference. Guides the search towards
    // steeper paths
    public void setElevationHeuristicOptions(boolean preferUphill) {
        if (preferUphill) {
            this.elevationHeuristic.setOptions(true);
        } else {
            this.elevationHeuristic.setOptions(false);
        }
    }

    public double applyDistanceScore(double distanceToNext) {
        if (distanceToNext > MINIMUM_SCORING_DISTANCE) {
            double scoreLength = Math
                    .min(distanceToNext, MAXIMUM_SCORING_DISTANCE);
            return scoreLength * DISTANCE_BONUS;
        }
        return 0;
    }

    public boolean pruneBranch(Way selectedWay, double currentDistance, double upperBound) {
        // prune this branch where street lighting required and none available
        if (getAvoidUnlit()) {
            if (!selectedWay.isLit()) {
                return true;
            }
        }

        // prune this branch where maximum length exceeded
        if (currentDistance > upperBound) {
            return true;
        }
        return false;
    }
}
