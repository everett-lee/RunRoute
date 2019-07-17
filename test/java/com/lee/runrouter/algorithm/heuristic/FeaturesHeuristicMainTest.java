package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.*;

import static org.junit.Assert.*;

public class FeaturesHeuristicMainTest {
    FeaturesHeuristicMain featuresHeuristicMain;
    // allocated scores for features
    double surface_value;
    double highway_value;
    double unlit_penalty;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        featuresHeuristicMain = new FeaturesHeuristicMain();
        Field f = featuresHeuristicMain.getClass().getDeclaredField("SURFACE_VALUE");
        surface_value = (double) f.get(featuresHeuristicMain);

        featuresHeuristicMain = new FeaturesHeuristicMain();
        f = featuresHeuristicMain.getClass().getDeclaredField("HIGHWAY_VALUE");
        highway_value = (double) f.get(featuresHeuristicMain);

        featuresHeuristicMain = new FeaturesHeuristicMain();
        f = featuresHeuristicMain.getClass().getDeclaredField("UNLIT_PENALTY");
        unlit_penalty = (double) f.get(featuresHeuristicMain);
    }

    @Test
    public void testHighwayAndSurface() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("PRIMARY", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("GRAVEL", "ASPHALT", "PEBBLED"));
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setPreferredHighways(highOpts);
        featuresHeuristicMain.setPreferredSurfaces(surfaceOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Primary");
        wayUnderTest.setSurface("Gravel");

        double expected = surface_value + highway_value;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }

    @Test
    public void testHighwayAndSurfaceWithLower() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("Primary", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("Gravel", "ASPHALT", "PEBBLED"));
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setPreferredSurfaces(surfaceOpts);
        featuresHeuristicMain.setPreferredHighways(highOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Primary");
        wayUnderTest.setSurface("Gravel");

        double expected = surface_value + highway_value;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }


    @Test
    public void testHighwayAndSurfaceNoScore() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("PATHWAY", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("GOLD", "ASPHALT", "PEBBLED"));
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setPreferredSurfaces(surfaceOpts);
        featuresHeuristicMain.setPreferredHighways(highOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Primary");
        wayUnderTest.setSurface("Gravel");

        double expected = 0.0;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }

    @Test
    public void testBicycleScore() {
        List<String> highOpts = new ArrayList<>(Arrays.asList("PATHWAY", "CYCLEWAY", "SECONDARY", "TRUNK"));
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("GOLD", "ASPHALT", "PEBBLED"));
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setPreferredSurfaces(surfaceOpts);
        featuresHeuristicMain.setPreferredHighways(highOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Cycleway");
        wayUnderTest.setSurface("Gravel");

        double expected = highway_value;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }

    @Test
    public void testUnwantedSurfaceScore() {
        List<String> surfaceOpts = new ArrayList<>(Arrays.asList("GOLD", "CONCRETE", "PEBBLED"));
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setDislikedSurfaces(surfaceOpts);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Cycleway");
        wayUnderTest.setSurface("Concrete");

        double expected = -surface_value;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }

    @Test
    public void testUnlitScore() {
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setAvoidUnlit(true);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Cycleway");
        wayUnderTest.setSurface("Gravel");

        double expected = -unlit_penalty;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }

    @Test
    public void testUnlitScoreUnaffectedWhereUnlitAccepted() {
        featuresHeuristicMain = new FeaturesHeuristicMain();
        featuresHeuristicMain.setAvoidUnlit(false);

        Way wayUnderTest = new Way(21);
        wayUnderTest.setHighway("Cycleway");
        wayUnderTest.setSurface("Gravel");

        double expected = 0;

        assertEquals(expected,
                featuresHeuristicMain.getScore(wayUnderTest),
                0.0001);
    }

}