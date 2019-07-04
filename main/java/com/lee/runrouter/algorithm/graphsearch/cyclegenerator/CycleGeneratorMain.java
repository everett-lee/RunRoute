package com.lee.runrouter.algorithm.graphsearch.cyclegenerator;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

/**
 * Combines results of two greedy searhc algorithms to form a cycle.
 * Where the return path algorithm fails to produce a valid result,
 * the outbound route is reversed and appended to form a cycle.
 * The resulting cycle is used as input for the iterated local search
 * algorithm.
 */
public class CycleGeneratorMain implements CycleGenerator {
    private GraphSearch initialOutwardsPather; // generates the initial path
    // away from the origin point
    private GraphSearch initialReturnPather; // generates the initial path
    // back to the origin point
    private ElementRepo repo; // the repository of created Ways and Nodes

    public CycleGeneratorMain(GraphSearch initialOutwardsPather, GraphSearch initialReturnPather,
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
     * // of visited locations
     */
    public PathTuple generateCycle(double[] coords, double distance) throws Exception {
        distance /= 2; // half distance for outward and return paths

        PathTuple outwardPath =
                initialOutwardsPather.searchGraph(repo.getOriginWay(), coords, distance);

        if (outwardPath.getSegmentLength() == -1) {
            throw new Exception("No valid path was generated");
        }
        Way lastVisited = outwardPath.getCurrentWay();
        double[] lastVisitedCoords = {outwardPath.getPreviousNode().getLat(),
                outwardPath.getPreviousNode().getLon()};

        PathTuple returnPath = initialReturnPather.searchGraph(lastVisited, lastVisitedCoords, distance);

        // if the returned PathTuple has a length of -1, the algorithm failed to find
        // a valid return route
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

        return head;
    }
}
