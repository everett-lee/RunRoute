package com.lee.runrouter.routegenerator;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGenerator;
import com.lee.runrouter.routegenerator.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch.IteratedLocalSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Uses the starting coordinates, run length and options sent
 * from the client to generate the initial route. This is
 * then used provided as input to the iterated local search
 * to yield the improved final route.
 */
@Component
@Qualifier("RouteGeneratorMain")
public class RouteGeneratorMain implements RouteGenerator {
    private CycleGenerator cycleGenerator;
    private IteratedLocalSearch ils;

    public RouteGeneratorMain(@Qualifier("CycleGeneratorMain") CycleGenerator cycleGenerator,
                              @Qualifier("IteratedLocalSearchMain")IteratedLocalSearch iteratedLocalSearch) {
        this.cycleGenerator = cycleGenerator;
        this.ils = iteratedLocalSearch;
    }

    /**
     * Generates the required circular route
     *
     * @param coords the coordinates of the route's starting point
     * @param distance the distance to run
     * @return a PathTuple which is the head of a linked list of
     *         visited locations
     * @throws PathNotGeneratedException where the cycle generator
     *         is unable to generate the initial route
     */
    @Override
    public PathTuple generateRoute(double[] coords, double distance) throws PathNotGeneratedException {

        PathTuple initialCycle = cycleGenerator.generateCycle(coords, distance);
        System.out.println("INITIAL CYCLE DONE");

        System.out.println("INITIAL CYCLE LEN " + initialCycle.getTotalLength());

        double remainingDistance = distance - initialCycle.getTotalLength();


        System.out.println(" <><><><><> THEERE IS " + remainingDistance + "TO ADD <> <> <><><><>");

        return ils.iterate(initialCycle, remainingDistance);
    }
}
