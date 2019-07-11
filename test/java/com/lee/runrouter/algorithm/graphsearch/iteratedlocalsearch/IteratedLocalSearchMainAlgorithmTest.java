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
import com.lee.runrouter.algorithm.heuristic.FeaturesHeuristic;
import com.lee.runrouter.algorithm.heuristic.Heuristic;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class IteratedLocalSearchMainAlgorithmTest {
    PathTuple morrishRoadShort;
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
        featuresHeuristic = new FeaturesHeuristic();
        ((FeaturesHeuristic) featuresHeuristic).setPreferredSurfaces(preferredSurfaces);
        ((FeaturesHeuristic) featuresHeuristic).setPreferredHighways(preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain();
        elevationHeuristic.setOptions(true);

        connectPathBeamSearch = new BeamSearchConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator,  gradientCalculator, elevationHeuristic);

        this.ilsBFS = new IteratedLocalSearchMain(connectPathBFS);
        this.ilsBeamSearch = new IteratedLocalSearchMain(connectPathBeamSearch);

    }

    @Test
    public void testMorrishScoreGreaterOrEqualBeam(){
        double originalScore = calculateScore(morrishRoadShort);
        double originalLength = calculateDistance(morrishRoadShort);

        PathTuple res = ilsBeamSearch.iterate(morrishRoadShort, 2000);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);
        assertTrue(postScore - originalScore >= -0.01);
    }

    @Test
    public void testCountIterationsBFS(){
        List<Integer> iterations = new ArrayList<>();
        List<Integer> improvements = new ArrayList<>();

        for (int i= 0; i < 50; i++) {
            System.out.println(i);
            craignair = getCraignair();
            PathTuple res = ilsBeamSearch.iterate(getCraignair(), 2000);
            iterations.add(ilsBeamSearch.getIterations());
            improvements.add(ilsBeamSearch.getImprovements());
        }

        System.out.println(iterations.stream().mapToDouble(x -> x).average());
        System.out.println(improvements.stream().mapToDouble(x -> x).average());
        System.out.println(iterations);
        System.out.println(improvements);
    }

    @Test
    public void testMorrishScoreGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrishRoadShort);
        double originalLength = calculateDistance(morrishRoadShort);


        PathTuple res = ilsBFS.iterate(morrishRoadShort, 1000);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);
        assertTrue(postScore - originalScore >= -0.01);

    }


    @Test
    public void testCraignairScoreGreaterOrEqualBeam() {
        double originalScore = calculateScore(craignair);
        double originalLength = calculateDistance(craignair);

        PathTuple res = ilsBeamSearch.iterate(craignair, 1000);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);

    }


    @Test
    public void testCraignairScoreGreaterOrEqualBFS() {
        double originalScore = calculateScore(craignair);
        double originalLength = calculateDistance(craignair);

        PathTuple res = ilsBeamSearch.iterate(craignair, 1000);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore >= originalScore);

        System.out.println(originalScore);
        System.out.println(postScore);
        System.out.println(originalLength);
        System.out.println(postDistance);
    }

    @Test
    public void testScoreGreaterOrEqualBeamTulse() {
        double originalScore = calculateScore(tulseHillLong);
        double originalLength = calculateDistance(tulseHillLong);

        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore - originalScore >= -0.01);
    }

    @Test
    public void testScoreGreaterOrEqualBFSTulse() {
        double originalScore = calculateScore(tulseHillLong);
        double originalLength = calculateDistance(tulseHillLong);


        PathTuple res = ilsBFS.iterate(tulseHillLong, 1400);
        double postScore = calculateScore(res);
        double postDistance = calculateDistance(res);

        assertTrue(postScore - originalScore >= -0.01);

        System.out.println(originalScore);
        System.out.println(postScore);
        System.out.println(originalLength);
        System.out.println(postDistance);
    }

    @Test
    public void testScoreGreaterOrEqualTulseHillBeam() {
        double originalScore = calculateScore(tulseHillLong);


        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);

        assertTrue(postScore - originalScore >= -0.01);
    }

    @Test
    public void testScoreGreaterOrEqualTulseHillBFS() {
        double originalScore = calculateScore(tulseHillLong);


        PathTuple res = ilsBFS.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);

        assertTrue(postScore - originalScore >= -0.01);
    }

}