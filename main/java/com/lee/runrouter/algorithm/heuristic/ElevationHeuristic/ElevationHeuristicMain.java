package com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Returns a score corresponding to the gradient of the path travelled.
 * This score either represents the 'flatness' of the route
 * , where flatter routes are preferred, or correlates to the route's
 * steepness.
 */
@Component
@Qualifier("ElevationHeuristicMain")
public class ElevationHeuristicMain implements ElevationHeuristic {
    private boolean preferUphill;
    private final double MULTIPLIER = 40; // number to scale
    // gradient by in increase its share of heuristic score

    public ElevationHeuristicMain() {
        this.preferUphill = false;
    }

    @Override
    public double getScore(double gradient) {

        if (preferUphill) {
            // score increases in line with the gradient
            if (gradient > 0) {
                return gradient * MULTIPLIER;
            }
        }

        // return 0 where uphill preferred and gradient is negative
        return 0;
    }

    @Override
    public void setOptions(boolean preferUpHill) {
        this.preferUphill = preferUpHill;
    }
}
