package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.ElevationPair;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import com.lee.runrouter.graph.graphbuilder.node.NodeContainer;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ElevationHeuristicMainTest {
    private ElementRepo repo;
    private DistanceCalculator distanceCalculator = new HaversineCalculator();
    private ElevationHeuristic elevationHeuristic = new ElevationHeuristicMain(false);
    private EdgeDistanceCalculator edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);

    {
        // deserialise test repo used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/repo.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            repo = (ElementRepo) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
    }

    @Test
    public void calculateEndNodeToStartNode() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 51436348L)
                .findFirst().get();

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 474810158L)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getStartNode();

        Node n2 = wayUnderTest2.getNodeContainer().getEndNode();

        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);

        double expected = -0.017;
        double res = elevationHeuristic.getScore(n1, n2, wayUnderTest1, wayUnderTest2, distance);

        assertEquals(expected, res, 0.001);
    }


    @Test
    public void calculateStartNodeToEndNode() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 4439919L)
                .findFirst().get();

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 4439920L)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getStartNode();

        Node n2 = wayUnderTest2.getNodeContainer().getStartNode();

        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);


        double expected = 0.0077;
        double res = elevationHeuristic.getScore(n1, n2, wayUnderTest1, wayUnderTest2, distance);

        assertEquals(expected, res, 0.001);
    }


    @Test
    public void calculateStartNodeToEndNodeTwo() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 3382093L)
                .findFirst().get();

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 3215772L)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getNodes().stream()
                .filter(x -> x.getId() == 15657646L).findFirst().get();

        Node n2 = wayUnderTest2.getNodeContainer().getEndNode();


        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);

        double expected = 0.017;
        double res = elevationHeuristic.getScore(n1, n2, wayUnderTest1, wayUnderTest2, distance);

        assertEquals(expected, res, 0.001);
    }


    @Test
    public void calculateNumeratorIsZero() {
        Node n1 = mock(Node.class);
        Way way1 = mock(Way.class);
        NodeContainer nc1 = mock(NodeContainer.class);
        when(way1.getNodeContainer()).thenReturn(nc1);
        when(nc1.getStartNode()).thenReturn(n1);
        ElevationPair ep1 = mock(ElevationPair.class);
        when(ep1.getStartElevation()).thenReturn(30L);
        when(way1.getElevationPair()).thenReturn(ep1);

        Node n2 = mock(Node.class);
        Way way2 = mock(Way.class);
        NodeContainer nc2 = mock(NodeContainer.class);
        when(way2.getNodeContainer()).thenReturn(nc2);
        when(nc2.getEndNode()).thenReturn(n2);
        ElevationPair ep2 = mock(ElevationPair.class);
        when(ep2.getEndElevation()).thenReturn(30L);
        when(way2.getElevationPair()).thenReturn(ep2);

        double expected = 0;
        double res = elevationHeuristic.getScore(n1, n2, way1, way2, 15);

        assertEquals(expected, res, 0.001);
    }


}