package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FeaturesHeuristicTest {
    FeaturesHeuristic featuresHeuristic;

    @Test
    public void testHighwayAndSurface() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("PRIMARY", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("GRAVEL", "ASPHALT", "PEBBLED"));
        featuresHeuristic = new FeaturesHeuristic(surfaceOpts, highOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Primary");
        wayUnderTest.setSurface("Gravel");

        double expected = 1;

        assertEquals(expected,
                featuresHeuristic.getScore(wayUnderTest),
                0.0001);
    }

    @Test
    public void testHighwayAndSurfaceWithLower() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("Primary", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("Gravel", "ASPHALT", "PEBBLED"));
        featuresHeuristic = new FeaturesHeuristic(surfaceOpts, highOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Primary");
        wayUnderTest.setSurface("Gravel");

        double expected = 1;

        assertEquals(expected,
                featuresHeuristic.getScore(wayUnderTest),
                0.0001);
    }


    @Test
    public void testHighwayAndSurfaceNoScore() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("PATHWAY", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("GOLD", "ASPHALT", "PEBBLED"));
        featuresHeuristic = new FeaturesHeuristic(surfaceOpts, highOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Primary");
        wayUnderTest.setSurface("Gravel");

        double expected = 0.0;

        assertEquals(expected,
                featuresHeuristic.getScore(wayUnderTest),
                0.0001);
    }

}