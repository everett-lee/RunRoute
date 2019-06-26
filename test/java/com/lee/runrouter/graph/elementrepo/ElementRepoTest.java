package com.lee.runrouter.graph.elementrepo;

import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static org.junit.Assert.*;


public class ElementRepoTest {
    ElementRepo repo;

    {
        // deserialise test repo used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/repo.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            repo = (ElementRepo) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
    }

    @Test
    public void testFindConnectedWays() {
        Way wayUnderTest = null;

        for (Way w: repo.getWayRepo()) {
            if (w.getId() == 639188614) {
                wayUnderTest = w;
            }
        }

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

        for (Way w: repo.getWayRepo()) {
            if (w.getId() == 106644897) {
                wayUnderTest = w;
            }
        }

        List<ConnectionPair> result = repo.getConnectedWays(wayUnderTest);

        // there is only the Way with this id number connected
        boolean flag = result.get(0).getConnectingWay().getId() == 26576457;

        assertTrue(flag);
        assertTrue(result.size() == 1);
    }

    @Test
    public void testFindPathBack() {
        Way wayUnderTest = repo.getWayRepo().stream().filter(x -> x.getId() == 106644898L).findFirst().get();

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