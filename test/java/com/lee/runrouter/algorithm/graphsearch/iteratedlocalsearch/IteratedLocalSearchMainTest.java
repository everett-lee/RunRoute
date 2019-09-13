package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.algorithm.pathnode.ScorePair;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.*;

public class IteratedLocalSearchMainTest {
    PathTuple morrish5k;
    PathTuple tulse5k;
    ILSGraphSearch ilsGraphSearch;

    private IteratedLocalSearch ils;

    {
        morrish5k = getMorrish5k();
        tulse5k = getTulse5k();
    }

    @Before
    public void setUp() {
        this.ilsGraphSearch = mock(ILSGraphSearch.class);
        this.ils = new IteratedLocalSearchMain(ilsGraphSearch);
    }


    // test first node in removed segment is correctly selected
    @Test
    public void testGetStartPathSegmentFirst() throws InvocationTargetException, IllegalAccessException {
        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathNode")).findFirst().get();
        getStartPathSegment.setAccessible(true);

        // create the linked list
        PathTuple a  = new PathTupleMain(null, null,
                null, new ScorePair(1, 1), 0, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, new ScorePair(2, 2), 0, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, new ScorePair(3, 3), 0, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, new ScorePair(4, 4), 0, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, new ScorePair(5, 5), 0, 0, 0);

        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, e, 1);
        double resultDistanceScore = result.getSegmentScore().getDistanceScore();
        long expected =  5;

        assertEquals(expected, resultDistanceScore, 0.01);
    }

    // test third node in removed segment is correctly selected
    @Test
    public void testGetStartPathSegmentThirdIn() throws InvocationTargetException, IllegalAccessException {
        // create the linked list
        PathTuple a  = new PathTupleMain(null, null,
                null, new ScorePair(1, 1), 0, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, new ScorePair(2, 2), 0, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, new ScorePair(3, 3), 0, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, new ScorePair(4, 4), 0, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, new ScorePair(5, 5), 0, 0, 0);


        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathNode")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, e, 3);
        double resultScore = result.getSegmentScore().getHeuristicScore();
        double expected = 3;

        assertEquals(expected, resultScore, 0.0001);
    }

    // test that node two from the start is selected
    @Test
    public void testGetEndPathSegmentTwoLong() throws InvocationTargetException, IllegalAccessException {
        PathTuple a  = new PathTupleMain(null, null,
                null, new ScorePair(1, 1), 0, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, new ScorePair(2, 2), 0, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, new ScorePair(3, 3), 0, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, new ScorePair(4, 4), 0, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, new ScorePair(5, 5), 0, 0, 0);


        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathNode")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, e, 2);

        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getEndPathNode")).findFirst().get();
        getEndPathSegment.setAccessible(true);
        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 2);


        double resultid = end.getSegmentScore().getHeuristicScore();
        double expected = 4;
        assertEquals(resultid, expected, 0.00001);
    }

    @Test
    public void testGetPathSizeSingle() throws InvocationTargetException, IllegalAccessException {
        int expected = 1;

        PathTupleMain a = new PathTupleMain(null, null,
                null, new ScorePair(0, 0), 0 ,0, 0);

        Method getPathsize = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getPathSize")).findFirst().get();
        getPathsize.setAccessible(true);
        int result = (int) getPathsize.invoke(ils, a);

        assertEquals(expected, result);
    }


    @Test
    public void testGetPathSizeTriple() throws InvocationTargetException, IllegalAccessException {
        int expected = 3;

        PathTuple a  = new PathTupleMain(null, null,
                null, new ScorePair(1, 1), 0, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, new ScorePair(2, 2), 0, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, new ScorePair(3, 3), 0, 0, 0);

        Method getPathsize = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getPathSize")).findFirst().get();
        getPathsize.setAccessible(true);
        int result = (int) getPathsize.invoke(ils, c);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFiveNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple a  = new PathTupleMain(null, null,
                null, new ScorePair(1, 1), 0, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, new ScorePair(2, 2), 0, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, new ScorePair(3, 3), 0, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, new ScorePair(4, 4), 0, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, new ScorePair(5, 5), 0, 0, 0);



        PathTuple head = e;
        PathTuple tail = head.getPredecessor()
                .getPredecessor().getPredecessor().getPredecessor();

        Method calculateScore = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateScore.setAccessible(true);
        double result = (double) calculateScore.invoke(ils, head, tail);
        double expected = 10;

        assertEquals(expected, result, 0.1);
    }

    @Test
    public void testcalculateScoreFiveNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null,
                null, new ScorePair(0, 1), 5, 0, 0);
        PathTuple D = new PathTupleMain(tail, null,
                null, new ScorePair(0, 1), 5, 0, 0);
        PathTuple C = new PathTupleMain(D, null, null,
                new ScorePair(0, 2) , 5, 0, 0);
        PathTuple B = new PathTupleMain(C, null,
                null, new ScorePair(0, 3), 5, 0, 0);
        PathTuple A = new PathTupleMain(B, null, null,
                new ScorePair(0, 5) , 5, 0, 0);
        PathTuple head = new PathTupleMain(A, null, null,
                new ScorePair(0, 8) , 5, 0, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 12;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testcalculateScoreTwoNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null,
                null, new ScorePair(0, 11), 5, 0, 0);
        PathTuple head = new PathTupleMain(tail, null, null,
                new ScorePair(0, 13) , 5, 0, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 11;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testcalculateScoreOneNode() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null,
                new ScorePair(0, 24), 5, 0, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, tail, tail);
        double expected = 0;

        assertEquals(expected, result, 0.001);
    }

    @Test
    public void testcalculateScoreThreeNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null,
                new ScorePair(0, 15), 2, 0, 0);
        PathTuple mid = new PathTupleMain(tail, null, null,new ScorePair(0, 15),
                40, 0, 0);
        PathTuple head = new PathTupleMain(mid, null, null,
                new ScorePair(0, 8), 40, 0, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 30;

        assertEquals(expected, result, 0.001);
    }



    @Test
    public void testcalculateDistanceTwoNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null,
                new ScorePair(0, 0), 5, 0, 0);
        PathTuple head = new PathTupleMain(tail, null, null,
                new ScorePair(0, 0), 5, 0, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateDistance")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 5;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testcalculatDistanceOneNode() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null,
                new ScorePair(0, 5), 5, 0, 0);

          Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateDistance")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, tail, tail);
        double expected = 0;

        assertEquals(expected, result, 0.001);
    }

    @Test
    public void testcalculateDistanceThreeNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null,
                new ScorePair(0, 0), 2, 0, 0);
        PathTuple midOne = new PathTupleMain(tail, null, null,
                new ScorePair(0, 0) , 40.55, 0, 0);
        PathTuple midTwo = new PathTupleMain(midOne, null, null,
                new ScorePair(0, 0) ,  60, 0, 0);
        PathTuple head = new PathTupleMain(midTwo, null, null,
                new ScorePair(0, 0), 45.03, 0, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateDistance")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 102.55;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testReverseList() throws InvocationTargetException, IllegalAccessException {
        PathTuple head = morrish5k;
        PathTuple tail = head;

        while (tail.getPredecessor() != null) {
            tail = tail.getPredecessor();
        }
        Method reverseList = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("reverseList")).findFirst().get();
        reverseList.setAccessible(true);
        PathTuple result = (PathTuple) reverseList.invoke(ils, head);

        assertEquals(result, tail);
    }

}