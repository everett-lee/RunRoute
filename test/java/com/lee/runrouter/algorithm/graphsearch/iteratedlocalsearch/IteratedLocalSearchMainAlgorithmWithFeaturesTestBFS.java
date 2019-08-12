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

public class IteratedLocalSearchMainAlgorithmWithFeaturesTestBFS {
    PathTuple morrish5kUphill;
    PathTuple morrish14kUphill;
    PathTuple morrish21kFlat;
    PathTuple craignai14kUphill;
    PathTuple craignair14kFlat;
    PathTuple craignair21kUphill;
    PathTuple craignair21kflat;
    PathTuple tulse5kUphill;
    PathTuple tulse14kUphill;
    PathTuple tulse14kFlat;
    PathTuple tulse21kUphill;
    PathTuple tulse21kFlat;
    PathTuple craignair5kFlat;

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
    IteratedLocalSearch ilsBFS;


    // load routes
    {
         morrish5kUphill = getMorrish5kFeaturesUphill();
         morrish14kUphill = getMorrish14kFeaturesUphill();
         morrish21kFlat = getMorrish21kFeaturesFlat();
         craignair5kFlat = getCraignair5kFeaturesFlat();
         craignai14kUphill = getCraignair14kFeaturesUphill();
         craignair14kFlat = getCraignair14kFeaturesFlat();
         craignair21kUphill = getCraignair21kFeaturesUphill();
         craignair21kflat = getCraignair21kFeaturesFlat();
         tulse5kUphill = getTulse5kFeaturesUphill();
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

        featuresHeuristicMain.setPreferredSurfaces(preferredSurfaces);
        featuresHeuristicMain.setPreferredHighways(preferredHighways);

        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();

        elevationHeuristicMain = new ElevationHeuristicMain();
        elevationHeuristicSensitive.setOptions(true);

        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristicSensitive, edgeDistanceCalculator,
                gradientCalculator, elevationHeuristicSensitive);

        this.ilsBFS = new IteratedLocalSearchMain(connectPathBFS);

    }

    @Test
    public void testMorrishUphill5K() {
        double originalScore = calculateScore(morrish5kUphill);
        double originalLength = calculateDistance(morrish5kUphill);
        double target = 200;

        System.out.println(morrish5kUphill.getPreviousNode());

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
    public void testMorrish14KUphill() {
        double originalScore = calculateScore(morrish5kUphill);
        double originalLength = calculateDistance(morrish5kUphill);
        double target = 300;

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
    public void testMorrish21kFlat() {
        double originalScore = calculateScore(morrish21kFlat);
        double originalLength = calculateDistance(morrish21kFlat);
        double target = 1900;

        PathTuple res = ilsBFS.iterate(morrish21kFlat, target);

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
    public void testCraignair5kFlat() {
        double originalScore = calculateScore(craignair5kFlat);
        double originalLength = calculateDistance(craignair5kFlat);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair5kFlat, target);

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
    public void testCraignair14kUphill() {
        double originalScore = calculateScore(craignai14kUphill);
        double originalLength = calculateDistance(craignai14kUphill);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignai14kUphill, target);

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
    public void testCraignair14kFlat() {
        double originalScore = calculateScore(craignair14kFlat);
        double originalLength = calculateDistance(craignair14kFlat);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair14kFlat, target);

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
    public void testCraignair21kFlat() {
        double originalScore = calculateScore(craignair21kflat);
        double originalLength = calculateDistance(craignair21kflat);
        double target = 1000;

        PathTuple res = ilsBFS.iterate(craignair21kUphill, target);

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
    public void testCraignair21kUphill() {
        double originalScore = calculateScore(craignair21kUphill);
        double originalLength = calculateDistance(craignair21kUphill);
        double target = 800;

        System.out.println(craignair21kUphill.getPreviousNode());

        PathTuple res = ilsBFS.iterate(craignair21kUphill, target);

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
    public void testTulse5kUphill() {
        double originalScore = calculateScore(tulse5kUphill);
        double originalLength = calculateDistance(tulse5kUphill);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulse5kUphill, target);

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
    public void testTulse14kUphill() {
        double originalScore = calculateScore(tulse14kUphill);
        double originalLength = calculateDistance(tulse14kUphill);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulse14kUphill, target);

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
    public void testTulse14kFlat() {
        double originalScore = calculateScore(tulse14kFlat);
        double originalLength = calculateDistance(tulse14kFlat);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulse14kFlat, target);

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
    public void testTulse21kUphill() {
        double originalScore = calculateScore(tulse21kUphill);
        double originalLength = calculateDistance(tulse21kUphill);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulse21kUphill, target);

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
    public void testTulse21kFlat() {
        double originalScore = calculateScore(tulse21kFlat);
        double originalLength = calculateDistance(tulse21kFlat);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulse21kFlat, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

}