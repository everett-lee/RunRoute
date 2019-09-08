//package com.lee.runrouter.executor;
//
//
//import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.SearchAlgorithm;
//import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
//import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
//import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
//import com.lee.runrouter.routegenerator.PathNotGeneratedException;
//import com.lee.runrouter.algorithm.pathnode.PathTuple;
//import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
//import com.lee.runrouter.graph.graphbuilder.node.Node;
//import com.lee.runrouter.routegenerator.RouteGenerator;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Receives input starting coordinates and distances and executes
// * algorithm and graph generation procedures.
// */
//@Component
//@Qualifier("ExecutorToDelete")
//public class ExecutorToDelete implements Executor {
//    private RouteGenerator routeGenerator;
//    private GraphBuilder graphBuilder;
//    private FeaturesHeuristic featuresHeuristic;
//    private ElevationHeuristic elevationHeuristic;
//    private LinkedListToArray linkedListToArray;
//    private SearchAlgorithm outwardsPather;
//    private SearchAlgorithm returnPather;
//    private SearchAlgorithm connectionPather;
//
//    private final double INITIAL_GRAPH_SIZE = 8000; // the starting size of the generated graph
//
//    public ExecutorToDelete(
//            @Qualifier("RouteGeneratorMain") RouteGenerator routeGenerator,
//            GraphBuilder graphBuilder,
//            @Qualifier("FeaturesHeuristicMain") FeaturesHeuristic featuresHeuristic,
//            @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic,
//            @Qualifier("LinkedListToArrayAllNodes") LinkedListToArray linkedListToArray,
//            @Qualifier("BeamSearch") SearchAlgorithm outwardsPather,
//            @Qualifier("BFSConnectionPath") SearchAlgorithm connectionPather) {
//        this.routeGenerator = routeGenerator;
//        this.graphBuilder = graphBuilder;
//        this.featuresHeuristic = featuresHeuristic;
//        this.elevationHeuristic = elevationHeuristic;
//        this.linkedListToArray = linkedListToArray;
//        this.outwardsPather = outwardsPather;
//        this.returnPather = returnPather;
//        this.connectionPather = connectionPather;
//    }
//
//    /**
//     * Receive the route paramaters from the client and generate the required route.
//     * The coordinates and distance are first used to build a graph containing
//     * information for the required area. The other parameters are then passed to the
//     * route generation class.
//     *
//     * @param coords the user's initial coordinates
//     * @param maxGradient the maximum incline requested
//     * @param distance the target route distance
//     * @param options an array of booleans representing options
//     *                selected by the user
//     * @return An array of nodes visited in the course of the route. These
//     *         are sent in JSON format the client
//     * @throws PathNotGeneratedException where a suitable route could not
//     *         generated
//     */
//    @Override
//    public ResponseObject executeQuery(double[] coords, double maxGradient, double distance, boolean[] options)
//            throws PathNotGeneratedException {
//        // update features heuristic to reflect user-defined options
//        processFeaturesOptions(options);
//
//        // update elevation heuristic to reflect user-defined options
//        processElevationOptions(maxGradient, options);
//
//        this.graphBuilder.buildGraph(coords, distance, processRoadOptions(options));
//
//        System.out.println("GRAPH BUILT");
//        PathTuple route = this.routeGenerator.generateRoute(coords, distance);
//
//        PathTuple tail = route;
//        double averageGradient = 0;
//        int count = 0;
//
//        // get reference to tail and calculate average gradient
//        while (tail.getPredecessor() != null) {
//            count++;
//            averageGradient += tail.getSegmentGradient();
//            tail = tail.getPredecessor();
//        }
//        averageGradient /= count;
//        double length = tail.getTotalLength(); // get total route length from the tail
//
//        List<Node> pathNodes = linkedListToArray.convert(route);
//        String startingWay = route.getCurrentWay().getName();
//
//        return new ResponseObject(pathNodes, length, startingWay, averageGradient);
//    }
//
//    /**
//     * Build a graph suited to a 21km route on receiving the user's initial
//     * position from the client. This serves to speed up the graph generation
//     * stage of later queries
//     *
//     * @param coords the user's initial coordinates
//     */
//    @Override
//    public void executeInitialGraphBuild(double[] coords) {
//        boolean[] roadOptions = {true, true, true, true, true, true, true,
//                true, true, true, true, true, true, true, true};
//
//        this.graphBuilder.buildGraph(coords, INITIAL_GRAPH_SIZE, roadOptions);
//    }
//
//    // create a boolean array reflecting the users preference selections
//    private boolean[] processRoadOptions(boolean[] options) {
//        boolean[] roadOptions = {true, true, true, true, true, true, true,
//                true, true, true, true, true, true, true, true};
//
//        // user selected avoid major roads
//        if (options[0]) {
//            roadOptions[0] = false;
//            roadOptions[1] = false;
//        }
//
//        // user selected avoid steps
//        if (options[1]) {
//            roadOptions[13] = false;
//        }
//
//        return roadOptions;
//    }
//
//    private void processFeaturesOptions(boolean[] options) {
//        // set all options to false/empty
//        this.outwardsPather.setAvoidUnlit(false);
//        this.returnPather.setAvoidUnlit(false);
//        this.connectionPather.setAvoidUnlit(false);
//
//        List<String> preferredHighways = new ArrayList<>();
//        List<String> preferredSurfaces = new ArrayList<>();
//        List<String> dislikedSurfaces = new ArrayList<>();
//
//        // user selected avoid concrete
//        if (options[2]) {
//            dislikedSurfaces.add(Way.Surface.CONCRETE.toString());
//        }
//
//        // user selected avoid unlit
//        if (options[3]) {
//            this.outwardsPather.setAvoidUnlit(true);
//            this.returnPather.setAvoidUnlit(true);
//            this.connectionPather.setAvoidUnlit(true);
//        }
//
//        // user selected prefer residential roads
//        if (options[5]) {
//            preferredHighways.add(Way.Highway.LIVING_STREET.toString());
//            preferredHighways.add(Way.Highway.RESIDENTIAL.toString());
//        }
//
//
//        // user selected prefer grass or dirt surfaces
//        if (options[6]) {
//            preferredSurfaces.add(Way.Surface.DIRT.toString());
//            preferredSurfaces.add(Way.Surface.GRASS.toString());
//            preferredSurfaces.add(Way.Surface.UNPAVED.toString());
//        }
//
//        // user selected prefer backroads and pathways
//        if (options[7]) {
//            preferredHighways.add(Way.Highway.CYCLEWAY.toString());
//            preferredHighways.add(Way.Highway.BRIDLEWAY.toString());
//            preferredHighways.add(Way.Highway.FOOTWAY.toString());
//            preferredHighways.add(Way.Highway.PATH.toString());
//            preferredHighways.add(Way.Highway.TRACK.toString());
//        }
//
//        this.featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
//        this.featuresHeuristic.setDislikedSurfaces(dislikedSurfaces);
//        this.featuresHeuristic.setPreferredHighways(preferredHighways);
//    }
//
//    private void processElevationOptions(double maxGradient, boolean[] booleans) {
//        this.outwardsPather.setMaxGradient(maxGradient);
//        this.returnPather.setMaxGradient(maxGradient);
//        this.connectionPather.setMaxGradient(maxGradient);
//        elevationHeuristic.setOptions(false);
//
//        // user select prefer uphill
//        if (booleans[4]) {
//            elevationHeuristic.setOptions(true);
//        }
//    }
//
//    static public String returnPath(PathTuple tp, String acc) {
//        acc += "node(id:";
//        while (tp != null) {
//            acc += tp.getCurrentNode().getId() + ", ";
//            System.out.println("(" + tp.getCurrentNode() + " distance: "
//                    + tp.getTotalLength() + " score: " + tp.getSegmentScore() +
//                    ") " + " way: " + tp.getCurrentWay().getId());
//            System.out.println("Segment length: " + tp.getSegmentLength());
//            tp = tp.getPredecessor();
//
//        }
//        acc = acc.substring(0, acc.length()-2);
//        acc += ");\nout;";
//        return acc;
//    }
//}