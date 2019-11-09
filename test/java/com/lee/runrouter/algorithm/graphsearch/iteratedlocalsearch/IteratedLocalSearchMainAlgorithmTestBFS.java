package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.EuclideanCalculator;
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

import static com.lee.runrouter.testhelpers.TestHelpers.*;


import static org.junit.Assert.*;

public class IteratedLocalSearchMainAlgorithmTestBFS {
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
    ElevationHeuristic elevationSensitive;
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

        repo = getRepoSW();
    }

    @Before
    public void setUp() {
        distanceCalculator = new EuclideanCalculator();
        distanceHeuristic = new DistanceFromOriginNodeHeuristicMain(distanceCalculator);

        featuresHeuristic = new FeaturesHeuristicUsingDistance();
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationSensitive = new ElevationHeuristicMain();

        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationSensitive);

        ilsBFS = new IteratedLocalSearchMain(connectPathBFS);

    }

    @Test
    public void testMorrish5K() {
        double originalScore = calculateScore(morrish5k);
        double originalLength = calculateDistance(morrish5k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrish5k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testMorrish14K() {
        double originalScore = calculateScore(morrish14k);
        double originalLength = calculateDistance(morrish14k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrish14k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);


        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testMorrish21K() {
        double originalScore = calculateScore(morrish21k);
        double originalLength = calculateDistance(morrish21k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrish21k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testCraignair5k() {
        double originalScore = calculateScore(craignair5k);
        double originalLength = calculateDistance(craignair5k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair5k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testCraignair14k() {
        double originalScore = calculateScore(craignair14k);
        double originalLength = calculateDistance(craignair14k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair14k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testCraignair21k() {
        double originalScore = calculateScore(craignair21k);
        double originalLength = calculateDistance(craignair21k);
        double target = 1000;

        PathTuple res = ilsBFS.iterate(craignair21k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);
        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }


    @Test
    public void testulse5k() {
        double originalScore = calculateScore(tulseHill5k);
        double originalLength = calculateDistance(tulseHill5k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulseHill5k, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testulse14k() {
        double originalScore = calculateScore(tulseHill14k);
        double originalLength = calculateDistance(tulseHill14k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(tulseHill14k, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }

    @Test
    public void testulse21k() {
        double originalScore = calculateScore(tulseHill21k);
        double originalLength = calculateDistance(tulseHill21k);
        double target = 1000;

        System.out.println(tulseHill21k.getCurrentNode());

        PathTuple res = ilsBFS.iterate(tulseHill21k, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.05);
        assertTrue(postDistance >= (originalLength + target) * 0.95);
    }
}