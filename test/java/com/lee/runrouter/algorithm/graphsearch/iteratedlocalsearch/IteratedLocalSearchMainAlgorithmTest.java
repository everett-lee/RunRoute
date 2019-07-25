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
import com.lee.runrouter.algorithm.heuristic.DistanceFromOriginToMidHeuristic;
import com.lee.runrouter.algorithm.heuristic.ElevationHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristicMain;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
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
    PathTuple morrishRoadShort;
    PathTuple morrishProblemOne;
    PathTuple morrishProblemTwo;
    PathTuple tulseHillLong;
    PathTuple craignair;
    DistanceCalculator distanceCalculator;
    Heuristic distanceHeuristic;
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
        morrishRoadShort = getMorrishShort();
        tulseHillLong = getTulseLong();
        craignair = getCraignair();
        morrishProblemOne = getMorrishProblemOne();
        morrishProblemTwo = getMorrishProblemTwo();
        repo = getRepo();
    }

    @Before
    public void setUp() {
        distanceCalculator = new HaversineCalculator();
        distanceHeuristic = new DistanceFromOriginToMidHeuristic(repo, distanceCalculator);

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
    public void testMorrishScoreGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrishRoadShort);
        double originalLength = calculateDistance(morrishRoadShort);


        PathTuple res = ilsBFS.iterate(morrishRoadShort, 360);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 120);
    }

    @Test
    public void testScoreGreaterOrEqualMorrishProblemOneBFS() {
        double originalScore = calculateScore(morrishProblemOne);
        double originalLength = calculateDistance(morrishProblemOne);


        PathTuple res = ilsBFS.iterate(morrishProblemOne, 100);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        System.out.println(originalLength);
        System.out.println(postDistance);
        System.out.println(originalScore);
        System.out.println(postScore);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 100);
    }


    @Test
    public void testScoreGreaterOrEqualMorrishProblemTwoBFS() {
        double originalScore = calculateScore(morrishProblemTwo);
        double originalLength = calculateDistance(morrishProblemTwo);


        PathTuple res = ilsBFS.iterate(morrishProblemTwo, 330);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 330);
    }


    @Test
    public void testCraignairScoreGreaterOrEqualBFS() {
        double originalScore = calculateScore(craignair);
        double originalLength = calculateDistance(craignair);

        PathTuple res = ilsBFS.iterate(craignair, 0);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 0);
    }


    @Test
    public void testScoreGreaterOrEqualBFSTulse() {
        double originalScore = calculateScore(tulseHillLong);
        double originalLength = calculateDistance(tulseHillLong);

        PathTuple res = ilsBFS.iterate(tulseHillLong, 0);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= (originalLength + 0));
    }



    @Test
    public void testScoreGreaterOrEqualTulseHillBFS() {
        double originalScore = calculateScore(tulseHillLong);
        double originalLength = calculateDistance(tulseHillLong);


        PathTuple res = ilsBFS.iterate(tulseHillLong, 0);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 0);
    }

    @Test
    public void testCountIterationsBeam() {
        List<Integer> iterations = new ArrayList<>();
        List<Integer> improvements = new ArrayList<>();

        craignair = getCraignair();
        PathTuple res = ilsBeamSearch.iterate(getCraignair(), 2000);
        iterations.add(ilsBeamSearch.getIterations());
        improvements.add(ilsBeamSearch.getImprovements());

        System.out.println(iterations.stream().mapToDouble(x -> x).average());
        System.out.println(improvements.stream().mapToDouble(x -> x).average());
        System.out.println(iterations);
        System.out.println(improvements);
    }


    @Test
    public void testMorrishScoreGreaterOrEqualBeam() {
        double originalScore = calculateScore(morrishRoadShort);
        double originalLength = calculateDistance(morrishRoadShort);

        PathTuple res = ilsBeamSearch.iterate(morrishRoadShort, 140);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);
        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 140);
    }

    @Test
    public void testCraignairScoreGreaterOrEqualBeam() {
        double originalScore = calculateScore(craignair);
        double originalLength = calculateDistance(craignair);

        PathTuple res = ilsBeamSearch.iterate(craignair, 0);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 0);
    }



    @Test
    public void testScoreGreaterOrEqualBeamTulse() {
        double originalScore = calculateScore(tulseHillLong);
        double originalLength = calculateDistance(tulseHillLong);

        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 125);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 2000);
    }

    @Test
    public void testScoreGreaterOrEqualTulseHillBeam() {
        double originalScore = calculateScore(tulseHillLong);
        double originalLength = calculateDistance(tulseHillLong);

        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 0);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);
        assertTrue(postDistance <= originalLength + 0);
    }

}