package com.lee.runrouter.algorithm;

import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AlgoHelpersTest {
    ElementRepo repo;

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
    public void testFindClosest() {
        Way wayUnderTest = null;

        for (Way w: repo.getWayRepo()) {
            if (w.getId() == 4439919) {
                wayUnderTest = w;
            }
        }

        // find a node somewhere in the middle of the Way
        Node nodeUnderTest = null;
        for (Node n : wayUnderTest.getNodeContainer().getNodes()) {
            if (n.getId() == 27244980) {
                nodeUnderTest = n;
            }
        }

        // assign start and end nodes of the way to a list
        Node start = wayUnderTest.getNodeContainer().getStartNode();
        Node end = wayUnderTest.getNodeContainer().getEndNode();
        List<Node> terminalNodes = Arrays.asList(start, end);

        // sort return the terminal node closest to the one under test
        Node res = AlgoHelpers.findClosest(nodeUnderTest, terminalNodes);

        assertEquals(res, end); // in this case the end node is closer
    }

    @Test
    public void testFindClosestTwo() {
        Way wayUnderTest = null;

        for (Way w: repo.getWayRepo()) {
            if (w.getId() == 51436348) {
                wayUnderTest = w;
            }
        }

        Node nodeUnderTest = null;
        Node closest = null;
        for (Node n : wayUnderTest.getNodeContainer().getNodes()) {
            if (n.getId() == 43413400) {
                nodeUnderTest = n;
            } else {

                if (n.getId() == 43413402) {
                    closest = n;
                }
            }
        }

        // comparing with all nodes
        List<Node> colleagues = wayUnderTest.getNodeContainer().getNodes().stream()
                .filter(x -> x.getId() != 43413400).collect(Collectors.toList());


        // sort return the terminal node closest to the one under test
        Node res = AlgoHelpers.findClosest(nodeUnderTest, colleagues);

        assertEquals(closest, res); // in this case the end node is close
    }
}