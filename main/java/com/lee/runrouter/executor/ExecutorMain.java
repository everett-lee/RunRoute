package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.routegenerator.RouteGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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

    public ExecutorMain(
            @Qualifier("RouteGeneratorMain") RouteGenerator routeGenerator,
            GraphBuilder graphBuilder) {
        this.routeGenerator = routeGenerator;
        this.graphBuilder = graphBuilder;
    }

    @Override
    public List<Node> executeQuery(double[] coords, double distance, boolean[] options)
            throws PathNotGeneratedException {

       graphBuilder.buildGraph(coords, distance, options);
        System.out.println("GRAPH BUILT");
        PathTuple route = routeGenerator.generateRoute(coords, distance);

        return convertLinkedListToList(route);
    }

    private List<Node> convertLinkedListToList(PathTuple head) {
        List<Node> nodes = new ArrayList<>();

        while (head != null) {
            nodes.add(head.getPreviousNode());
            head = head.getPredecessor();
        }
        return nodes;
    }
}
