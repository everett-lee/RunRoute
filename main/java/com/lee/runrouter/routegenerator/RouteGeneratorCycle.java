package com.lee.runrouter.routegenerator;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.routegenerator.cyclegenerator.CycleGenerator;
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
@Qualifier("RouteGeneratorCycle")
public class RouteGeneratorCycle implements RouteGenerator {
    private GraphSearch pather;
    private ILSGraphSearch connectionPather;
    private IteratedLocalSearch ils;
    private ElementRepo repo;
    private int MAX_ATTEMPTS = 5;

    public RouteGeneratorCycle(@Qualifier("BeamSearchCycle") GraphSearch pather,
                               @Qualifier("IteratedLocalSearchMain")IteratedLocalSearch iteratedLocalSearch,
                                @Qualifier("BFSConnectionPath") ILSGraphSearch connectionPather,
                                ElementRepo repo) {
        this.pather = pather;
        this.ils = iteratedLocalSearch;
        this.connectionPather = connectionPather;
        this.repo = repo;
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

        PathTuple initialCycle = pather.searchGraph(repo.getOriginWay(), coords, distance);
        System.out.println("INITIAL CYCLE DONE");

        int attempts = 1;
        // if a valid route was not generated
        if (initialCycle.getTotalLength() == -1) {
            // reduce the algorithm run time
            pather.setTimeLimit(500);
            // reattempt until a valid route is generated or max attempts made
            while (attempts < MAX_ATTEMPTS && initialCycle.getTotalLength() == -1) {
                initialCycle = pather.searchGraph(repo.getOriginWay(), coords, distance);
                attempts++;
            }
        }

        if (initialCycle.getTotalLength() == -1) {
            throw new PathNotGeneratedException("No valid path was generated");
        }

        System.out.println("INITIAL CYCLE LEN " + initialCycle.getTotalLength());

        setMinimumPathPercentage(distance);
        double remainingDistance = distance - initialCycle.getTotalLength();

        System.out.println(" <><><><><> THEERE IS " + remainingDistance + "TO ADD <> <> <><><><>");

        return ils.iterate(initialCycle, remainingDistance);
    }

    // increase the minimum length of the path added by the ILS algorithm in line
    // with the total distance travelled.
    private void setMinimumPathPercentage(double distance) {
        if (distance > 10000 && distance < 15000) {
            connectionPather.setMinimumPathPercentage(0.6);
        }

        if (distance > 15000) {
            connectionPather.setMinimumPathPercentage(0.6);
        }
    }
}
