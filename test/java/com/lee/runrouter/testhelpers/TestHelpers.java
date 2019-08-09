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
                    + tp.getTotalLength() + " score: " + tp.getSegmentScore().getHeuristicScore() +
                    ") " + " way: " + tp.getCurrentWay().getId());
            System.out.println("Segment length: " + tp.getSegmentLength());
            tp = tp.getPredecessor();

        }
        acc = acc.substring(0, acc.length()-2);
        acc += ");\nout;";
        return acc;
    }

    static public PathTuple getMorrish5k() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish5k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }


    static public PathTuple getMorrish5kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish5kWithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getMorrish14k() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish14k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getMorrish14kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish14kWithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getMorrish21k() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish21k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }


    static public PathTuple getMorrish21kFeaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish21kWithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getMorrish21kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/morrish21kWithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair5k() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair5k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair5kFeaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair5kwithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair5kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair5kwithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair14k() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair14k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair14kFeaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair14kwithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair14kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair14kwithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair21k() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair21k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair21kFeaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair21kwithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getCraignair21kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/craignair21kwithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse5k() {
        PathTuple route = null;
        // deserialise test repo used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse5k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse5keaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse5kWithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse5kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse5kWithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse14k() {
        PathTuple route = null;
        // deserialise test repo used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse14k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse14keaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse14kWithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse14kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse14kWithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse21k() {
        PathTuple route = null;
        // deserialise test repo used for testing.
        try {
            FileInputStream fileIn = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse21k.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse21keaturesFlat() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse21kWithFeaturesFlat.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }

    static public PathTuple getTulse21kFeaturesUphill() {
        PathTuple route = null;
        // deserialise test path used for testing.
        try {
            FileInputStream fileIn
                    = new FileInputStream("/home/lee/project/app/runrouter/src/savedRoutes/tulse21kWithFeaturesUphill.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            route = (PathTuple) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Repo class not found");
            c.printStackTrace();
        }
        return route;
    }


    public static double calculateScore(PathTuple head) {
        double score = 0;

        while (head != null) {
            score += head.getSegmentScore().getHeuristicScore();
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
