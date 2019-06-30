package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;
import java.util.Arrays;

import static com.lee.runrouter.testhelpers.TestHelpers.*;
import static org.junit.Assert.*;

public class IteratedLocalSearchMainTest {
    PathTuple morrishRoadShort;
    PathTuple tulseHillLong;
    private IteratedLocalSearch ils;

    {
        morrishRoadShort = getMorrishShort();
        tulseHillLong = getTulseLong();
    }

    @Before
    public void setUp() {
        this.ils = new IteratedLocalSearchMain();
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
    public void testGetStartPathSegmentSixthIn() throws InvocationTargetException, IllegalAccessException {

        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 6);
        long resultid = result.getPreviousNode().getId();
        long expected = 115967131;

        assertEquals(expected, resultid);
    }


    @Test
    public void testGetStartPathSegmentFifteenthIn() throws InvocationTargetException, IllegalAccessException {

        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 15);
        long resultid = result.getPreviousNode().getId();
        long expected = 4918954452L;

        assertEquals(expected, resultid);
    }


    @Test
    public void testGetEndPathSegmentTwoLong() throws InvocationTargetException, IllegalAccessException {
        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 2);


        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
        getEndPathSegment.setAccessible(true);
        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 2);

        long resultid = end.getPreviousNode().getId();
        long expected = 115966693;
        assertEquals(resultid, expected);
    }

    @Test
    public void testGetEndPathSegmentElevenLong() throws InvocationTargetException, IllegalAccessException {
            Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 6);


        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
        getEndPathSegment.setAccessible(true);
        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 11);

        long resultid = end.getPreviousNode().getId();
        long expected = 21074565;
        assertEquals(resultid, expected);
    }

    @Test
    public void testGetLastEndPathSegment() throws InvocationTargetException, IllegalAccessException {
        returnPath(morrishRoadShort, "");

        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
        getStartPathSegment.setAccessible(true);
        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 1);


        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
        getEndPathSegment.setAccessible(true);
        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 31);

        System.out.println(start.getPreviousNode().getId() + " !!! START NODE !!! ");
        long resultid = end.getPreviousNode().getId();
        long expected = 114280020;
        assertEquals(resultid, expected);
    }

    @Test
    public void testGetPathSize() throws InvocationTargetException, IllegalAccessException {
        int expected = 33;

        Method getPathsize = Arrays.stream(ils.getClass().getDeclaredMethods())
                .filter(x -> x.getName().equals("getPathSize")).findFirst().get();
        getPathsize.setAccessible(true);
        int result = (int) getPathsize.invoke(ils, morrishRoadShort);

        assertEquals(expected, result);
    }
}