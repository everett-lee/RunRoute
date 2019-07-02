//package com.lee.runrouter.algorithm.graphsearch.iteratedlocalsearch;
//
//import com.lee.runrouter.algorithm.graphsearch.graphsearchalgorithms.ILSGraphSearch;
//import com.lee.runrouter.algorithm.pathnode.PathTuple;
//import com.lee.runrouter.algorithm.pathnode.PathTupleMain;
//import com.lee.runrouter.graph.graphbuilder.node.Node;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.lang.reflect.*;
//
//import static org.mockito.Mockito.*;
//
//import java.nio.file.Path;
//import java.util.Arrays;
//
//import static com.lee.runrouter.testhelpers.TestHelpers.*;
//import static org.junit.Assert.*;
//
//public class IteratedLocalSearchMainTest {
//    PathTuple morrishRoadShort;
//    PathTuple tulseHillLong;
//    ILSGraphSearch ilsGraphSearch;
//
//    private IteratedLocalSearch ils;
//
//    {
//        morrishRoadShort = getMorrishShort();
//        tulseHillLong = getTulseLong();
//    }
//
//    @Before
//    public void setUp() {
//        this.ilsGraphSearch = mock(ILSGraphSearch.class);
//        this.ils = new IteratedLocalSearchMain(ilsGraphSearch);
//    }
//
//
//
//    @Test
//    public void testGetStartPathSegmentFirst() throws InvocationTargetException, IllegalAccessException {
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 1);
//        long resultid = result.getPreviousNode().getId();
//        long expected = 114280020;
//
//        assertEquals(expected, resultid);
//    }
//
//
//    @Test
//    public void testGetStartPathSegmentSixthIn() throws InvocationTargetException, IllegalAccessException {
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 6);
//        long resultid = result.getPreviousNode().getId();
//        long expected = 115967131;
//
//        assertEquals(expected, resultid);
//    }
//
//
//    @Test
//    public void testGetStartPathSegmentFifteenthIn() throws InvocationTargetException, IllegalAccessException {
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple result = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 15);
//        long resultid = result.getPreviousNode().getId();
//        long expected = 4918954452L;
//
//        assertEquals(expected, resultid);
//    }
//
//
//    @Test
//    public void testGetEndPathSegmentTwoLong() throws InvocationTargetException, IllegalAccessException {
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 2);
//
//        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
//        getEndPathSegment.setAccessible(true);
//        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 2);
//
//
//        long resultid = end.getPreviousNode().getId();
//        long expected = 115966799;
//        assertEquals(resultid, expected);
//    }
//
//    @Test
//    public void testGetEndPathSegmentElevenLong() throws InvocationTargetException, IllegalAccessException {
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 6);
//
//
//        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
//        getEndPathSegment.setAccessible(true);
//        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 11);
//
//        long resultid = end.getPreviousNode().getId();
//        long expected = 115968733;
//        assertEquals(resultid, expected);
//    }
//
//    @Test
//    public void testGetLastEndPathSegment() throws InvocationTargetException, IllegalAccessException {
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, morrishRoadShort, 1);
//
//        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
//        getEndPathSegment.setAccessible(true);
//        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 32);
//
//        System.out.println(start.getPreviousNode().getId() + " !!! START NODE !!! ");
//        long resultid = end.getPreviousNode().getId();
//        long expected = 114280020;
//        assertEquals(resultid, expected);
//    }
//
//    @Test
//    public void testGetPathSize() throws InvocationTargetException, IllegalAccessException {
//        int expected = 33;
//
//        Method getPathsize = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getPathSize")).findFirst().get();
//        getPathsize.setAccessible(true);
//        int result = (int) getPathsize.invoke(ils, morrishRoadShort);
//
//        assertEquals(expected, result);
//    }
//
//
//    @Test
//    public void testGetScoreMorrish() throws InvocationTargetException, IllegalAccessException {
//        PathTuple head = morrishRoadShort;
//        PathTuple tail = morrishRoadShort.getPredecessor().getPredecessor().getPredecessor();
//
//        Method calculateScore = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
//        calculateScore.setAccessible(true);
//        double result = (double) calculateScore.invoke(ils, head, tail);
//        double expected = 34.046;
//
//        assertEquals(expected, result, 0.001);
//    }
//
//    @Test
//    public void testGetScoreMorrishTwo() throws InvocationTargetException, IllegalAccessException {
//        PathTuple head = morrishRoadShort.getPredecessor().getPredecessor().getPredecessor()
//                .getPredecessor().getPredecessor().getPredecessor();
//        PathTuple tail = head.getPredecessor()
//                .getPredecessor().getPredecessor();
//
//        Method calculateScore = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
//        calculateScore.setAccessible(true);
//        double result = (double) calculateScore.invoke(ils, head, tail);
//        double expected = 30.382;
//
//        assertEquals(expected, result, 0.001);
//    }
//
//    @Test
//    public void testGetScoreMorrishTotalSum() throws InvocationTargetException, IllegalAccessException {
//        PathTuple head = morrishRoadShort;
//
//        double expected = 0;
//
//        while (head != null) {
//            expected += head.getSegmentScore();
//            head = head.getPredecessor();
//        }
//
//        head = morrishRoadShort;
//
//        Method calculateScore = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("calculateScore")).findFirst().get();
//        calculateScore.setAccessible(true);
//        double result = (double) calculateScore.invoke(ils, head, null);
//
//
//        assertEquals(expected, result, 0.001);
//    }
//
//
//    @Test
//    public void testcalculateLengthMorrishOne() throws InvocationTargetException, IllegalAccessException {
//        PathTuple head = morrishRoadShort.getPredecessor().getPredecessor();
//        PathTuple tail = head.getPredecessor()
//                .getPredecessor().getPredecessor().getPredecessor().getPredecessor();
//
//        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("calculateLength")).findFirst().get();
//        calculateLen.setAccessible(true);
//        double result = (double) calculateLen.invoke(ils, head, tail);
//        double expected = 652.3441;
//
//        assertEquals(expected, result, 0.001);
//    }
//
//
//    @Test
//    public void testcalculateLengthMorrishTwo() throws InvocationTargetException, IllegalAccessException {
//        PathTuple head = morrishRoadShort.getPredecessor().getPredecessor();
//        PathTuple tail = head.getPredecessor()
//                .getPredecessor().getPredecessor().getPredecessor().getPredecessor().getPredecessor()
//                .getPredecessor().getPredecessor().getPredecessor();
//
//        Method calculateLen = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("calculateLength")).findFirst().get();
//        calculateLen.setAccessible(true);
//        double result = (double) calculateLen.invoke(ils, head, tail);
//        double expected = 1271.7483;
//
//        assertEquals(expected, result, 0.001);
//    }
//
//    @Test
//    public void testReverseList() throws InvocationTargetException, IllegalAccessException {
//        PathTuple head = morrishRoadShort;
//
//        Method reverseList = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("reverseList")).findFirst().get();
//        reverseList.setAccessible(true);
//        PathTuple result = (PathTuple) reverseList.invoke(ils, head);
//
//        long expected = result.getPredecessor().getPredecessor().getPreviousNode().getId();
//        assertEquals(expected, 290017288L);
//    }
//
//    @Test
//    public void testInsertSegmentOne() throws InvocationTargetException, IllegalAccessException {
//        Method reverseList = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("reverseList")).findFirst().get();
//        reverseList.setAccessible(true);
//        PathTuple result = (PathTuple) reverseList.invoke(ils, morrishRoadShort);
//
//        Method getStartPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getStartPathSegment")).findFirst().get();
//        getStartPathSegment.setAccessible(true);
//        PathTuple start = (PathTuple) getStartPathSegment.invoke(ils, result, 2);
//
//        Method getEndPathSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("getEndPathSegment")).findFirst().get();
//        getEndPathSegment.setAccessible(true);
//        PathTuple end = (PathTuple) getEndPathSegment.invoke(ils, start, 4);
//
//        PathTuple newSegmentTail = new PathTupleMain(null, new Node(1, 1, 1),
//                getRepo().getOriginWay(),
//                -1, -1, -1);
//        PathTuple newSegmentHead = new PathTupleMain(newSegmentTail, new Node(2, 2, 2),
//                getRepo().getOriginWay(),
//                -1, -1, -1);
//
//        Method insertSegment = Arrays.stream(ils.getClass().getDeclaredMethods())
//                .filter(x -> x.getName().equals("insertSegment")).findFirst().get();
//        insertSegment.setAccessible(true);
//        PathTuple combined = (PathTuple) insertSegment.invoke(ils, start, end, newSegmentHead);
//
//        assertEquals(newSegmentHead.getPredecessor().getPreviousNode().getId(), 1226775264);
//    }
//}