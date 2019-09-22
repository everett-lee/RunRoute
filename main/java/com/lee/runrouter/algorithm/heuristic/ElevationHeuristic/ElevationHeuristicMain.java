package com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Returns a score corresponding to the gradient of the path travelled.
 * It returns a positive score consistent with the gradient where steeper
 * routes are required
 */
@Component
@Qualifier("ElevationHeuristicMain")
public class ElevationHeuristicMain implements ElevationHeuristic {
    private boolean preferUphill;
    private final double MULTIPLIER = 10; // number to scale
    // gradient by in increase its share of heuristic score

    public ElevationHeuristicMain() {
        this.preferUphill = false;
    }

    @Override
    public double getScore(double gradient, double distance) {

        // ignore very short distance
        if (distance < 100) {
            return 0;
        }

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
