package com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * A more sensitive version of the standard elevation heuristic.
 * This is used in the iterated local search algorithm to choose
 * steeps.
 */
@Component
@Qualifier("ElevationHeuristicSensitive")
public class ElevationHeuristicSensitive implements ElevationHeuristic {
    private boolean preferUphill;
    private final double MULTIPLIER = 6; // number to scale
    // gradient by in increase its share of heuristic score

    public ElevationHeuristicSensitive() {
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
