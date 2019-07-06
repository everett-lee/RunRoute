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
        featuresHeuristic = new FeaturesHeuristic(preferredSurfaces, preferredHighways);
        edgeDistanceCalculator = new EdgeDistanceCalculatorMain(distanceCalculator);
        gradientCalculator = new SimpleGradientCalculator();
        elevationHeuristic = new ElevationHeuristicMain(true);

        connectPathBeamSearch = new BeamSearchConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, gradientCalculator, elevationHeuristic);
        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator,  gradientCalculator, elevationHeuristic);

        this.ilsBFS = new IteratedLocalSearchMain(connectPathBFS);
        this.ilsBeamSearch = new IteratedLocalSearchMain(connectPathBeamSearch);

    }

    @Test
    public void testScoreGreaterOrEqualBeam(){
        double originalScore = calculateScore(morrishRoadShort);

        PathTuple res = ilsBeamSearch.iterate(morrishRoadShort, 2000);

        double postScore = calculateScore(res);
        assertTrue(postScore >= originalScore);

    }

    @Test
    public void testScoreGreaterOrEqualBFS() {
        double originalScore = calculateScore(morrishRoadShort);

        PathTuple res = ilsBFS.iterate(morrishRoadShort, 2000);

        double postScore = calculateScore(res);

        assertTrue(postScore >= originalScore);

        System.out.println(originalScore);
        System.out.println(postScore);
    }


    @Test
    public void testScoreGreaterOrEqualBeamCraignair() {
        double originalScore = calculateScore(craignair);


        PathTuple res = ilsBeamSearch.iterate(craignair, 2000);

        double postScore = calculateScore(res);

        assertTrue(postScore >= originalScore);
    }


    @Test
    public void testScoreGreaterOrEqualBFSCraignair() {
        double originalScore = calculateScore(craignair);

        PathTuple res = ilsBFS.iterate(craignair, 2000);



        double postScore = calculateScore(res);


    }

    @Test
    public void testScoreGreaterOrEqualBeamTulse() {
        double originalScore = calculateScore(tulseHillLong);


        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);

        assertTrue(postScore - originalScore >= -0.01);
    }

    @Test
    public void testScoreGreaterOrEqualBFSTulse() {
        double originalScore = calculateScore(tulseHillLong);


        PathTuple res = ilsBFS.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);

        assertTrue(postScore - originalScore >= -0.01);
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



    static double calculateScore(PathTuple head) {
        double score = 0;

        PathTuple g = head;
        while (head != null) {
            score += head.getSegmentScore();
            head = head.getPredecessor();
        }

        return score;
    }





}