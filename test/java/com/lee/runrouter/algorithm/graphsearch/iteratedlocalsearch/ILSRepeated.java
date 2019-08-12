package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFSConnectionPath;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class ILSRepeated {
    PathTuple morrish5k;
    PathTuple morrish14k;
    PathTuple morrish21k;
    PathTuple tulseHill5k;
    PathTuple tulseHill14k;
    PathTuple tulseHill21k;
    PathTuple craignair5k;
    PathTuple craignair14k;
    PathTuple craignair21k;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    ElementRepo repo;
    FeaturesHeuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeursitic;
    ILSGraphSearch connectPathBFS;
    IteratedLocalSearch ilsBFS;

    {
        morrish5k = getMorrish5k();
        morrish14k = getMorrish14k();
        morrish21k = getMorrish21k();
        craignair5k = getCraignair5k();
        craignair14k = getCraignair14k();
        craignair21k = getCraignair21k();
        tulseHill5k = getTulse5k();
        tulseHill14k = getTulse14k();
        tulseHill21k = getTulse21k();

        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);

        List<String> preferredSurfaces = new ArrayList<>(Arrays.asList("GRASS",
                "DIRT", "GRAVEL"));
        List<String> preferredHighways = new ArrayList<>(Arrays.asList("TRACK",
                "FOOTWAY", "BRIDLEWAY", "STEPS", "PATH"));

        featuresHeuristic = new FeaturesHeuristicUsingDistance();

        featuresHeuristic.setPreferredSurfaces(preferredSurfaces);
        featuresHeuristic.setPreferredHighways(preferredHighways);

        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();

        elevationHeursitic = new ElevationHeuristicMain();
        elevationHeursitic.setOptions(true);

        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic , edgeDistanceCalculator,
                gradientCalculator, elevationHeursitic);

        ilsBFS = new IteratedLocalSearchMain(connectPathBFS);

    }

    @Test
    public void testMorrish5K() {
        double originalScore = calculateScore(morrish5k);
        double originalLength = calculateDistance(morrish5k);
        double target = 0;
        int iterations = 100;

        double totalLength = 0;
        double totalScore = 0;

        for (int i = 0; i < iterations; i++) {
            PathTuple in = getMorrish5k();
            PathTuple res = ilsBFS.iterate(in, target);
            totalScore += calculateScore(res);
            totalLength += calculateDistance(res);
        }

        System.out.println("ORIGINAL LENGTH: " + originalLength);
        System.out.println("ORIGINAL SCORE " + originalScore);

        System.out.println("AFTER LENGTH (AVG): " + totalLength / iterations);
        System.out.println("AFTER SCORE (AVG): " + totalScore / iterations);
    }

    @Test
    public void testMorrish14K() {
        double originalScore = calculateScore(morrish14k);
        double originalLength = calculateDistance(morrish14k);
        double target = 0;
        int iterations = 100;

        double totalLength = 0;
        double totalScore = 0;

        for (int i = 0; i < iterations; i++) {

            PathTuple res = ilsBFS.iterate(getMorrish14k(), target);
            totalScore += calculateScore(res);
            totalLength += calculateDistance(res);
        }

        System.out.println("ORIGINAL LENGTH: " + originalLength);
        System.out.println("ORIGINAL SCORE " + originalScore);

        System.out.println("AFTER LENGTH (AVG): " + totalLength / iterations);
        System.out.println("AFTER SCORE (AVG): " + totalScore / iterations);
    }

    @Test
    public void testCraignair5k() {
        double originalScore = calculateScore(craignair5k);
        double originalLength = calculateDistance(craignair5k);
        double target = 0;
        int iterations = 100;

        double totalLength = 0;
        double totalScore = 0;

        for (int i = 0; i < iterations; i++) {

            PathTuple res = ilsBFS.iterate(getCraignair5k(), target);
            totalScore += calculateScore(res);
            totalLength += calculateDistance(res);
        }

        System.out.println("ORIGINAL LENGTH: " + originalLength);
        System.out.println("ORIGINAL SCORE " + originalScore);

        System.out.println("AFTER LENGTH (AVG): " + totalLength / iterations);
        System.out.println("AFTER SCORE (AVG): " + totalScore / iterations);
    }

    @Test
    public void testCraignair14k() {
        double originalScore = calculateScore(craignair14k);
        double originalLength = calculateDistance(craignair14k);
        double target = 0;
        int iterations = 100;

        double totalLength = 0;
        double totalScore = 0;

        for (int i = 0; i < iterations; i++) {

            PathTuple res = ilsBFS.iterate(getCraignair14k(), target);
            totalScore += calculateScore(res);
            totalLength += calculateDistance(res);
        }

        System.out.println("ORIGINAL LENGTH: " + originalLength);
        System.out.println("ORIGINAL SCORE " + originalScore);

        System.out.println("AFTER LENGTH (AVG): " + totalLength / iterations);
        System.out.println("AFTER SCORE (AVG): " + totalScore / iterations);
    }


    @Test
    public void testTulse5k() {
        double originalScore = calculateScore(tulseHill5k);
        double originalLength = calculateDistance(tulseHill5k);
        double target = 0;
        int iterations = 100;

        double totalLength = 0;
        double totalScore = 0;

        for (int i = 0; i < iterations; i++) {

            PathTuple res = ilsBFS.iterate(getTulse5k(), target);
            totalScore += calculateScore(res);
            totalLength += calculateDistance(res);
        }

        System.out.println("ORIGINAL LENGTH: " + originalLength);
        System.out.println("ORIGINAL SCORE " + originalScore);

        System.out.println("AFTER LENGTH (AVG): " + totalLength / iterations);
        System.out.println("AFTER SCORE (AVG): " + totalScore / iterations);
    }

    @Test
    public void tesTulse14k() {
        double originalScore = calculateScore(getTulse14k());
        double originalLength = calculateDistance(getTulse14k());
        double target = 0;
        int iterations = 100;

        double totalLength = 0;
        double totalScore = 0;

        for (int i = 0; i < iterations; i++) {

            PathTuple res = ilsBFS.iterate(getTulse14k(), target);
            totalScore += calculateScore(res);
            totalLength += calculateDistance(res);
        }

        System.out.println("ORIGINAL LENGTH: " + originalLength);
        System.out.println("ORIGINAL SCORE " + originalScore);

        System.out.println("AFTER LENGTH (AVG): " + totalLength / iterations);
        System.out.println("AFTER SCORE (AVG): " + totalScore / iterations);
    }
}