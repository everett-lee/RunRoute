package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.GradientCalculator;
import com.lee.runrouter.algorithm.gradientcalculator.SimpleGradientCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculator;
import com.lee.runrouter.algorithm.graphsearch.edgedistancecalculator.EdgeDistanceCalculatorMain;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BFSConnectionPath;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.BeamSearchConnectionPath;
import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.heuristic.*;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.DistanceHeuristic.DistanceFromOriginNodeHeursitic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristic.ElevationHeuristicSensitive;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistance;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic.FeaturesHeuristicUsingDistanceSensitive;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IteratedLocalSearchMainAlgorithmWithFeaturesTest {
    PathTuple morrish5kUphill;
    PathTuple morrish14kFlat;
    PathTuple morrish21Uphill;
    PathTuple morrish21kFlat;
    PathTuple craignai14kUphill;
    PathTuple craignair14kFlat;
    PathTuple craignair21kUphill;
    PathTuple craignair21kflat;
    PathTuple tulse14kUphill;
    PathTuple tulse14kFlat;
    PathTuple tulse21kUphill;
    PathTuple tulse21kFlat;

    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    ElementRepo repo;
    FeaturesHeuristic featuresHeuristicMain;
    FeaturesHeuristic featuresHeuristicSensitive;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristic elevationHeuristicMain;
    ElevationHeuristic elevationHeuristicSensitive;
    ILSGraphSearch connectPathBFS;
    ILSGraphSearch connectPathBeamSearch;
    IteratedLocalSearch ilsBFS;
    IteratedLocalSearch ilsBeamSearch;


    // load routes
    {
         morrish5kUphill = getMorrish5kFeaturesUphill();
         morrish14kFlat = getMorrish14kFeaturesFlat();
         morrish21Uphill = getMorrish21kFeaturesUphill();
         morrish21kFlat = getMorrish21kFeaturesFlat();
         craignai14kUphill = getCraignair14kFeaturesUphill();
         craignair14kFlat = getCraignair14kFeaturesFlat();
         craignair21kUphill = getCraignair21kFeaturesUphill();
         craignair21kflat = getCraignair21kFeaturesFlat();
         tulse14kUphill = getTulse14kFeaturesUphill();
         tulse14kFlat = getTulse14keaturesFlat();
         tulse21kUphill = getTulse21kFeaturesUphill();
         tulse21kFlat = getTulse21keaturesFlat();

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

        featuresHeuristicMain = new FeaturesHeuristicUsingDistance();
        featuresHeuristicSensitive = new FeaturesHeuristicUsingDistanceSensitive();
        featuresHeuristicMain.setPreferredSurfaces(preferredSurfaces);
        featuresHeuristicMain.setPreferredHighways(preferredHighways);
        featuresHeuristicSensitive.setPreferredSurfaces(preferredSurfaces);
        featuresHeuristicSensitive.setPreferredHighways(preferredHighways);

        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();

        elevationHeuristicMain = new ElevationHeuristicMain();
        elevationHeuristicSensitive = new ElevationHeuristicSensitive();
        elevationHeuristicSensitive.setOptions(true);
        elevationHeuristicSensitive.setOptions(true);

        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristicSensitive, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristicSensitive);

        this.ilsBFS = new IteratedLocalSearchMain(connectPathBFS);

    }

    @Test
    public void testMorrishUphill5KGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrish5kUphill);
        double originalLength = calculateDistance(morrish5kUphill);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrish5kUphill, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testMorrishFlat14KGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrish14kFlat);
        double originalLength = calculateDistance(morrish14kFlat);
        double target = 1500;

        PathTuple res = ilsBFS.iterate(morrish14kFlat, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }


    @Test
    public void testMorrishUphill21KGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrish21Uphill);
        double originalLength = calculateDistance(morrish21Uphill);
        double target = 1900;

        PathTuple res = ilsBFS.iterate(morrish21Uphill, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

//    @Test
//    public void testCraignair5k() {
//        double originalScore = calculateScore(craignair5k);
//        double originalLength = calculateDistance(craignair5k);
//        double target = 0;
//
//        PathTuple res = ilsBFS.iterate(craignair5k, target);
//
//        double postScore = calculateScore(res);
//        double postDistance = calculateDistance(res);
//
//        assertTrue(postScore >= originalScore);
//        assertTrue(postDistance <= (originalLength + target) * 1.1);
//    }
//
//    @Test
//    public void testCraignair14k() {
//        double originalScore = calculateScore(craignair14k);
//        double originalLength = calculateDistance(craignair14k);
//        double target = 900;
//
//        PathTuple res = ilsBFS.iterate(craignair14k, target);
//
//        double postScore = calculateScore(res);
//        double postDistance = calculateDistance(res);
//
//        assertTrue(postScore >= originalScore);
//        assertTrue(postDistance <= (originalLength + target) * 1.1);
//    }
//
//    @Test
//    public void testCraignair21k() {
//        double originalScore = calculateScore(craignair21k);
//        double originalLength = calculateDistance(craignair21k);
//        double target = 0;
//
//        PathTuple res = ilsBFS.iterate(craignair21k, target);
//
//        double postScore = calculateScore(res);
//        double postDistance = calculateDistance(res);
//
//        assertTrue(postScore >= originalScore);
//        assertTrue(postDistance <= (originalLength + target) * 1.1);
//    }
//
//
//    @Test
//    public void testulse5k() {
//        double originalScore = calculateScore(tulseHill5k);
//        double originalLength = calculateDistance(tulseHill5k);
//        double target = 700;
////
////        PathTuple res = ilsBFS.iterate(tulseHill5k, target);
////        double postScore = calculateScore(res);
////        double postDistance = calculateDistance(res);
////
////        assertTrue(postScore >= originalScore);
////        assertTrue(postDistance <= (originalLength + target) * 1.1);
//    }
//
//    @Test
//    public void testulse14k() {
//        double originalScore = calculateScore(tulseHill14k);
//        double originalLength = calculateDistance(tulseHill14k);
//        double target = 0;
//
//        PathTuple res = ilsBFS.iterate(tulseHill14k, target);
//        double postScore = calculateScore(res);
//        double postDistance = calculateDistance(res);
//
//        assertTrue(postScore >= originalScore);
//        assertTrue(postDistance <= (originalLength + target) * 1.1);
//    }
//
//    @Test
//    public void testulse21k() {
//        double originalScore = calculateScore(tulseHill21k);
//        double originalLength = calculateDistance(tulseHill21k);
//        double target = 0;
//
//        PathTuple res = ilsBFS.iterate(tulseHill21k, target);
//        double postScore = calculateScore(res);
//        double postDistance = calculateDistance(res);
//
//        assertTrue(postScore >= originalScore);
//        assertTrue(postDistance <= (originalLength + target) * 1.1);
//    }
}