package com.lee.runrouter.routegenerator.cyclegenerator;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Combines results of two greedy search algorithms to form a cycle.
 * Where the return path algorithm fails to produce a valid result,
 * the outbound route is reversed and appended to form a cycle.
 * The resulting cycle is used as input for the iterated local search
 * algorithm.
 */
@Component
@Qualifier("CycleGeneratorMain")
public class CycleGeneratorMain implements CycleGenerator {
    private GraphSearch initialOutwardsPather; // generates the initial path
    // away from the origin point
    private GraphSearch initialReturnPather; // generates the initial path
    // back to the origin point
    private ElementRepo repo; // the repository of created Ways and Nodes

    @Autowired
    public CycleGeneratorMain(@Qualifier("BeamSearch") GraphSearch initialOutwardsPather,
                              @Qualifier("BeamSearchReturnPath")  GraphSearch initialReturnPather,
                              ElementRepo repo) {
        this.initialOutwardsPather = initialOutwardsPather;
        this.initialReturnPather = initialReturnPather;
        this.repo = repo;
    }

    /**
     * Creates the required cycle
     * @param coords starting positon of the route
     * @param distance distance to travel
     * @return a PathTuple, which is the head of a linked list
     * // containing PathTuples corresponding to previously
     * visited locations
     */
    public PathTuple generateCycle(double[] coords, double distance) throws PathNotGeneratedException {
        distance /= 2; // half distance for outward and return paths

       PathTuple outwardPath =
                initialOutwardsPather.searchGraph(repo.getOriginWay(), coords, distance);

        if (outwardPath.getSegmentLength() == -1) {
            throw new PathNotGeneratedException("No valid path was generated");
        }

        // the head of the generated outward path is the last  visited
        // location
        Way lastVisited = outwardPath.getCurrentWay();
        double[] lastVisitedCoords = {outwardPath.getPreviousNode().getLat(),
                outwardPath.getPreviousNode().getLon()};

        // find a path from the last visited location back to the origin
        PathTuple returnPath = initialReturnPather.searchGraph(lastVisited, lastVisitedCoords, distance);

        // if the returned PathTuple has a length of -1, the algorithm failed to find
        // a valid return route, so the initial route is reversed and appended
        if (returnPath.getTotalLength() == -1) {
            System.out.println("Failed to generate return route");
            return makeReverseRoute(outwardPath);
        }

        // update the distance of the return route by adding
        // distance of outbound route
        double halfDistance = outwardPath.getTotalLength();
        PathTuple tail = returnPath;
        while (tail.getPredecessor().getPredecessor() != null) {
            tail.setTotalLength(tail.getTotalLength() + halfDistance);
            tail = tail.getPredecessor();
        }

        // link outbound and return journeys
        tail.setPredecessor(outwardPath);

        System.out.println(returnPath.getTotalLength());
        return returnPath;
    }

    public PathTuple makeReverseRoute(PathTuple outwardPath) {
        PathTuple head = outwardPath;
        PathTuple current = outwardPath;

        double distance = head.getTotalLength();
        while (current != null) {

            // skip duplicating the connecting node between outward and return path
            if (current.getPreviousNode().getId() != head.getPreviousNode().getId()) {

                distance += current.getSegmentLength();

                PathTuple toAdd = new PathTupleMain(head, current.getPreviousNode(),
                        current.getCurrentWay(),
                        current.getSegmentScore(), current.getSegmentLength(), distance
                );

                head = toAdd;
            }
            current = current.getPredecessor();
        }

        // Head's length must be updated to reflect final stretch of the journey
        head = new PathTupleMain(head.getPredecessor(), head.getPreviousNode(), head.getCurrentWay(),
        head.getSegmentScore(), head.getPredecessor().getSegmentLength(),
                head.getPredecessor().getSegmentLength() + head.getPredecessor().getTotalLength());

        System.out.println(head.getTotalLength());
        return head;
    }
}
