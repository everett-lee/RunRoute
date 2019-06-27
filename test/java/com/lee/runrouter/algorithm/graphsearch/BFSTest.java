package com.lee.runrouter.algorithm.graphsearch;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
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

import static org.junit.Assert.*;

public class BFSTest {
    ElementRepo repo;
    GraphSearch bfs;
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
        distanceHeuristic = new DistanceFromOriginHeuristic(repo, distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("LIVING_STREET","PEDESTRIAN", "TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));
        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        elevationHeuristic = new ElevationHeuristicMain(true);

        bfs = new BFS(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic);
    }

    @Test
    public void bb() {
        double[] coords = {51.446583, -0.125217};
        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 2.5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }


    @Test
    public void bb2() {
        double[] coords = {51.446583, -0.125217};
        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }


    @Test
    public void cc() {
        double[] coords = {51.439140, -0.117574};

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 2.5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }


    @Test
    public void cc2() {
        double[] coords = {51.439140, -0.117574};


        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = bfs.searchGraph(repo.getOriginWay(), coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }

    static void returnPath(PathTuple tp) {
        if (tp.getPredecessor() == null) {
            System.out.println("(" + tp.getPreviousNode() + " distance: " + tp.getLength() + ") " + " way: " + tp.getCurrentWay().getId());
            return;
        }

        System.out.println("(" + tp.getPreviousNode() + " distance: " + tp.getLength() + ") " + " way: " + tp.getCurrentWay().getId());
        returnPath(tp.getPredecessor());
    }


}