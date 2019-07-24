package com.lee.runrouter.testhelpers;

import com.lee.runrouter.algorithm.pathnode.PathTuple;
import com.lee.runrouter.graph.elementrepo.ElementRepo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class TestHelpers {

    // deserialise test repo used for testing.
    static public ElementRepo getRepo() {
        ElementRepo repo = null;
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
        return repo;
    }

    static public String returnPath(PathTuple tp, String acc) {
        acc += "node(id:";
        while (tp != null) {
            acc += tp.getPreviousNode().getId() + ", ";
            System.out.println("(" + tp.getPreviousNode() + " distance: "
                    + tp.getTotalLength() + " score: " + tp.getSegmentScore() +
                    ") " + " way: " + tp.getCurrentWay().getId());
            System.out.println("Segment length: " + tp.getSegmentLength());
            tp = tp.getPredecessor();

        }
        acc = acc.substring(0, acc.length()-2);
        acc += ");\nout;";
        return acc;
    }

    static public PathTuple getMorrishShort() {
        PathTuple morrishRoadShort = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/morrishshort.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            morrishRoadShort = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return morrishRoadShort;
    }

    static public PathTuple getMorrishProblemOne() {
        PathTuple morrishRoadProb = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/morrishProb1.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            morrishRoadProb = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return morrishRoadProb;
    }

    static public PathTuple getMorrishProblemTwo() {
        PathTuple morrishRoadProb = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/morrishProb2.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            morrishRoadProb = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return morrishRoadProb;
    }

    static public PathTuple getCraignair() {
        PathTuple morrishRoadShort = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/craignair.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            morrishRoadShort = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return morrishRoadShort;
    }

    static public PathTuple getTulseLong() {
        PathTuple tulseHillLong = null;
        // deserialise test repo used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/tulsehilllong.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            tulseHillLong = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return tulseHillLong;
    }

    public static double calculateScore(PathTuple head) {
        double score = 0;

        while (head != null) {
            score += head.getSegmentScore();
            head = head.getPredecessor();
        }

        return score;
    }

    public static double calculateDistance(PathTuple head) {
        double distance = 0;

        while (head != null) {
            distance += head.getSegmentLength();
            head = head.getPredecessor();
        }

        return distance;
    }

    public static PathTuple getTail(PathTuple head) {
        while (head.getPredecessor() != null) {
            head = head.getPredecessor();
        }
        return head;
    }

    public static int getNumberofNodes(PathTuple head) {
        int total = 0;

        while (head != null) {
            total++;
            head = head.getPredecessor();
        }
        return total;
    }
}
