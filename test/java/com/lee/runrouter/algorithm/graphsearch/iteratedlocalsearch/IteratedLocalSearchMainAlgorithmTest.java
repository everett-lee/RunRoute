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
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IteratedLocalSearchMainAlgorithmTest {
    PathTuple morrish5k;
    PathTuple morrish14k;
    PathTuple morrish21k;
    PathTuple morrishProblemOne;
    PathTuple morrishProblemTwo;
    PathTuple tulseHill5k;
    PathTuple tulseHill14k;
    PathTuple tulseHill21k;
    PathTuple craignair5k;
    PathTuple craignair14k;
    PathTuple craignair21k;
    DistanceCalculator distanceCalculator;
    DistanceFromOriginNodeHeursitic distanceHeuristic;
    ElementRepo repo;
    Heuristic featuresHeuristic;
    EdgeDistanceCalculator edgeDistanceCalculator;
    GradientCalculator gradientCalculator;
    ElevationHeuristicMain elevationHeuristic;
    ILSGraphSearch connectPathBFS;
    ILSGraphSearch connectPathBeamSearch;
    IteratedLocalSearch ilsBFS;
    IteratedLocalSearch ilsBeamSearch;


    {
        morrish5k = getMorrish5k();
        morrish14k = getMorrish14k();
        morrish21k = getMorrish21k();
        morrishProblemOne = getMorrishProblemOne();
        morrishProblemTwo = getMorrishProblemTwo();
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
        featuresHeuristic = new FeaturesHeuristicMain();
        ((FeaturesHeuristicMain) featuresHeuristic).setPreferredSurfaces(preferredSurfaces);
        ((FeaturesHeuristicMain) featuresHeuristic).setPreferredHighways(preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);

        connectPathBeamSearch = new BeamSearchConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);

        this.ilsBFS = new IteratedLocalSearchMain(connectPathBFS);
        this.ilsBeamSearch = new IteratedLocalSearchMain(connectPathBeamSearch);

    }

    @Test
    public void testMorrish5KGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrish5k);
        double originalLength = calculateDistance(morrish5k);
        double target = 270;

        PathTuple res = ilsBFS.iterate(morrish5k, target);

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
    public void testMorrish14KGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrish14k);
        double originalLength = calculateDistance(morrish14k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrish14k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testMorrish21KGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrish21k);
        double originalLength = calculateDistance(morrish21k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrish21k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testScoreGreaterOrEqualMorrishProblemOneBFS() {
        double originalScore = calculateScore(morrishProblemOne);
        double originalLength = calculateDistance(morrishProblemOne);
        double target = 100;

        PathTuple res = ilsBFS.iterate(morrishProblemOne, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }


    @Test
    public void testScoreGreaterOrEqualMorrishProblemTwoBFS() {
        double originalScore = calculateScore(morrishProblemTwo);
        double originalLength = calculateDistance(morrishProblemTwo);
        double target = 0;

        PathTuple res = ilsBFS.iterate(morrishProblemTwo, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }


    @Test
    public void testCraignair5k() {
        double originalScore = calculateScore(craignair5k);
        double originalLength = calculateDistance(craignair5k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair5k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testCraignair14k() {
        double originalScore = calculateScore(craignair14k);
        double originalLength = calculateDistance(craignair14k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair14k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testCraignair21k() {
        double originalScore = calculateScore(craignair21k);
        double originalLength = calculateDistance(craignair21k);
        double target = 0;

        PathTuple res = ilsBFS.iterate(craignair21k, target);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }


    @Test
    public void testulse5k() {
        double originalScore = calculateScore(tulseHill5k);
        double originalLength = calculateDistance(tulseHill5k);
        double target = 700;

        PathTuple res = ilsBFS.iterate(tulseHill5k, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testulse15k() {
        double originalScore = calculateScore(tulseHill14k);
        double originalLength = calculateDistance(tulseHill14k);
        double target = 500;

        PathTuple res = ilsBFS.iterate(tulseHill14k, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testulse21k() {
        double originalScore = calculateScore(tulseHill21k);
        double originalLength = calculateDistance(tulseHill21k);
        double target = 2000;

        PathTuple res = ilsBFS.iterate(tulseHill21k, target);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + target) * 1.1);
    }

    @Test
    public void testCountIterationsBeam() {
        List<Integer> iterations = new ArrayList<>();
        List<Integer> improvements = new ArrayList<>();

        PathTuple res = ilsBeamSearch.iterate(craignair5k, 2000);
        iterations.add(ilsBeamSearch.getIterations());
        improvements.add(ilsBeamSearch.getImprovements());

        System.out.println(iterations.stream().mapToDouble(x -> x).average());
        System.out.println(improvements.stream().mapToDouble(x -> x).average());
        System.out.println(iterations);
        System.out.println(improvements);
    }


    @Test
    public void testMorrishScoreGreaterOrEqualBeam() {
        double originalScore = calculateScore(morrish5k);
        double originalLength = calculateDistance(morrish5k);

        PathTuple res = ilsBeamSearch.iterate(morrish5k, 140);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);
        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 140);
    }

    @Test
    public void testCraignairScoreGreaterOrEqualBeam() {
        double originalScore = calculateScore(craignair5k);
        double originalLength = calculateDistance(craignair5k);

        PathTuple res = ilsBeamSearch.iterate(craignair5k, 0);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 0);
    }



    @Test
    public void testScoreGreaterOrEqualBeamTulse() {
        double originalScore = calculateScore(tulseHill5k);
        double originalLength = calculateDistance(tulseHill5k);

        PathTuple res = ilsBeamSearch.iterate(tulseHill5k, 125);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 2000);
    }

    @Test
    public void testScoreGreaterOrEqualTulseHillBeam() {
        double originalScore = calculateScore(tulseHill5k);
        double originalLength = calculateDistance(tulseHill5k);

        PathTuple res = ilsBeamSearch.iterate(tulseHill5k, 0);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 0);
    }

}