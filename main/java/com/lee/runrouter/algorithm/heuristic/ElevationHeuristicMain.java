package com.lee.runrouter.algorithm.heuristic;

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
    private double maxGradient; // user-defined max acceptable gradient
    private final double MULTIPLIER = 10; // number to scale
    // gradient by in increase its share of heuristic score
    private final double MAX_GRADIENT_PENALTY = -10000; // penalty
    // for exceeding the max gradient

    public ElevationHeuristicMain() {
        this.preferUphill = false;
        this.maxGradient = 0.5;
    }

    @Override
    public double getScore(double gradient) {
        if (gradient > maxGradient) {
            return MAX_GRADIENT_PENALTY;
        }

        // flatter routes are preferred, so increase score where gradient is lower
        if (!preferUphill) {
            return 0.25 - Math.abs(gradient * MULTIPLIER);
        }

        // score increases in line with the gradient
        if (gradient > 0) {
            return gradient * MULTIPLIER;
        }

        // return 0 where uphill preferred and gradient is negative
        return 0;
    }

    @Override
    public void setMaxGradient(double gradient) {
        this.maxGradient = gradient;
    }

    @Override
    public void setOptions(boolean preferUpHill) {
        this.preferUphill = preferUpHill;
    }


}
