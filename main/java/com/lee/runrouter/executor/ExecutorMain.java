package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.routegenerator.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.routegenerator.RouteGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Receives input starting coordinates and distances and executes
 * algorithm and graph generation procedures.
 */
@Component
@Qualifier("ExecutorMain")
public class ExecutorMain implements Executor {
    private RouteGenerator routeGenerator;
    private GraphBuilder graphBuilder;
    private FeaturesHeuristic featuresHeuristic;
    private ElevationHeuristic elevationHeuristic;

    public ExecutorMain(
            @Qualifier("RouteGeneratorMain") RouteGenerator routeGenerator,
            GraphBuilder graphBuilder,
            @Qualifier("FeaturesHeuristicMain") FeaturesHeuristic featuresHeuristic,
            @Qualifier("ElevationHeuristicMain") ElevationHeuristic elevationHeuristic) {
        this.routeGenerator = routeGenerator;
        this.graphBuilder = graphBuilder;
        this.featuresHeuristic = featuresHeuristic;
        this.elevationHeuristic = elevationHeuristic;
    }

    @Override
    public List<Node> executeQuery(double[] coords, double maxGradient, double distance, boolean[] options)
            throws PathNotGeneratedException {

        // update features heuristic to reflect user-defined options
        processFeaturesOptions(options);

        // update elevation heuristic to reflect user-defined options
        processElevationOptions(maxGradient, options);

        this.graphBuilder.buildGraph(coords, distance, processRoadOptions(options));

        System.out.println("GRAPH BUILT");
        PathTuple route = this.routeGenerator.generateRoute(coords, distance);

        return convertLinkedListToList(route);
    }

    // convert the returned route to an ArrayList, ready to be
    // sent to the client as JSON
    private List<Node> convertLinkedListToList(PathTuple head) {
        List<Node> nodes = new ArrayList<>();

        while (head != null) {
            nodes.add(head.getPreviousNode());
            head = head.getPredecessor();
        }
        return nodes;
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
        List<String> preferredHighways = new ArrayList<>();
        List<String> preferredSurfaces = new ArrayList<>();
        List<String> dislikedSurfaces = new ArrayList<>();

        // user selected avoid concrete
        if (options[2]) {
            dislikedSurfaces.add(Way.Surface.CONCRETE.toString());
        }

        // user selected avoid unlit
        if (options[3]) {
            this.featuresHeuristic.setAvoidUnlit(true);
        }

        // user selected prefer grass or dirt surfaces
        if (options[5]) {
            preferredSurfaces.add(Way.Surface.DIRT.toString());
            preferredSurfaces.add(Way.Surface.GRASS.toString());
            preferredSurfaces.add(Way.Surface.UNPAVED.toString());
        }

        // user selected prefer residential roads
        if (options[6]) {
            preferredHighways.add(Way.Highway.LIVING_STREET.toString());
            preferredHighways.add(Way.Highway.RESIDENTIAL.toString());
        }

        // user selected prefer backroads and pathways
        if (options[7]) {
            preferredHighways.add(Way.Highway.CYCLEWAY.toString());
            preferredHighways.add(Way.Highway.BRIDLEWAY.toString());
            preferredHighways.add(Way.Highway.FOOTWAY.toString());
            preferredHighways.add(Way.Highway.PATH.toString());
            preferredHighways.add(Way.Highway.TRACK.toString());
        }

        this.featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
        this.featuresHeuristic.setDislikedSurfaces(dislikedSurfaces);
        this.featuresHeuristic.setPreferredHighways(preferredHighways);
    }

    private void processElevationOptions(double maxGradient, boolean[] booleans) {
        elevationHeuristic.setMaxGradient(maxGradient);

        // user select prefer uphill
        if (booleans[4]) {
            elevationHeuristic.setOptions(true);
        }
    }
}
