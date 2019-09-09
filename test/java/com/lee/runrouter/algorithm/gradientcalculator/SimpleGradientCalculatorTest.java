package com.lee.runrouter.algorithm.gradientcalculator;

import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.Before;
import org.junit.Test;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import static org.junit.Assert.*;

public class SimpleGradientCalculatorTest {
SimpleGradientCalculator simpleGradientCalculator;
ElementRepo repo;
    @Before
    public void setUp() {
        simpleGradientCalculator = new SimpleGradientCalculator();
        repo = TestHelpers.getRepoSW();
    }

    @Test
    public void testUphill() {
        Way brixtonHillStart = repo.getWayRepo().get(150783571L);
        Way brixtonHillEnd = repo.getWayRepo().get(192339946L);
        Node start = brixtonHillStart.getNodeContainer().getStartNode();
        Node end = brixtonHillEnd.getNodeContainer().getStartNode();

        double expected = 0.03142; // based on Google maps
        double result = simpleGradientCalculator
                .calculateGradient(start, brixtonHillStart, end
                , brixtonHillEnd, 700);

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testFlat() {
        Way churchStart = repo.getWayRepo().get(192469360L);
        Way churchEnd = repo.getWayRepo().get(311169658L);
        Node start = churchStart.getNodeContainer().getStartNode();
        Node end = churchEnd.getNodeContainer().getStartNode();

        double expected = 0.0175; // based on Google maps
        double result = simpleGradientCalculator
                .calculateGradient(start, churchStart, end
                        , churchEnd, 400);

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testDownhill() {
        Way tulseStart = repo.getWayRepo().get(4898590L);
        Way tulseEnd = repo.getWayRepo().get(177218531L);
        Node start = tulseStart.getNodeContainer().getStartNode();
        Node end = tulseEnd.getNodeContainer().getStartNode();

        double expected = -0.025; // based on Google maps
        double result = simpleGradientCalculator
                .calculateGradient(start, tulseStart, end
                        , tulseEnd, 400);

        assertEquals(expected, result, 0.001);
    }
}