package com.lee.runrouter.algorithm.graphsearch.cyclegenerator;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.GraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;

import java.util.Arrays;

public class CycleGeneratorMain implements CycleGenerator {
    private GraphSearch initialOutwardsPather; // generates the initial path
    // away from the origin point
    private GraphSearch initialReturnPather; // generates the initial path
    // back to the origin point
    private ElementRepo repo;

    public CycleGeneratorMain(GraphSearch initialOutwardsPather, GraphSearch initialReturnPather,
                          ElementRepo repo) {
        this.initialOutwardsPather = initialOutwardsPather;
        this.initialReturnPather = initialReturnPather;
        this.repo = repo;
    }

    public PathTuple generateCycle(double[] coords, double distance) {
        distance /= 2; // half distance for outward and return paths

        PathTuple outwardPath =
                initialOutwardsPather.searchGraph(repo.getOriginWay(), coords, distance);

        Way lastVisited = outwardPath.getCurrentWay();

        System.out.println("first half done");

        double[] lastVisitedCoords = {outwardPath.getPreviousNode().getLat(),
                outwardPath.getPreviousNode().getLon()};

        System.out.println(Arrays.toString(lastVisitedCoords));

        PathTuple returnPath = initialReturnPather.searchGraph(lastVisited, lastVisitedCoords, distance);



        if (returnPath.getTotalLength() == -1) {
            System.out.println("it happended");
            return makeReverseRoute(outwardPath);
        }

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
