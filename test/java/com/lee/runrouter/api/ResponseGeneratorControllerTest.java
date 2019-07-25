package com.lee.runrouter.api;

import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristicMain;
import com.lee.runrouter.executor.Executor;
import com.lee.runrouter.executor.ExecutorMain;
import com.lee.runrouter.executor.LinkedListToArray;
import com.lee.runrouter.executor.LinkedListToArrayHeadNodes;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.routegenerator.RouteGenerator;
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
    ElevationHeuristic elevationHeuristic;
    LinkedListToArray linkedListToArray;

    @Before
    public void setUp() {
        routeGenerator = mock(RouteGeneratorMain.class);
        graphBuilder = mock(GraphBuilder.class);
        featuresHeuristic = new FeaturesHeuristicMain();
        elevationHeuristic = new ElevationHeuristicMain();
        linkedListToArray = new LinkedListToArrayHeadNodes();

        executor = new ExecutorMain(routeGenerator, graphBuilder, featuresHeuristic, elevationHeuristic, linkedListToArray);
        responseGeneratorController = new ResponseGeneratorController(executor);
    }

    @Test
    public void testHighwayDislikedSurfacesOnlyCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,true,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("dislikedSurfaces");
        f.setAccessible(true);
        List<String> dislikedSurfaces = (List<String>) f.get(featuresHeuristic);

        assertTrue(dislikedSurfaces.contains("CONCRETE"));
        assertTrue(dislikedSurfaces.size() == 1);
    }

    @Test
    public void testUnlitStreetsCorrect() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,true,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("avoidUnlit");
        f.setAccessible(true);
        Boolean avoidUnlit = (Boolean) f.get(featuresHeuristic);

        assertTrue(avoidUnlit);
    }

    @Test
    public void testUnlitStreetsCorrectWhenOff() throws NoSuchFieldException, IllegalAccessException {
        boolean[] options = {false,false,false,false,false,false,false,false};

        responseGeneratorController.receiveArgs(50, 1, 2000, 25, options);

        Field f = featuresHeuristic.getClass().getDeclaredField("avoidUnlit");
        f.setAccessible(true);
        Boolean avoidUnlit = (Boolean) f.get(featuresHeuristic);

        assertFalse(avoidUnlit);
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
        assertTrue(preferredSurfaces.size() == 3);
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