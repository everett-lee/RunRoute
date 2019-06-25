package com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import static org.junit.Assert.*;

public class EdgeDistanceCalculatorMainTest {
    DistanceCalculator distanceCalculator;
    EdgeDistanceCalculator edgeDistanceCalculator;
    static ElementRepo repo;

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
        this.distanceCalculator = new HaversineCalculator();
        this.edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
    }

    @Test
    public void testLengthProvidedWhereTerminalNodesUsed() {
       Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 51436348L)
               .findFirst().get();
       Node a = wayUnderTest1.getNodeContainer().getEndNode();

       Way wayUnderTest2 = repo.getNodeToWay().get(43398761L).stream()
               .filter(way -> way.getId() == 474810158L).findFirst().get();

       Node b = wayUnderTest2.getNodeContainer().getStartNode();

       // the distance from node a, to node b, which connects wayUnderTest1 to wayUnderTest2
       double res = edgeDistanceCalculator.calculateDistance(a, b, wayUnderTest1);

       // resulting length should be that contained in wayUnderTest1, as travelling from a to b
       // involves traversing entire length of the way
       assertEquals(wayUnderTest1.getLength(), res, 0.0001);
    }


    @Test
    public void testLengthWhereTerminalNodeIntersectsWay() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 12539701L).findFirst().get();
        Node a = wayUnderTest1.getNodeContainer().getStartNode();

        Way wayUnderTest2 = repo.getNodeToWay().get(114266678L).stream()
                .filter(x -> x.getId() == 26396773L).findFirst().get();

        Node b = wayUnderTest2.getNodeContainer().getStartNode();

        double expected = 180.0;
        double res = edgeDistanceCalculator.calculateDistance(a, b, wayUnderTest1);


        assertEquals(expected, res, 10);
    }

    @Test
    public void testLengthWhereTerminalNodeIntersectsWayTwo() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 12539701L).findFirst().get();
        Node a = wayUnderTest1.getNodeContainer().getEndNode();

        Way wayUnderTest2 = repo.getNodeToWay().get(114266678L).stream()
                .filter(x -> x.getId() == 26396773L).findFirst().get();

        Node b = wayUnderTest2.getNodeContainer().getStartNode();

        double expected = 60.0;
        double res = edgeDistanceCalculator.calculateDistance(a, b, wayUnderTest1);


        assertEquals(expected, res, 10);
    }

    @Test
    public void testLengthWhereTerminalNodeIntersectsWayThree() {
        Way wayUnderTest1 = repo.getWayRepo().stream().filter(x -> x.getId() == 303223151L).findFirst().get();
        Node a = wayUnderTest1.getNodeContainer().getEndNode();

        Way wayUnderTest2 = repo.getWayRepo().stream().filter(x -> x.getId() == 4791836L).findFirst().get();
        Node b = wayUnderTest2.getNodeContainer().getEndNode();

        double expected = 290.0;
        double res = edgeDistanceCalculator.calculateDistance(a, b, wayUnderTest1);

        assertEquals(expected, res, 10);
    }
}