package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ExecutorMainTest {
    private Executor executor;
    private RouteGenerator routeGenerator;
    private GraphBuilder graphBuilder;
    private FeaturesHeuristic featuresHeuristic;
    private ElevationHeuristic elevationHeuristic;
    private LinkedListToArray linkedListToArray;
    private final double DISTANCE = 21000;
    private final boolean[] OPTIONS = {true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true};

    @Before
    public void setUp() {
        this.routeGenerator = mock(RouteGeneratorMain.class);
        this.graphBuilder = mock(GraphBuilder.class);
        this.featuresHeuristic = mock(FeaturesHeuristicMain.class);
        this.elevationHeuristic = mock(ElevationHeuristicMain.class);
        this.linkedListToArray = mock(LinkedListToArray.class);
        this.executor = new ExecutorMain(routeGenerator, graphBuilder, featuresHeuristic,
                                          elevationHeuristic, linkedListToArray);
    }

    @Test
    public void testInputOne() {
        double[] coords = {50, 0};

        boolean called = false;
        executor.executeInitialGraphBuild(coords);
        verify(graphBuilder, atLeastOnce()).buildGraph(coords, DISTANCE, OPTIONS);
    }


    @Test
    public void testInputTwo() {
        double[] coords = {55, -2};

        boolean called = false;
        executor.executeInitialGraphBuild(coords);
        verify(graphBuilder, atLeastOnce()).buildGraph(coords, DISTANCE, OPTIONS);
    }
}