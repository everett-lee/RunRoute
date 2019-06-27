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
import java.util.*;


public class DistanceFromOriginToMidTestHeuristicRealData {
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
        distanceFromOriginHeuristic = new DistanceFromOriginToMidHeuristic(repo, calc);
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

    @Test
    public void testClosestToOrigin() {
        Node n1 = new Node(1, 1, 0);
        Node n2 = new Node(2, 1, 0);

        Way origin = repo.getOriginWay();
        Way w1 = repo.getWayRepo().stream().filter(x -> x.getId() == 192339946L).findFirst().get();
        Way w2 = repo.getWayRepo().stream().filter(x -> x.getId() == 578916713L).findFirst().get();
        Way w3 = repo.getWayRepo().stream().filter(x -> x.getId() == 4970487L).findFirst().get();
        Way w4 = repo.getWayRepo().stream().filter(x -> x.getId() == 5045573L).findFirst().get();

        PriorityQueue<PathTuple> queue = new PriorityQueue<>(Comparator
                .comparing((PathTuple tuple) -> tuple.getScore()).reversed());

        List<Way> outQueue = new ArrayList<>();

        PathTupleMain pt = new PathTupleMain(null, n1
                , w1, distanceFromOriginHeuristic.getScore(n1, n2, w1), 0);
        queue.add(pt);
        pt = new PathTupleMain(null, n1
                , w2, distanceFromOriginHeuristic.getScore(n1, n2, w2), 0);
        queue.add(pt);
        pt = new PathTupleMain(null, n1
                , w3, distanceFromOriginHeuristic.getScore(n1, n2, w3), 0);
        queue.add(pt);
        pt = new PathTupleMain(null, n1
                , w4, distanceFromOriginHeuristic.getScore(n1, n2, w4), 0);
        queue.add(pt);

        while (!queue.isEmpty()) {
            outQueue.add(queue.poll().getCurrentWay());
        }

        assertEquals(outQueue, new ArrayList<>(Arrays.asList(w1, w2, w3, w4)));
    }




}

