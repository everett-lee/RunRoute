package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
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
    private DistanceCalculator distanceCalculator;
    private GradientCalculator gradientCalculator;
    private ElevationHeuristic elevationHeuristic;
    private EdgeDistanceCalculator edgeDistanceCalculator;


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

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic.setOptions(true);
    }

    @Test
    public void testNegativeGradientGivesZeroWhereUphillPreferred() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 51436348L)
                .findFirst().get();

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 474810158L)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getStartNode();

        Node n2 = wayUnderTest2.getNodeContainer().getEndNode();


        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);

        double expected = 0;
        double gradient = gradientCalculator.calculateGradient(n1, wayUnderTest1, n2, wayUnderTest2, distance);
        double score = elevationHeuristic.getScore(gradient);

        assertEquals(expected, score, 0.001);
    }

    @Test
    public void testMildNegativeGradientGivesPositiveWhereFlatPreferred() {
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(false);

        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 51436348L)
                .findFirst().get();

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 474810158L)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getStartNode();

        Node n2 = wayUnderTest2.getNodeContainer().getEndNode();


        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);

        double gradient = gradientCalculator.calculateGradient(n1, wayUnderTest1, n2, wayUnderTest2, distance);
        double score = elevationHeuristic.getScore(gradient);
       assertTrue(score > 0);
    }

    @Test
    public void testSteepPathUpHillPreferred() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 4898590)
                .findFirst().get(); // tulse hill

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 4898590)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getNodes().get(15);
        Node n2 = wayUnderTest2.getNodeContainer().getNodes().get(2);


        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);


        double gradient = gradientCalculator.calculateGradient(n1, wayUnderTest1, n2, wayUnderTest2, distance);
        double score = elevationHeuristic.getScore(gradient);
        assertTrue(score > 0);
    }

    @Test
    public void testSteepPathFlatPreferred() {
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(false);

        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 4898590)
                .findFirst().get(); // tulse hill

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 4898590)
                .findFirst().get();

        Node n1 = wayUnderTest1.getNodeContainer().getNodes().get(15);
        Node n2 = wayUnderTest2.getNodeContainer().getNodes().get(2);

        double distance = edgeDistanceCalculator.calculateDistance(n1,n2, wayUnderTest2);

        double gradient = gradientCalculator.calculateGradient(n1, wayUnderTest1, n2, wayUnderTest2, distance);
        double score = elevationHeuristic.getScore(gradient);
        assertTrue(score < 0.48);
    }


    @Test
    public void testVerySteepWay() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node( 2, 2, 2);
        NodeContainer nc = mock(NodeContainer.class);
        when(nc.getStartNode()).thenReturn(n1);
        when(nc.getEndNode()).thenReturn(n2);
        ElevationPair ep = mock(ElevationPair.class);
        when(ep.getStartElevation()).thenReturn(10l);
        when(ep.getEndElevation()).thenReturn(34l);
        Way startingWay = mock(Way.class);
        when(startingWay.getNodeContainer()).thenReturn(nc);
        when(startingWay.getElevationPair()).thenReturn(ep);

        double gradient = gradientCalculator.calculateGradient(n1, startingWay, n2, startingWay, 50);
        double score = elevationHeuristic.getScore(gradient);
        assertTrue(score >= 1);
    }

    @Test
    // the gradient exceeds default max
    public void testTooSteepWay() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node( 2, 2, 2);
        NodeContainer nc = mock(NodeContainer.class);
        when(nc.getStartNode()).thenReturn(n1);
        when(nc.getEndNode()).thenReturn(n2);
        ElevationPair ep = mock(ElevationPair.class);
        when(ep.getStartElevation()).thenReturn(10l);
        when(ep.getEndElevation()).thenReturn(35l);
        Way startingWay = mock(Way.class);
        when(startingWay.getNodeContainer()).thenReturn(nc);
        when(startingWay.getElevationPair()).thenReturn(ep);

        double gradient = gradientCalculator.calculateGradient(n1, startingWay, n2, startingWay, 50);
        double score = elevationHeuristic.getScore(gradient);
        assertTrue(score < 0);
    }

    @Test
    public void testScoreReductionSetSteep() {
        double gradient = 0.011;
        elevationHeuristic.setMaxGradient(0.01);
        double score = elevationHeuristic.getScore(gradient);
        assertTrue(score < 0);
    }

}