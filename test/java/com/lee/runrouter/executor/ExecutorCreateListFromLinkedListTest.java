package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;
import java.util.List;

import static org.junit.Assert.*;
import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class ExecutorCreateListFromLinkedListTest {
    private PathTuple morrish5k;
    private PathTuple craigNair14k;
    private LinkedListToArray linkedListToArrayNodes;

    {
        morrish5k = getMorrish5k();
        craigNair14k = getCraignair14k();
    }

    @Before
    public void setUp() {
       linkedListToArrayNodes = new LinkedListToArrayHeadNodes();
    }

    @Test
    public void testMorrishConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = linkedListToArrayNodes.convert(morrish5k);

        int size = 0;
        PathTuple head = morrish5k;

        while (head != null) {
            size++;
            head = head.getPredecessor();
        }

        // the new route starts and ends at the same point
        assertEquals(result.get(0).getId(), result.get(result.size()-1).getId());
        // the output list contains all points of the input route
        assertEquals(size, result.size());
    }

    @Test
    public void testCraignairConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = (List<Node>) linkedListToArrayNodes.convert(craigNair14k);

        int size = 0;
        PathTuple head = craigNair14k;

        while (head != null) {
            size++;
            head = head.getPredecessor();
        }

        // the new route starts and ends at the same point
        assertEquals(result.get(0).getId(), result.get(result.size()-1).getId());
        // the output list contains all points of the input route
        assertEquals(size, result.size());
    }
}