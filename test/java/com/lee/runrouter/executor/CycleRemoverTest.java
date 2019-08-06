package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import org.junit.Before;
import org.junit.Test;

import com.lee.runrouter.graph.graphbuilder.node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CycleRemoverTest {
    CycleRemover cycleRemover;
    DistanceCalculator distanceCalculator;

    @Before
    public void setUp() {
        this.distanceCalculator = new HaversineCalculator();
        this.cycleRemover = new CycleRemover(distanceCalculator);
    }

    @Test
    public void testShortList() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node(11, 1, 1);
        Node n3 = new Node(3, 1, 1);
        Node n4 = new Node(2, 1, 1);
        Node n5 = new Node(6, 1, 1);
        Node n6 = new Node(7, 1, 1);
        Node n7 = new Node(15, 1, 1);
        Node n8 = new Node(6, 1, 1);
        Node n9 = new Node(21, 1, 1);
        Node n10 = new Node(22, 1, 1);

        List<Node> nodes = new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6, n7,
                n8, n9, n10));

        cycleRemover.removeCycle(nodes);

        int postLength = nodes.size();

        assertEquals(7, postLength);
    }

    @Test
    public void testTwoNodeList() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node(11, 1, 1);

        List<Node> nodes = new ArrayList<>(Arrays.asList(n1, n2));

        cycleRemover.removeCycle(nodes);

        int postLength = nodes.size();

        assertEquals(2, postLength);
    }

    @Test
    public void tesThreeNodeList() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node(11, 1, 1);
        Node n3 = new Node(3, 1, 1);

        List<Node> nodes = new ArrayList<>(Arrays.asList(n1, n2, n3));

        cycleRemover.removeCycle(nodes);

        int postLength = nodes.size();

        assertEquals(3, postLength);
    }

    @Test
    public void testSixNodeList() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node(11, 1, 1);
        Node n3 = new Node(22, 1, 1);
        Node n4 = new Node(3, 1, 1);
        Node n5 = new Node(3, 1, 1);
        Node n6 = new Node(6, 1, 1);

        List<Node> nodes = new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6));

        cycleRemover.removeCycle(nodes);

        int postLength = nodes.size();

        assertEquals(5, postLength);
    }

    @Test
    public void testLessThanFiveGap() {
        Node n1 = new Node(1, 1, 1);
        Node n2 = new Node(11, 1, 1);
        Node n3 = new Node(3, 1, 1);
        Node n4 = new Node(2, 1, 1);
        Node n5 = new Node(6, 1, 1);
        Node n6 = new Node(7, 1, 1);
        Node n7 = new Node(15, 1, 1);
        Node n8 = new Node(51, 1, 1);
        Node n9 = new Node(6, 1, 1);
        Node n10 = new Node(21, 1, 1);
        Node n11 = new Node(43, 1, 1);
        Node n12 = new Node(77, 1, 1);
        Node n13 = new Node(33, 1, 1);

        List<Node> nodes = new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6, n7,
                n8, n9, n10, n11, n12, n13));

        cycleRemover.removeCycle(nodes);

        int postLength = nodes.size();

        assertEquals(9, postLength);
    }

}