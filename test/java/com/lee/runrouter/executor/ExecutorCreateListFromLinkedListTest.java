package com.lee.runrouter.executor;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;
import java.util.List;

import static org.junit.Assert.*;
import static com.lee.runrouter.testhelpers.TestHelpers.*;

public class ExecutorCreateListFromLinkedListTest {
    private PathTuple morrish5k;
    private PathTuple craigNair14k;
    private ElementRepo repo;
    CycleRemover cycleremover;
    private LinkedListToArray linkedListToArray;
    private LinkedListToArray linkedListToArrayNodes;

    {
        morrish5k = getMorrish5k();
        craigNair14k = getCraignair14k();
        repo = getRepoSW();
    }

    @Before
    public void setUp() {
       linkedListToArray = new LinkedListToArrayAllNodes(repo);
       linkedListToArrayNodes = new LinkedListToArrayHeadNodes();
    }

    @Test
    public void testMorrishConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = linkedListToArray.convert(morrish5k);

        assertTrue(result.get(0).getId() == result.get(result.size()-1).getId());
    }

    @Test
    public void testCraignairConversion() throws InvocationTargetException, IllegalAccessException {
        List<Node> result = (List<Node>) linkedListToArray.convert(craigNair14k);


        assertTrue(result.get(0).getId() == result.get(result.size()-1).getId());
    }
}