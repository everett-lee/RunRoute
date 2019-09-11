package com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Scores Way under consideration based on its features and attributes.
 * The score reflects user-supplied preferences.
 * This variant multipliers the score by the distance travelled.
 */
@Component
@Qualifier("FeaturesHeuristicUsingDistance")
public class FeaturesHeuristicUsingDistance implements FeaturesHeuristic {
    private List<String> preferredSurfaces; // list of user-preferred surfaces
    private List<String> preferredHighways; // list of user-preferred highways
    private List<String> dislikedSurfaces; // list of surfaces to be avoided

    // allocated scores for features
    static final double SURFACE_VALUE = 0.01;
    static final double HIGHWAY_VALUE = 0.01;


    public FeaturesHeuristicUsingDistance() {
        this.preferredSurfaces = new ArrayList<>();
        this.preferredHighways = new ArrayList<>();
        this.dislikedSurfaces = new ArrayList<>();
    }

    // iterate over user-provided surface and highway types and
    // adjust score for matching values
    private double calculateScore(Way selectedWay, double distance) {
        double score = 0;

        // short distances are not considered
        if (distance < 200) {
            return 0;
        }

        for (String surface: this.preferredSurfaces) {
            if (selectedWay.getSurface().equals(surface.toUpperCase())) {
                score += SURFACE_VALUE * distance;
            }
        }

        // lower score where surface should be avoided
        for (String surface: this.dislikedSurfaces) {
            if (selectedWay.getSurface().equals(surface.toUpperCase())) {
                score -= SURFACE_VALUE * distance;
            }
        }

        for (String highway: this.preferredHighways) {
            if (selectedWay.getHighway().equals(highway.toUpperCase())) {
                score += HIGHWAY_VALUE * distance;
            }
        }

        return score;
    }

    @Override
    public double getScore (Way selectedWay, double distance) {
        return calculateScore(selectedWay, distance);
    }

    public void setPreferredSurfaces(List<String> preferredSurfaces) {
        this.preferredSurfaces = preferredSurfaces;
    }

    public void setDislikedSurfaces(List<String> dislikedSurfaces) {
        this.dislikedSurfaces = dislikedSurfaces;
    }

    public void setPreferredHighways(List<String> preferredHighways) {
        this.preferredHighways = preferredHighways;
    }
}
