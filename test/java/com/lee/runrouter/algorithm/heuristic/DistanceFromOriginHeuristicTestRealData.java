package com.lee.runrouter.algorithm.heuristic;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.PriorityQueue;


public class DistanceFromOriginHeuristicTestRealData {
    ElementRepo repo;
    Heuristic distanceFromOriginHeuristic;
    DistanceCalculator calc;


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
        calc = new HaversineCalculator();
        distanceFromOriginHeuristic = new DistanceFromOriginHeuristic(repo, calc);
    }


    @Test
    public void testClosestOne() {
        Node n1 = new Node(1, 1, 0);
        Node n2 = new Node(2, 1, 0);

        Way w1 = repo.getWayRepo().stream().filter(x -> x.getId() == 12536708L).findFirst().get();
        Way w2 = repo.getWayRepo().stream().filter(x -> x.getId() == 10498542L).findFirst().get();
        Way w3 = repo.getWayRepo().stream().filter(x -> x.getId() == 22898317L).findFirst().get();
        Way w4 = repo.getWayRepo().stream().filter(x -> x.getId() == 12536353L).findFirst().get();

        double score1 = distanceFromOriginHeuristic.getScore(n1, n2, w1);
        double score2 = distanceFromOriginHeuristic.getScore(n1, n2, w2);
        double score3 = distanceFromOriginHeuristic.getScore(n1, n2, w3);
        double score4 = distanceFromOriginHeuristic.getScore(n1, n2, w4);

        System.out.println(score2);
        System.out.println(score1);

        assertTrue(score2 > score1);
        assertTrue(score3 > score1);
        assertTrue(score4 > score3);
    }

    @Test
    public void testClosestTwo() {
        Node n1 = new Node(1, 1, 0);
        Node n2 = new Node(2, 1, 0);

        Way w1 = repo.getWayRepo().stream().filter(x -> x.getId() == 385587247L).findFirst().get();

        PriorityQueue<PathTuple> queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getScore()).reversed());


        repo.getConnectedWays(w1).forEach(x ->
        {
            double score = distanceFromOriginHeuristic.getScore(n1, n2, x.getConnectingWay());
            PathTupleMain pt = new PathTupleMain(null, x.getConnectingNode()
                    , x.getConnectingWay(), score, 0);
            queue.add(pt);
        });

        assertEquals(queue.poll().getCurrentWay().getId(), 385587256L);

    }


}

