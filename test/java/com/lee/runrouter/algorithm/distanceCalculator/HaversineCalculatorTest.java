package com.lee.runrouter.algorithm.distanceCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HaversineCalculatorTest {
    DistanceCalculator haversineCalc;

    @Before
    public void setUp() {
        this.haversineCalc = new HaversineCalculator();
    }

    @Test
    public void testLongDistance() {
        Node n1 = new Node(1, 51.91, 1.04);
        Node n2 = new Node(1, 50, 2.04);

        double res = haversineCalc.calculateDistance(n1, n2);
        double expected = 223600.0;

        assertEquals(expected, res, 100);
    }

    @Test
    public void testShortDistance() {
        Node n1 = new Node(1, 50.0001, 1.02);
        Node n2 = new Node(1, 50, 1.02);

        double res = haversineCalc.calculateDistance(n1, n2);
        double expected = 11.12;

        assertEquals(expected, res, 0.1);
    }

    @Test
    public void testShortDistanceTwo() {
        Node n1 = new Node(1, 0, 0);
        Node n2 = new Node(1, 0.001, 0.002);

        double res = haversineCalc.calculateDistance(n1, n2);
        double expected = 248.6;

        assertEquals(expected, res, 0.1);
    }
}