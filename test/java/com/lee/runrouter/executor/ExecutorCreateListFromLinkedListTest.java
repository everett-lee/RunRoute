package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristicMain;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
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
    private ElementRepo repo;
    private LinkedListToArray linkedListToArray;
    private LinkedListToArray linkedListToArrayNodes;

    {
        morrishRoad = getMorrishShort();
        craigNair = getCraignair();
        repo = getRepo();
    }

    @Before
    public void setUp() {
       linkedListToArray = new LinkedListToArrayAllNodes(repo);
       linkedListToArrayNodes = new LinkedListToArrayHeadNodes();
    }

    @Test
    public void testMorrishConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = linkedListToArray.convert(morrishRoad);
        //result = linkedListToArrayNodes.convert(morrishRoad);


        result.forEach(x -> System.out.println(x.getId()));

        assertTrue(result.get(0).getId() == result.get(result.size()-1).getId());



    }
    @Test
    public void testCraignairConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = (List<Node>) linkedListToArray.convert(craigNair);


        assertTrue(result.get(0).getId() == result.get(result.size()-1).getId());

    }


}