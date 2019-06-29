package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.AlgoHelpers;
import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lee.runrouter.graph.graphbuilder.node.Node;
import static org.junit.Assert.*;

public class ReturnPathTest {
    ElementRepo repo;
    GraphSearch returnPath;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    ElevationHeuristic elevationHeuristic;

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
        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain(false);


        returnPath = new ReturnPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);
    }

    @Test(timeout=3000)
    public void testMorrishRoadShortReturn() {
        double[] coords = {51.442, -0.109};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 26446121L).findFirst().get();
        System.out.println(w);

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 2.5);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);
    }


    @Test(timeout=3000)
    public void testMorrishRoadLongerReturn() {
        double[] coords = {51.445, -0.112};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 4898590L).findFirst().get();

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);


    }

    @Test(timeout=3000)
    public void testCraignairRoadShortReturn() {
        double[] coords = {51.448321, -0.114648};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 12694843L).findFirst().get();

            Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);


        PathTuple x = returnPath.searchGraph(w, coords, 2.5);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);


    }

    @Test(timeout=3000)
    public void testCraignairRoadLongerReturn() {
        double[] coords = {51.448321, -0.114648};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 22751151L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        double[] originCoords = {51.446810, -0.125484};
        Node originNode = new Node(-1, originCoords[0], originCoords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);

    }

    @Test(timeout=3000)
    public void testTulseHillReturn() {
        double[] coords = {51.441109, -0.106974};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 12538762L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        Node originNode = new Node(-1, coords[0], coords[1]);
        originNode = AlgoHelpers.findClosest(originNode, repo.getOriginWay().getNodeContainer().getNodes());
        // update the repository origin node
        repo.setOriginNode(originNode);

        PathTuple x = returnPath.searchGraph(w, coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);

    }

    static String returnPath(PathTuple tp, String acc) {
        while (tp.getPredecessor() != null) {
            acc += tp.getPreviousNode().getId() + ", ";
            System.out.println("(" + tp.getPreviousNode() + " distance: "
                    + tp.getLength() + ") " + " way: " + tp.getCurrentWay().getId());
            tp = tp.getPredecessor();

        }
        acc = acc.substring(0, acc.length()-3);
        acc += ");\nout;";
        return acc;
    }

}