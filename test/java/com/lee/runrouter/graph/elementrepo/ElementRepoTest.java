package com.lee.runrouter.graph.elementrepo;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.testhelpers.TestHelpers;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;


public class ElementRepoTest {
    ElementRepo repo;

    {
        // deserialise test repo used for testing.

        repo = TestHelpers.getRepoSW();
    }

    @Test
    public void testFindConnectedWays() {
        Way wayUnderTest = repo.getWayRepo().get(639188614L);

        List<ConnectionPair> result = repo.getConnectedWays(wayUnderTest);

        // all three ways are contained
        boolean flag = true;

        for (ConnectionPair pair: result) {
            if (pair.getConnectingWay().getId() != 12540910 &&
            pair.getConnectingWay().getId() != 12540900 &&
                    pair.getConnectingWay().getId() != 26464531) {
                flag = false;
            }
        }

        assertTrue(result.size() == 3);
        assertTrue(flag);
    }

    @Test
    public void testFindUniConnectedWay() {
        Way wayUnderTest = null;

        for (Way w: repo.getWayRepo().values()) {
            if (w.getId() == 106644897) {
                wayUnderTest = w;
            }
        }

        List<ConnectionPair> result = repo.getConnectedWays(wayUnderTest);

        // there is only one Way with this id number connected
        boolean flag = result.get(0).getConnectingWay().getId() == 26576457;

        assertTrue(flag);
        assertTrue(result.size() == 1);
    }

    @Test
    public void testFindPathBack() {
        Way wayUnderTest = repo.getWayRepo().get(106644898L);

        Way w1 = repo.getConnectedWays(wayUnderTest).stream()
                .filter(x -> x.getConnectingWay().getId() == 106644899L).findFirst().get().getConnectingWay();

        boolean flag = false;

        for (ConnectionPair pair: repo.getConnectedWays(w1)) {
            if (pair.getConnectingWay().getId() == 106644898L) {
                flag = true;
            }
        }

        assertTrue(flag);
    }
}