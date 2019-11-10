package com.lee.runrouter.executor;


import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.SearchAlgorithm;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.routegenerator.RouteGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Receives input starting coordinates and distances and
 * passes them to the graph generation and graph search
 * routines.
 */
@Component
@Qualifier("ExecutorMain")
public class ExecutorMain implements Executor {
    private RouteGenerator routeGenerator;
    private GraphBuilder graphBuilder;
    private LinkedListToArray linkedListToArray;
    private SearchAlgorithm pather;
    private SearchAlgorithm connectionPather;
    private CycleRemover cycleRemover;

    private final double INITIAL_GRAPH_SIZE = 5000; // size (in KM^2) of requested graph

    public ExecutorMain(
            @Qualifier("RouteGeneratorMain") RouteGenerator routeGenerator,
            GraphBuilder graphBuilder,
            @Qualifier("LinkedListToArrayAllNodes") LinkedListToArray linkedListToArray,
            @Qualifier("BFS") SearchAlgorithm pather,
            @Qualifier("BFSConnectionPath") SearchAlgorithm connectionPather,
            CycleRemover cycleRemover) {
        this.routeGenerator = routeGenerator;
        this.graphBuilder = graphBuilder;
        this.linkedListToArray = linkedListToArray;
        this.pather = pather;
        this.connectionPather = connectionPather;
        this.cycleRemover = cycleRemover;
    }

    /**
     * Receive the route parameters from the client and generate the required route.
     * The coordinates and distance are first used to build a graph containing
     * information for the required area. The other parameters are then passed to the
     * route generation class.
     *
     * @param coords the user's initial coordinates
     * @param maxGradient the maximum incline requested
     * @param distance the target route distance
     * @param options an array of booleans representing options
     *                selected by the user
     * @return An array of nodes visited in the course of the route. These
     *         are sent in JSON format the client
     * @throws PathNotGeneratedException where a suitable route could not
     *         generated
     */
    @Override
    public ResponseObject executeQuery(double[] coords, double maxGradient, double distance, boolean[] options)
            throws PathNotGeneratedException {

        // update features heuristic to reflect user-defined options
        processFeaturesOptions(options);

        // update elevation heuristic to reflect user-defined options
        processElevationOptions(maxGradient, options);

        this.graphBuilder.buildGraph(coords, distance, processRoadOptions(options));
        System.out.println("graph built");

        PathTuple route = this.routeGenerator.generateRoute(coords, distance);

        PathTuple tail = route;
        double averageGradient = 0;

        int count = 0;
        // get reference to tail and calculate average gradient
        while (tail.getPredecessor() != null) {
            count++;
            averageGradient += tail.getSegmentGradient();
            tail = tail.getPredecessor();
        }
        averageGradient /= count;

        double length = tail.getTotalLength(); // get total route length from the tail

        // convert the way to an Array
        List<Node> pathNodes = linkedListToArray.convert(route);

        String startingWay = route.getCurrentWay().getName();

        // return as object suitable for marshalling as HTTP response
        return new ResponseObject(pathNodes, length, startingWay, averageGradient);
    }

    /**
     * Build a graph on receiving the user's initial position from the client.
     * This serves to speed up the graph generation stage of later queries
     *
     * @param coords the user's initial coordinates
     */
    @Override
    public void executeInitialGraphBuild(double[] coords) {
        boolean[] roadOptions = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};

        this.graphBuilder.buildGraph(coords, INITIAL_GRAPH_SIZE, roadOptions);
    }

    // create a boolean array reflecting the users preference selections
    private boolean[] processRoadOptions(boolean[] options) {
        boolean[] roadOptions = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};

        // user selected avoid major roads
        if (options[0]) {
            roadOptions[0] = false;
            roadOptions[1] = false;
        }

        // user selected avoid steps
        if (options[1]) {
            roadOptions[13] = false;
        }

        return roadOptions;
    }

    private void processFeaturesOptions(boolean[] options) {
        // set all options to false/empty
        this.pather.setAvoidUnlit(false);
        this.connectionPather.setAvoidUnlit(false);

        List<String> preferredHighways = new ArrayList<>();
        List<String> preferredSurfaces = new ArrayList<>();
        List<String> dislikedSurfaces = new ArrayList<>();

        // user selected avoid concrete
        if (options[2]) {
            dislikedSurfaces.add(Way.Surface.CONCRETE.toString());
            dislikedSurfaces.add(Way.Surface.ASPHALT.toString());
        }

        // user selected avoid unlit
        if (options[3]) {
            this.pather.setAvoidUnlit(true);
            this.connectionPather.setAvoidUnlit(true);

        }

        // user selected prefer residential roads
        if (options[5]) {
            preferredHighways.add(Way.Highway.LIVING_STREET.toString());
            preferredHighways.add(Way.Highway.RESIDENTIAL.toString());
        }


        // user selected prefer unpaved surfaces
        if (options[6]) {
            preferredSurfaces.add(Way.Surface.DIRT.toString());
            preferredSurfaces.add(Way.Surface.GRASS.toString());
            preferredSurfaces.add(Way.Surface.GROUND.toString());
            preferredSurfaces.add(Way.Surface.EARTH.toString());
            preferredSurfaces.add(Way.Surface.SAND.toString());
            preferredSurfaces.add(Way.Surface.UNPAVED.toString());
        }

        // user selected prefer backroads and pathways
        if (options[7]) {
            preferredHighways.add(Way.Highway.CYCLEWAY.toString());
            preferredHighways.add(Way.Highway.BRIDLEWAY.toString());
            preferredHighways.add(Way.Highway.FOOTWAY.toString());
            preferredHighways.add(Way.Highway.PATH.toString());
            preferredHighways.add(Way.Highway.TRACK.toString());
        }

        this.pather.setFeaturesHeuristicOptions(preferredHighways, preferredSurfaces, dislikedSurfaces);
        this.connectionPather.setFeaturesHeuristicOptions(preferredHighways, preferredSurfaces, dislikedSurfaces);
    }

    private void processElevationOptions(double maxGradient, boolean[] booleans) {
        this.pather.setMaxGradient(maxGradient);
        this.connectionPather.setMaxGradient(maxGradient);
        this.pather.setElevationHeuristicOptions(false);
        this.connectionPather.setElevationHeuristicOptions(false);

        // user select prefer uphill
        if (booleans[4]) {
            this.pather.setElevationHeuristicOptions(true);
            this.connectionPather.setElevationHeuristicOptions(true);
        }
    }

}
