package com.lee.runrouter.algorithm;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.graphsearch.BFS;
import com.lee.runrouter.algorithm.graphsearch.GraphSearch;
import com.lee.runrouter.algorithm.graphsearch.ReturnPath;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IteratedLocalSearchMainTest {
    ElementRepo repo;
    GraphSearch bfs;
    GraphSearch returnPath;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    ElevationHeuristic elevationHeuristic;
    IteratedLocalSearch ils;

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
        elevationHeuristic = new ElevationHeuristicMain(true);


        bfs = new BFS(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic);

        returnPath = new ReturnPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);

        ils = new IteratedLocalSearchMain(bfs, returnPath, repo);
    }

    @Test(timeout=5000)
    public void testMorrishRoadShortRoundTrip() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = ils.generateCycle(coords, 5);

        String str = "node(id:";
        str = returnPath(res, str);
        System.out.println(str);

    }


    @Test(timeout=5000)
    public void testMorrishRoadLongRoundTrip() {
        double[] coords = {51.446810, -0.125484};
        PathTuple res = ils.generateCycle(coords, 10);


        String str = "node(id:";
        str = returnPath(res, str);
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