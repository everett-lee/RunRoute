package com.lee.runrouter.routegenerator;

import com.lee.runrouter.algorithm.graphsearch.cyclegenerator.CycleGenerator;
import com.lee.runrouter.algorithm.graphsearch.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch.IteratedLocalSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.List;

/**
 * Uses the starting coordinates, run length and options sent
 * from the client to generate the initial route. This is
 * then used provided as input to the iterated local search
 * to yield the improved final route.
 */
public class RouteGeneratorMain implements RouteGenerator {
    private CycleGenerator cycleGenerator;
    private IteratedLocalSearch ils;

    public RouteGeneratorMain(CycleGenerator cycleGenerator, IteratedLocalSearch iteratedLocalSearch) {
        this.cycleGenerator = cycleGenerator;
        this.ils = iteratedLocalSearch;
    }

    /**
     *
     * @param coords
     * @param distance
     * @return
     * @throws PathNotGeneratedException
     */
    @Override
    public PathTuple generateRoute(double[] coords, double distance) throws PathNotGeneratedException {
        PathTuple initialCycle = cycleGenerator.generateCycle(coords, distance);
        double remainingDistance = distance - initialCycle.getTotalLength();

        return ils.iterate(initialCycle, remainingDistance);
    }
}
