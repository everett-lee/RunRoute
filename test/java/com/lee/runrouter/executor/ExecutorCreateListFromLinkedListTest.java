package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristicMain;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.lang.reflect.*;
import java.util.List;

import static org.junit.Assert.*;
import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.mockito.Mockito.*;

public class ExecutorCreateListFromLinkedListTest {
    private PathTuple morrishRoad;
    private PathTuple craigNair;
    private Executor rgi;
    private RouteGenerator rg;
    private GraphBuilder gb;
    private FeaturesHeuristic featuresHeuristic;
    private ElevationHeuristic elevationHeuristic;
    Method convertList;

    {
        morrishRoad = getMorrishShort();
        craigNair = getCraignair();
    }

    @Before
    public void setUp() {
        rg = mock(RouteGeneratorMain.class);
        gb = mock(GraphBuilder.class);
        featuresHeuristic = new FeaturesHeuristicMain();
        elevationHeuristic = new ElevationHeuristicMain();
        rgi = new ExecutorMain(rg, gb, featuresHeuristic, elevationHeuristic);

       convertList = Arrays.stream(rgi.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("convertLinkedListToList")).findFirst().get();
        convertList.setAccessible(true);
    }

    @Test
    public void testMorrishConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = (List<Node>) convertList.invoke(rgi, morrishRoad);

        long expectedId = morrishRoad.getPreviousNode().getId();
        int expectedSize = getNumberofNodes(morrishRoad);

    }
    @Test
    public void testCraignairConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = (List<Node>) convertList.invoke(rgi, craigNair);

        long expectedId = craigNair.getPreviousNode().getId();
        int expectedSize = getNumberofNodes(craigNair);

        assertEquals(expectedSize, result.size());
        assertEquals(expectedId, result.get(0).getId());
        assertEquals(expectedId, result.get(expectedSize-1).getId());
    }


}