package com.lee.runrouter.routegenerator;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch.IteratedLocalSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Uses the starting coordinates, run length and options sent
 * from the client to generate the initial route by generating a series
 * of initial routes, sorting them by score, and returning the best one.
 * This is then used provided as input to the iterated local search
 * to yield the improved final route.
 */
@Component
@Qualifier("RouteGeneratorCycle")
public class RouteGeneratorCycle implements RouteGenerator {
    private GraphSearch pather;
    private ILSGraphSearch connectionPather;
    private IteratedLocalSearch ils;
    private ElementRepo repo;
    private int MAX_ATTEMPTS = 4; // maximum number of initial routes to generate

    public RouteGeneratorCycle(@Qualifier("BFS") GraphSearch pather,
                               @Qualifier("IteratedLocalSearchMain") IteratedLocalSearch iteratedLocalSearch,
                               @Qualifier("DFSConnectionPath") ILSGraphSearch connectionPather,
                               ElementRepo repo) {
        this.pather = pather;
        this.ils = iteratedLocalSearch;
        this.connectionPather = connectionPather;
        this.repo = repo;
    }

    /**
     * Generates the required circular route
     *
     * @param coords   the coordinates of the route's starting point
     * @param distance the distance to run
     * @return a PathTuple which is the head of a linked list of
     * visited locations
     * @throws PathNotGeneratedException where the cycle generator
     *                                   is unable to generate the initial route
     */
    @Override
    public PathTuple generateRoute(double[] coords, double distance) throws PathNotGeneratedException {
        int attempts = 1;
        ArrayList<PathTuple> results = new ArrayList<>();
        PathTuple initialCycle = pather.searchGraph(repo.getOriginWay(), coords, distance);
        results.add(initialCycle);

        System.out.println("INITIAL CYCLE DONE");

        {
            // reduce the algorithm run time
            pather.setTimeLimit(500);
            // continue to generate until max attempts made
            while (attempts < MAX_ATTEMPTS) {
                initialCycle = pather.searchGraph(repo.getOriginWay(), coords, distance);
                results.add(initialCycle);
                attempts++;
            }
        }

        // sort the results by their difference to the target, then by score
        results.sort(Comparator
                .comparing((PathTuple tuple) -> AlgoHelpers.calculateScore(tuple)).reversed());


        results.stream()
                .mapToDouble((PathTuple tuple) -> AlgoHelpers.calculateScore(tuple))
                .forEach(x -> System.out.println(x));

        initialCycle = results.get(0);

        if (initialCycle.getTotalLength() == -1) {
            throw new PathNotGeneratedException("No valid path was generated");
        }

        System.out.println("INITIAL CYCLE LEN " + initialCycle.getTotalLength());

        double remainingDistance = distance - initialCycle.getTotalLength();

        System.out.println(" <><><><><> THEERE IS " + remainingDistance + "TO ADD <> <> <><><><>");

        return ils.iterate(initialCycle, remainingDistance);
    }
}
