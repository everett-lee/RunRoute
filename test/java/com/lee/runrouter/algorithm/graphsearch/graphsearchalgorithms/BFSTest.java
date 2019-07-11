package com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class BFSTest {
    ElementRepo repo;
    GraphSearch bfs;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
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
        featuresHeuristic = new FeaturesHeuristic();
        ((FeaturesHeuristic) featuresHeuristic).setPreferredHighways(preferredHighways);
        ((FeaturesHeuristic) featuresHeuristic).setPreferredSurfaces(preferredSurfaces);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);


        bfs = new BFS(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

    }

    @Test(timeout=3000)
    public void testMorrishRoadShort() {

        double[] coords = {51.446810, -0.125484};
        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 2500);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);
    }


    @Test(timeout=3000)
    public void testMorrishRoadLonger() {
        double[] coords = {51.446810, -0.125484};
        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 5000);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);
    }

    @Test(timeout=3000)
    public void testCraignairRoadShort() {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 2500);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);

    }


    @Test(timeout=3000)
    public void testCraignairRoadLonger() {
        double[] coords = {51.448321, -0.114648};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();

        repo.setOriginWay(origin);

        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 5000);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);
    }


    @Test(timeout=3000)
    public void TulseHillTest10KM() {
        double[] coords = {51.441109, -0.106974};

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("CONCRETE"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("FOOTWAY"));
        featuresHeuristic = new FeaturesHeuristic();
        ((FeaturesHeuristic) featuresHeuristic).setPreferredSurfaces(preferredSurfaces);
        ((FeaturesHeuristic) featuresHeuristic).setPreferredHighways(preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);

        bfs = new BFS(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 4004611L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 5000);
        System.out.println(x.getPredecessor() + " hello");

        String str = "node(id:";
        str = returnPath(x, str);
        System.out.println(str);
    }

    static String returnPath(PathTuple tp, String acc) {
        while (tp != null) {
            acc += tp.getPreviousNode().getId() + ", ";
            System.out.println("(" + tp.getPreviousNode() + " distance: "
                    + tp.getSegmentLength() + ") " + " way: " + tp.getCurrentWay().getId());
            tp = tp.getPredecessor();

        }
        acc = acc.substring(0, acc.length()-3);
        acc += ");\nout;";
        return acc;
    }
}