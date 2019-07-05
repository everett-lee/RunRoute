package com.lee.runrouter.algorithm.heuristic;

/**
 * Returns a score corresponding to the gradient of the path travelled.
 * This score either represents the 'flatness' of the route
 * , where flatter routes are preferred, or correlates to the route's
 * steepness.
 */
public class ElevationHeuristicMain implements ElevationHeuristic {
    private boolean preferUphill;
    private final double MULTIPLIER = 10; // number to scale
    // gradient by in increase its share of heuristic score

    public ElevationHeuristicMain(boolean preferUphill) {
        this.preferUphill = preferUphill;
    }

    @Override
    public double getScore(double gradient) {
        // flatter routes are preferred, so increase score where gradient is lower
        if (!preferUphill) {
            return 0.5 - Math.abs(gradient * MULTIPLIER);
        }

        // score increases in line with the gradient
        if (gradient > 0) {
            return gradient * MULTIPLIER;
        }

        // return 0 where uphill preferred and gradient is negative
        return 0;
     }
}
