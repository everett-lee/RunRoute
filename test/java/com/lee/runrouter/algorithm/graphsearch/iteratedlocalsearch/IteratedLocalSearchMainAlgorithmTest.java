package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.distanceCalculator.DistanceCalculator;
import com.lee.runrouter.algorithm.distanceCalculator.HaversineCalculator;
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
        elevationHeuristic = new ElevationHeuristicMain(true);

        connectPathBeamSearch = new BeamSearchConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);
        connectPathBFS = new BFSConnectionPath(repo, distanceHeuristic,
                featuresHeuristic, edgeDistanceCalculator, elevationHeuristic, distanceCalculator);

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
    }


    @Test
    public void testScoreGreaterOrEqualBeamCraignair() {
        double originalScore = calculateScore(craignair);


        PathTuple res = ilsBeamSearch.iterate(craignair, 2000);

        double postScore = calculateScore(res);

        System.out.println(originalScore);
        System.out.println(postScore);
        assertTrue(postScore >= originalScore);


        String x = returnPath(res, "");
        System.out.println(x);

        System.out.println(originalScore);
        System.out.println(postScore);

    }

    @Test
    public void testScoreGreaterOrEqualBeamTulse() {
        double originalScore = calculateScore(tulseHillLong);


        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);

        System.out.println(originalScore);
        System.out.println(postScore);
        assertTrue(postScore >= originalScore);

    }

    @Test
    public void testHighwayReflectedTulse() {
        double originalScore = calculateScore(tulseHillLong);


        PathTuple res = ilsBeamSearch.iterate(tulseHillLong, 2000);

        double postScore = calculateScore(res);

        System.out.println(originalScore);
        System.out.println(postScore);
        assertTrue(postScore >= originalScore);

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