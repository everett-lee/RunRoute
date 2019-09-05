package com.lee.runrouter.algorithm.distanceCalculator;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EuclideanCalculatorTest {
    DistanceCalculator euclideanCalc;

    @Before
    public void setUp() {
        this.euclideanCalc = new EuclideanCalculator();
    }

    @Test
    public void testDistanceSouth10m() {
        Node n1 = new Node(1,  51.972875, 0.550801);
        Node n2 = new Node(1, 51.972877, 0.550949);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 10; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceSouth100m() {
        Node n1 = new Node(1,  51.972875, 0.550801);
        Node n2 = new Node(1, 51.973199, 0.549438);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 100; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceSouth250m() {
        Node n1 = new Node(1,  51.972861, 0.550779 );
        Node n2 = new Node(1, 51.973074, 0.554428);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 250; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceSouth500m() {
        Node n1 = new Node(1, 51.973249, 0.550219);
        Node n2 = new Node(1, 51.973265, 0.557521);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 500; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceSouth1km() {
        Node n1 = new Node(1, 51.972865, 0.550796);
        Node n2 = new Node(1, 51.973139, 0.565407);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 1000; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceSouth5km() {
        Node n1 = new Node(1, 51.973194, 0.549439);
        Node n2 = new Node(1, 52.005318, 0.600562);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 5000; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceSouth10km() {
        Node n1 = new Node(1, 51.937396, 0.467962);
        Node n2 = new Node(1, 52.027338, 0.467750);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 10000; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }


    @Test
    public void testDistanceNorth10m() {
        Node n1 = new Node(1,  54.809575, -2.338385);
        Node n2 = new Node(1, 54.809571, -2.338230);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 10; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceNorth100m() {
        Node n1 = new Node(1,  54.809575, -2.338385);
        Node n2 = new Node(1, 54.809722, -2.336848);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 100; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceNorth250m() {
        Node n1 = new Node(1,  54.809575, -2.338940);
        Node n2 = new Node(1, 54.809622, -2.335038);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 250; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceNorth500m() {
        Node n1 = new Node(1, 54.809575, -2.338940);
        Node n2 = new Node(1, 54.809482, -2.346749);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 500; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceNorth1km() {
        Node n1 = new Node(1, 54.809575, -2.338940);
        Node n2 = new Node(1,  54.809373, -2.354558);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 1000; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceNorth5km() {
        Node n1 = new Node(1, 54.823332, -2.331436);
        Node n2 = new Node(1, 54.798026, -2.395925);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 5000; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }

    @Test
    public void testDistanceNorth10km() {
        Node n1 = new Node(1, 54.823332, -2.331436);
        Node n2 = new Node(1, 54.772539, -2.460155);

        double res = euclideanCalc.calculateDistance(n1, n2);
        double expected = 10000; // based on google maps

        // result is within 1.5 metres of expected
        assertEquals(expected, res, 1.5);
    }


}