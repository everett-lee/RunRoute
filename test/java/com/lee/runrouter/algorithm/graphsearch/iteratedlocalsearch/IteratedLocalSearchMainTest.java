package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.*;

public class IteratedLocalSearchMainTest {
    PathTuple morrishRoadShort;
    PathTuple tulseHillLong;
    ILSGraphSearch ilsGraphSearch;

    private IteratedLocalSearch ils;

    {
        morrishRoadShort = getMorrishShort();
        tulseHillLong = getTulseLong();
    }

    @Before
    public void setUp() {
        this.ilsGraphSearch = mock(ILSGraphSearch.class);
        this.ils = new IteratedLocalSearchMain(ilsGraphSearch);
    }



    @Test
    public void testGetStartPathSegmentFirst() throws InvocationTargetException, IllegalAccessException {
        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 1);
        long resultid = result.getPreviousNode().getId();
        long expected = 114280020;

        assertEquals(expected, resultid);
    }


    @Test
    public void testGetStartPathSegmentThirdIn() throws InvocationTargetException, IllegalAccessException {
        PathTuple a  = new PathTupleMain(null, null,
                null, 1, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, 2, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, 3, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, 4, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, 5, 0, 0);



        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, e, 3);
        double resultScore = result.getSegmentScore();
        double expected = 3;

        assertEquals(expected, resultScore, 0.0001);
    }


    @Test
    public void testGetEndPathSegmentTwoLong() throws InvocationTargetException, IllegalAccessException {
        PathTuple a  = new PathTupleMain(null, null,
                null, 1, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, 2, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, 3, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, 4, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, 5, 0, 0);

        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, e, 2);

        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
        getEndPathSegment.setAccessible(true);
        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 2);


        double resultid = end.getSegmentScore();
        double expected = 4;
        assertEquals(resultid, expected, 0.00001);
    }

    @Test
    public void testGetPathSizeSingle() throws InvocationTargetException, IllegalAccessException {
        int expected = 1;

        PathTupleMain a = new PathTupleMain(null, null, null, 0, 0 ,0);

        returnPath(morrishRoadShort, "");

        Method getPathsize = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getPathSize")).findFirst().get();
        getPathsize.setAccessible(true);
        int result = (int) getPathsize.invoke(ils, a);

        assertEquals(expected, result);
    }


    @Test
    public void testGetPathSizeTriple() throws InvocationTargetException, IllegalAccessException {
        int expected = 3;

        PathTupleMain a = new PathTupleMain(null, null, null, 0, 0 ,0);
        PathTupleMain b = new PathTupleMain(a, null, null, 0, 0 ,0);
        PathTupleMain c = new PathTupleMain(b, null, null, 0, 0 ,0);


        returnPath(morrishRoadShort, "");

        Method getPathsize = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getPathSize")).findFirst().get();
        getPathsize.setAccessible(true);
        int result = (int) getPathsize.invoke(ils, c);

        assertEquals(expected, result);
    }

    @Test
    public void testGetScoreFiveNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple a  = new PathTupleMain(null, null,
                null, 1, 0, 0);
        PathTuple b  = new PathTupleMain(a, null,
                null, 2, 0, 0);
        PathTuple c  = new PathTupleMain(b, null,
                null, 3, 0, 0);
        PathTuple d  = new PathTupleMain(c, null,
                null, 4, 0, 0);
        PathTuple e  = new PathTupleMain (d, null,
                null, 5, 0, 0);


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
    public void testcalculateScoreTwoNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null, 11, 5, 0);
        PathTuple head = new PathTupleMain(tail, null, null,13 , 5, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 11;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testcalculateScoreOneNode() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null, 24, 5, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, tail, tail);
        double expected = 0;

        assertEquals(expected, result, 0.001);
    }

    @Test
    public void testcalculateScoreThreeNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null, 15, 2, 0);
        PathTuple mid = new PathTupleMain(tail, null, null,15 , 40, 0);
        PathTuple head = new PathTupleMain(mid, null, null,8 , 40, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 30;

        assertEquals(expected, result, 0.001);
    }



    @Test
    public void testcalculateDistanceTwoNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null, 0, 5, 0);
        PathTuple head = new PathTupleMain(tail, null, null,0 , 5, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateDistance")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 5;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testcalculatDistanceOneNode() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null, 5, 5, 0);

          Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateDistance")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, tail, tail);
        double expected = 0;

        assertEquals(expected, result, 0.001);
    }

    @Test
    public void testcalculateDistanceThreeNodes() throws InvocationTargetException, IllegalAccessException {
        PathTuple tail = new PathTupleMain(null, null, null, 0, 2, 0);
        PathTuple mid = new PathTupleMain(tail, null, null,0 , 40, 0);
        PathTuple head = new PathTupleMain(mid, null, null,0 , 40, 0);

        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("calculateDistance")).findFirst().get();
        calculateLen.setAccessible(true);
        double result = (double) calculateLen.invoke(ils, head, tail);
        double expected = 42;

        assertEquals(expected, result, 0.001);
    }


    @Test
    public void testReverseList() throws InvocationTargetException, IllegalAccessException {
        PathTuple head = morrishRoadShort;

        Method reverseList = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("reverseList")).findFirst().get();
        reverseList.setAccessible(true);
        PathTuple result = (PathTuple) reverseList.invoke(ils, head);

        long expected = result.getPredecessor().getPredecessor().getPreviousNode().getId();
        assertEquals(expected, 290017288L);
    }

}