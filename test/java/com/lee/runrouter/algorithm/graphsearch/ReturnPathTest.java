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

    @Test
    public void bb() {
        double[] coords = {51.461, -0.116};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 205285345L).findFirst().get();
        PathTuple x = returnPath.searchGraph(w, coords, 2.5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }


    @Test
    public void bb2() {
        double[] coords = {51.455, -0.114};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 9382943L).findFirst().get();
        PathTuple x = returnPath.searchGraph(w, coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }

    @Test
    public void cc() {
        double[] coords = {51.441, -0.095};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 4968543L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);

        PathTuple x = returnPath.searchGraph(w, coords, 2.5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }

    @Test
    public void cc2() {
        double[] coords = {51.468, -0.144};
        Way w = repo.getWayRepo().stream().filter(x -> x.getId() == 2526157L).findFirst().get();

        Way origin = repo.getWayRepo().stream().filter(x -> x.getId() == 5045576L)
                .findFirst().get();
        repo.setOriginWay(origin);


        PathTuple x = returnPath.searchGraph(w, coords, 5);
        System.out.println(x.getPredecessor() + " hello");

        returnPath(x);

    }

    static void returnPath(PathTuple tp) {
        if (tp.getPredecessor() == null) {
            System.out.print("(" + tp.getPreviousNode().getId() + " distance: " + tp.getLength() + ") ");
            return;
        }

        System.out.print("(" + tp.getPreviousNode().getId() + " distance: " + tp.getLength() + ") ");
        returnPath(tp.getPredecessor());
    }



}