package com.lee.runrouter.api;

import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.*;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.algorithm.pathnode.ScorePair;
import com.lee.runrouter.executor.*;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.routegenerator.RouteGenerator;
import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.routegenerator.RouteGeneratorMain;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.*;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ResponseGeneratorControllerTest {
    ResponseGeneratorController responseGeneratorController;
    Executor executor;
    RouteGenerator routeGenerator;
    GraphBuilder graphBuilder;
    FeaturesHeuristic featuresHeuristic;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    ElevationHeuristic elevationHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    LinkedListToArray linkedListToArray;
    SearchAlgorithm BFS;
    SearchAlgorithm BFSconnection;
    CycleRemover cycleRemover;
    ElementRepo repo;

    @Before
    public void setUp() throws PathNotGeneratedException {
        routeGenerator = mock(RouteGeneratorMain.class);

        // mock the response PathTuple and associated objects
        Node mocknode = mock(Node.class);
        when(mocknode.getId()).thenReturn(1l);
        PathTuple response = mock(PathTupleMain.class);
        when(response.getPredecessor()).thenReturn(null);
        when(response.getCurrentNode()).thenReturn(mocknode);
        when(response.getSegmentLength()).thenReturn(1d);
        when(response.getSegmentScore()).thenReturn(new ScorePair(1,1));
        Way mockedWay = mock(Way.class);
        when(mockedWay.getId()).thenReturn(1l);
        when(response.getCurrentWay()).thenReturn(mockedWay);

        when(routeGenerator.generateRoute(anyObject(), anyDouble()))
                .thenReturn(response);

        graphBuilder = mock(GraphBuilder.class);
        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        elevationHeuristic = new ElevationHeuristicMain();
        linkedListToArray = new LinkedListToArrayHeadNodes();
        distanceHeuristic = mock(DistanceFromOriginNodeHeuristicMain.class);

        repo = mock(ElementRepo.class);
        edgeDistanceCalculator = mock(EdgeDistanceCalculatorMain.class);
        gradientCalculator = mock(SimpleGradientCalculator.class);
        BFS = new BFS(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                                    gradientCalculator, elevationHeuristic);
        BFSconnection = new BFSConnectionPath(repo, distanceHeuristic, featuresHeuristic, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristic);
        cycleRemover = mock(CycleRemover.class);

        executor = new ExecutorMain(routeGenerator, graphBuilder, linkedListToArray,
                BFS, BFSconnection, cycleRemover);
        responseGeneratorController = new ResponseGeneratorController(executor);
    }

    @Test
    public void testHighwayDislikedSurfacesOnlyCorrect() throws NoSuchFieldException, IllegalAccessException, PathNotGeneratedException {
        boolean[] options = {false,false,true,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("dislikedSurfaces");
        f.setAccessible(true);
        List<String> dislikedSurfaces = (List<String>) f.get(featuresHeuristic);

        assertTrue(dislikedSurfaces.contains("CONCRETE"));
        assertTrue(dislikedSurfaces.size() == 2);
    }

    @Test
    public void testUnlitStreetsCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,true,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        boolean avoidUnlit = BFS.getAvoidUnlit();
        boolean avoidUnlit3 = BFSconnection.getAvoidUnlit();

        assertTrue(avoidUnlit && avoidUnlit3);
    }

    @Test
    public void testUnlitStreetsCorrectWhenOff() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        boolean avoidUnlit = BFS.getAvoidUnlit();
        boolean avoidUnlit3 = BFSconnection.getAvoidUnlit();

        assertFalse(avoidUnlit && avoidUnlit3);
    }

    @Test
    public void testMaxGradient() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};
        responseGeneratorController.receiveArgs(50, 1, 2000, 0.05, options);


        double maxGradient = BFS.getMaxGradient();
        double maxGradient3 = BFSconnection.getMaxGradient();

        assertEquals(maxGradient, 0.05, 0.000001);
        assertEquals(maxGradient3, 0.05, 0.000001);
    }

    @Test
    public void testMaxGradientTwo() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};
        responseGeneratorController.receiveArgs(50, 1, 2000, 0.67, options);


        double maxGradient = BFS.getMaxGradient();
        double maxGradient3 = BFSconnection.getMaxGradient();

        assertEquals(maxGradient, 0.67, 0.000001);
        assertEquals(maxGradient3, 0.67, 0.000001);
    }

    @Test
    public void testUphillCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,true,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = elevationHeuristic.getClass().getDeclaredField("preferUphill");
        f.setAccessible(true);
        Boolean preferUphill = (Boolean) f.get(elevationHeuristic);

        assertTrue(preferUphill);
    }


    @Test
    public void testUphillCorrectWhenOff() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = elevationHeuristic.getClass().getDeclaredField("preferUphill");
        f.setAccessible(true);
        Boolean preferUphill = (Boolean) f.get(elevationHeuristic);

        assertFalse(preferUphill);
    }

    @Test
    public void testGrassCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,true,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("preferredSurfaces");
        f.setAccessible(true);
        List<String> preferredSurfaces = (List<String>) f.get(featuresHeuristic);

        assertTrue(preferredSurfaces.contains("DIRT"));
        assertTrue(preferredSurfaces.contains("GRASS"));
        assertTrue(preferredSurfaces.contains("UNPAVED"));
        assertTrue(preferredSurfaces.size() == 6);
    }


    @Test
    public void testGrassCorrectWhenOff() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("preferredSurfaces");
        f.setAccessible(true);
        List<String> preferredSurfaces = (List<String>) f.get(featuresHeuristic);

        assertFalse(preferredSurfaces.contains("DIRT"));
        assertFalse(preferredSurfaces.contains("GRASS"));
        assertFalse(preferredSurfaces.contains("UNPAVED"));
        assertTrue(preferredSurfaces.size() == 0);
    }

    @Test
    public void testBackRoadsCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,true,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("preferredHighways");
        f.setAccessible(true);
        List<String> preferredSurfaces = (List<String>) f.get(featuresHeuristic);

        assertTrue(preferredSurfaces.contains("LIVING_STREET"));
        assertTrue(preferredSurfaces.contains("RESIDENTIAL"));
        assertTrue(preferredSurfaces.size() == 2);
    }


    @Test
    public void testResidentialCorrectWhenOff() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);


        Field f = featuresHeuristic.getClass().getDeclaredField("preferredHighways");
        f.setAccessible(true);
        List<String> preferredSurfaces = (List<String>) f.get(featuresHeuristic);

        assertFalse(preferredSurfaces.contains("LIVING_STREET"));
        assertFalse(preferredSurfaces.contains("RESIDENTIAL"));
        assertTrue(preferredSurfaces.size() == 0);
    }

    @Test
    public void testBackroadsCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,true};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("preferredHighways");
        f.setAccessible(true);
        List<String> preferredSurfaces = (List<String>) f.get(featuresHeuristic);

        assertTrue(preferredSurfaces.contains("CYCLEWAY"));
        assertTrue(preferredSurfaces.contains("BRIDLEWAY"));
        assertTrue(preferredSurfaces.contains("FOOTWAY"));
        assertTrue(preferredSurfaces.contains("PATH"));
        assertTrue(preferredSurfaces.contains("TRACK"));
        assertTrue(preferredSurfaces.size() == 5);
    }


    @Test
    public void testBackroadsCorrectWhenOff() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);


        Field f = featuresHeuristic.getClass().getDeclaredField("preferredHighways");
        f.setAccessible(true);
        List<String> preferredSurfaces = (List<String>) f.get(featuresHeuristic);

        assertTrue(preferredSurfaces.size() == 0);
    }
}