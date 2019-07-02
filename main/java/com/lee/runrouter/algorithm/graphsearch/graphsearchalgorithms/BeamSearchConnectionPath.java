//package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;
//
//import com.lee.runrouter.algorithm.AlgoHelpers;
//import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
//import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
//import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
//import com.lee.runrouter.algorithm.heuristic.Heuristic;
//import com.lee.runrouter.algorithm.pathnode.PathTuple;
//import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
//import com.lee.runrouter.graph.elementrepo.ConnectionPair;
//import com.lee.runrouter.graph.elementrepo.ElementRepo;
//import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
//import com.lee.runrouter.graph.graphbuilder.node.Node;
//
//import java.util.*;
//
///**
// * Variant of the BFS algorithm that restricts the next selected Way
// * to those closer to the starting point than the previous. It is used
// * to complete the circuit and return to the route's starting position
// * following execution of the BFS.
// */
//public class BeamSearchConnectionPath implements ILSGraphSearch {
//    private ElementRepo repo; // the repository of Ways and Nodes
//    private Heuristic distanceFromOriginHeursitic;
//    private Heuristic featuresHeuristic;
//    private EdgeDistanceCalculator edgeDistanceCalculator;
//    private ElevationHeuristic elevationHeuristic;
//    private double currentRouteLength;
//
//    private double maxGradient = 0.8; // is user-defined
//    private final double REPEATED_EDGE_PENALTY = 2; // deducted from score where
//    // edge/Way has been previously visited
//    private final double RANDOM_REDUCER = 5; // divides into random number added to the
//    // score
//
//    private List<PathTuple> queue;
//    private final long TIME_LIMIT = 1000;
//
//
//    public BeamSearchConnectionPath(ElementRepo repo, Heuristic distanceHeuristic,
//                             Heuristic featuresHeuristic, EdgeDistanceCalculator edgeDistanceCalculator,
//                             ElevationHeuristic elevationHeuristic, DistanceCalculator distanceCalculator) {
//        this.repo = repo;
//        this.distanceFromOriginHeursitic = distanceHeuristic;
//        this.featuresHeuristic = featuresHeuristic;
//        this.edgeDistanceCalculator = edgeDistanceCalculator;
//        this.elevationHeuristic = elevationHeuristic;
//        this.currentRouteLength = 0;
//
//        // compare priority queue items by their assigned score in descending order
//        this.queue = new ArrayList<>();
//    }
//
//    @Override
//    public PathTuple searchGraph(Way root, double[] coords, double distance) {
//        Set<Long> visitedWays = new HashSet<>();
//        long startTime = System.currentTimeMillis();
//        long elapsedTime = 0L;
//        double upperBound = distance;
//
//        Node originNode = new Node(-1, coords[0], coords[1]);
//        originNode = AlgoHelpers.findClosest(originNode, root.getNodeContainer().getNodes());
//        queue.add(new PathTupleMain(null, originNode, root,
//                0, 0, 0));
//
//        while (!queue.isEmpty() && elapsedTime <= TIME_LIMIT)  {
//            elapsedTime = (new Date()).getTime() - startTime;
//            queue.sort(Comparator
//                    .comparing((PathTuple tuple) -> tuple.getSegmentScore()).reversed());
//
//            if (queue.size() > 10) {
//                queue = queue.subList(0, 10);
//            }
//
//            PathTuple topTuple = queue.get(0);
//            queue.remove(0);
//
//            Way currentWay = topTuple.getCurrentWay();
//            Node currentNode = topTuple.getPreviousNode();
//            double score;
//            currentRouteLength = topTuple.getTotalLength();
//
//            // the route has reached the target
//            if (topTuple.getCurrentWay().getId() == repo.getOriginWay().getId()) {
//
//                return topTuple;
//            }
//
//            // distance to origin point from the last explored way
//            double lastDist = distanceFromOriginHeursitic.getScore(currentWay);
//
//            // for each Way reachable from the current Way
//            for (ConnectionPair pair : repo.getConnectedWays(currentWay)) {
//                currentRouteLength = topTuple.getTotalLength();
//                score = 0;
//                currentNode = topTuple.getPreviousNode();
//                Node connectingNode = pair.getConnectingNode();
//                Way selectedWay = pair.getConnectingWay();
//
//                double distanceToNext = edgeDistanceCalculator
//                        .calculateDistance(currentNode, connectingNode, currentWay);
//
//                if (currentRouteLength + distanceToNext > upperBound) {
//                    continue; // skip to next where max length exceeded
//                }
//
//                double currentDistanceScore
//                        = distanceFromOriginHeursitic.getScore(selectedWay);
//
//                // if the current distance score is less than the previous Way's, that
//                // is it is further away, then skip this iteration
//                if (currentDistanceScore < lastDist) {
//                    continue;
//                }
//
//                // drop the score where this way has already been explored
//                if (visitedWays.contains(currentWay.getId())) {
//                    score -= REPEATED_EDGE_PENALTY;
//                }
//
//                if (distanceToNext < 100) {
//                    score -= 1;
//                }
//
//                visitedWays.add(currentWay.getId());
//
//                // add score reflecting correspondence of terrain features to user selectionss
//                score += featuresHeuristic.getScore(selectedWay);
//
//                // add a small random value to break ties
//                score += (Math.random() / RANDOM_REDUCER);
//
//                PathTuple toAdd = new PathTupleMain(topTuple, connectingNode, selectedWay,
//                        score, distanceToNext, currentRouteLength + distanceToNext);
//                queue.add(toAdd);
//            }
//        }
//
//        return new PathTupleMain(null, null, null, Double.MIN_VALUE,
//                -1, -1);
//    }
//
//    @Override
//    public void setCurrentDistance(double distance) {
//        this.currentRouteLength = distance;
//    }
//
//    @Override
//    public void setTarget(Node targetNode, Way targetWay) {
//        repo.setOriginWay(targetWay);
//        repo.setOriginNode(targetNode);
//    }
//}
