package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ConnectionPair;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.graph.graphbuilder.node.NodeContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DepthFirstSearch implements GraphSearch {
    private ElementRepo repo;
    private Heuristic distanceFromOriginHeursitic;
    private Heuristic featuresHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;
    private ElevationHeuristic elevationHeuristic;

    private final double SCALE = 0.1; // amount to scale upper and lower bound on
    // run length by

    public DepthFirstSearch(ElementRepo repo, Heuristic distanceHeuristic,
                           Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator,
                           ElevationHeuristic elevationHeuristic) {
        this.repo = repo;
        this.distanceFromOriginHeursitic = distanceHeuristic;
        this.featuresHeuristic = featuresHeuristic;
        this.edgeDistanceCalculator = edgeDistanceCalculator;
        this.elevationHeuristic = elevationHeuristic;
    }


    @Override
    public PathTuple searchGraph(Way root, double[] coords, double distance) {
        distance *= 1000; // distance in meters

        double runLength;
        double score;
        double halfLength = distance / 2;
        double upperBound = distance + (distance * SCALE); // upper bound of
        // run length
        double lowerBound = distance - (distance * SCALE); // lower bound of
        // run length

        List<PathTuple> stack = new ArrayList<>();

        Node currentNode = new Node(0, coords[0], coords[1]);
        // set starting node to the member of the root Way that is closest to it
        currentNode = AlgoHelpers.findClosest(currentNode, root.getNodeContainer().getNodes());

        // add the root Way to to the queue, with predecessor set to null
        stack.add(new PathTupleMain(null, currentNode, repo.getOriginWay(), 0, 0));

        List<PathTuple> exploringStack = new ArrayList<>();

        while (!stack.isEmpty()) {
            PathTuple topTuple = stack.get(stack.size()-1);
            stack.remove(stack.size()-1);


            Way currentWay = topTuple.getCurrentWay();

            List<ConnectionPair> waystoExplore = new ArrayList<>();
            if (topTuple.getLength() > halfLength) {
                waystoExplore = repo.getConnectedWays(currentWay);
                waystoExplore.sort((Comparator.comparing((ConnectionPair x) -> distanceFromOriginHeursitic.getScore(
                                x.getConnectingNode(), x.getConnectingNode(), x.getConnectingWay()))));

                waystoExplore.subList(Math.max(waystoExplore.size() - 3, 0), waystoExplore.size());
            } else {
                waystoExplore = repo.getConnectedWays(currentWay);
            }

            for (ConnectionPair pair: waystoExplore) {
                runLength = topTuple.getLength();
                score = 0;
                currentNode = topTuple.getPreviousNode();
                Node connectingNode = pair.getConnectingNode();

                Way selectedWay = pair.getConnectingWay();


//                // if the run has exceeded its minimum length
//                if (topTuple.getLength() >= lowerBound) {
//                    // the route has returned to the origin
//                    System.out.println("name1: " + selectedWay.getName()
//                            + " name 2: " + repo.getOriginWay().getName());
//                    System.out.println("id 1: " + selectedWay.getId()
//                    + " id 2: " + repo.getOriginWay().getId());
//                    if (selectedWay.getId() == repo.getOriginWay().getId()) {
//                        System.out.println("Yoooooooo");
//
//                        PathTuple x = topTuple;
//                        while(x.getPredecessor()!=null) {
//                            System.out.println(x.getPreviousNode());
//                            x = x.getPredecessor();
//                        }
//
//
//                        return topTuple;
//                    }
//                }

                double distanceToNext = edgeDistanceCalculator
                        .calculateDistance(currentNode, connectingNode, currentWay);

                                if (runLength + distanceToNext > upperBound) {
                    continue; // skip to next where max length exceeded
                }
//
//                if (runLength + distanceToNext > halfLength) {
//                    double distanceScore = distanceFromOriginHeursitic.getScore(currentNode, connectingNode, selectedWay);
//                    if (distanceScore < 0.1) {
//                        continue;
//                    }
//                    score += distanceScore;
//                }

                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
                        score, runLength + distanceToNext);
                stack.add(toAdd);
            }

        }
        // error condition
        return new PathTupleMain(null, null, repo.getOriginWay(), -1, -1);
    }

    static void returnPath(PathTuple tp) {
        if (tp.getPredecessor() == null) {
            System.out.println();
            return;
        }

        System.out.print("(" + tp.getPreviousNode().getId() + " distance: " + tp.getLength() + ") ");
        returnPath(tp.getPredecessor());
    }
}
