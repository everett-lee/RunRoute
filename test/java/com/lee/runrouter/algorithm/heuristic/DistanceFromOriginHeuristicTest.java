package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.graph.graphbuilder.node.NodeContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DistanceFromOriginHeuristicTest {
    ElementRepo repo;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginHeuristic distanceFromOriginHeuristic;
    Way way;
    Way originWay;
    NodeContainer nc;
    NodeContainer originNodeContainer;


    @Before
    public void setUp() {
        repo = mock(ElementRepo.class);
        distanceCalculator = new HaversineCalculator();
        distanceFromOriginHeuristic = new DistanceFromOriginHeuristic(repo, distanceCalculator);
        way = mock(Way.class);
        originWay = mock(Way.class);
        nc = mock(NodeContainer.class);
        originNodeContainer = mock(NodeContainer.class);
    }

    @Test
    public void testCalculation() {

        Node a = new Node(1, 51.447651, -0.126708);
        Node b = new Node(2, 51.446272, -0.124363);
        Node origin = new Node(3, 51.449822, -0.122808);

        when(way.getNodeContainer()).thenReturn(nc);
        when(nc.getStartNode()).thenReturn(a);
        when(nc.getEndNode()).thenReturn(b);

        when(repo.getOriginWay()).thenReturn(originWay);
        when(originWay.getNodeContainer()).thenReturn(originNodeContainer);
        when(originNodeContainer.getNodes()).thenReturn(new ArrayList<>(Arrays.asList(origin)));

        double expected = 50 / 362.4 ; // the 'score' obtained by dividing the constant by
        // the distance between the closest node (b) and the origin.
        assertEquals(expected, distanceFromOriginHeuristic.getScore(a, a, way), 0.01);
    }

    @Test
    public void testCloserOfTwo() {
        Node a = new Node(1, 51.447651, -0.126708);
        Node b = new Node(2, 51.446272, -0.124363);
        Node origin = new Node(3, 51.449822, -0.122808);

        when(way.getNodeContainer()).thenReturn(nc);
        when(nc.getStartNode()).thenReturn(a);
        when(nc.getEndNode()).thenReturn(b);

        Node c = new Node(3, 51.449542, -0.119805); // nodes of way 2
        Node d = new Node(4, 51.448381, -0.118530 );

        Way way2 = mock(Way.class); // mock the second Way
        NodeContainer nc2 = mock(NodeContainer.class);
        when(way2.getNodeContainer()).thenReturn(nc2);
        when(nc2.getStartNode()).thenReturn(c);
        when(nc2.getEndNode()).thenReturn(d);

        when(repo.getOriginWay()).thenReturn(originWay);
        when(originWay.getNodeContainer()).thenReturn(originNodeContainer);
        when(originNodeContainer.getNodes()).thenReturn(new ArrayList<>(Arrays.asList(origin)));

        double score1 = distanceFromOriginHeuristic.getScore(a, a, way);
        double score2 = distanceFromOriginHeuristic.getScore(a, a, way2);

        boolean result = score2 > score1; // way2 is closer to the origin than
        // way, so yields a higher score

        assertTrue(result);
    }

}