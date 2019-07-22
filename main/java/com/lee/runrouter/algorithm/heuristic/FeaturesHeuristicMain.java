package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Scores Way under consideration based on its features and attributes.
 * The scores reflect matches against user-supplied preferences.
 */
@Component
@Qualifier("FeaturesHeuristicMain")
public class FeaturesHeuristicMain implements Heuristic, FeaturesHeuristic {
    private double score;
    private Way selectedWay;

    private boolean avoidUnlit; // user choice to avoid unlit streets
    private List<String> preferredSurfaces; // list of user-preferred surfaces
    private List<String> preferredHighways; // list of user-preferred highways
    private List<String> dislikedSurfaces; // list of surfaces to be avoided

    // allocated scores for features
    static final double SURFACE_VALUE = 0.5;
    static final double HIGHWAY_VALUE = 1;
    // score deduction for unlit ways
    static final double UNLIT_PENALTY = 10000;

    public FeaturesHeuristicMain() {
        this.avoidUnlit = false;
        this.preferredSurfaces = new ArrayList<>();
        this.preferredHighways = new ArrayList<>();
        this.dislikedSurfaces = new ArrayList<>();
    }

    // iterate over user-provided surface and highway types and
    // adjust score for matching values
    private void calculateScore() {
        for (String surface: this.preferredSurfaces) {
            if (this.selectedWay.getSurface().equals(surface.toUpperCase())) {
                this.score += SURFACE_VALUE;
            }
        }

        // lower score where surface should be avoided
        for (String surface: this.dislikedSurfaces) {
            if (this.selectedWay.getSurface().equals(surface.toUpperCase())) {
                this.score -= SURFACE_VALUE;
            }
        }

        for (String highway: this.preferredHighways) {
            if (this.selectedWay.getHighway().equals(highway.toUpperCase())) {
                this.score += HIGHWAY_VALUE;
            }
        }

        if (this.avoidUnlit) {
            if (!selectedWay.isLit()) {
                this.score -= UNLIT_PENALTY;
            }
        }
    }

    @Override
    public double getScore (Way selectedWay) {
        this.selectedWay = selectedWay;
        score = 0;
        calculateScore();
        return this.score;
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

    public void setAvoidUnlit(boolean choice) {
        this.avoidUnlit = choice;
    }
}
