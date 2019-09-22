package com.lee.runrouter.graph.graphbuilder;

import com.lee.runrouter.dbconnection.queries.*;
import com.lee.runrouter.graph.elementbuilder.WayBuilder;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GraphBuilder {
    private QueryDirector wayDirector;
    private OriginParser originParser;
    private WayBuilder wayBuilder;
    private ElementRepo repo;

    /**
     * Constructs Way objects from the ResultSet returned by the QueryDirector class and adds them
     * to the repository.
     *
     * @param wayDirector  the WayQueryDirectorEnvelope class. Encapsulates PostGIS SQL query for retrieving
     *                     Ways matching the user-provided parameters
     * @param originParser builds and executes PostGIS query to retrieve ID
     *                     corresponding to the originating way of the route.
     * @param wayBuilder   the WayBuilder class. For incremental construction of Way objects
     * @param repo         repository of Way objects and their associated nodes
     */
    @Autowired
    public GraphBuilder(@Qualifier("WayQueryDirectorEnvelope") QueryDirector wayDirector,
                        OriginParser originParser, WayBuilder wayBuilder, ElementRepo repo) {
        this.wayDirector = wayDirector;
        this.originParser = originParser;
        this.wayBuilder = wayBuilder;
        this.repo = repo;
    }

    /**
     * @param coords   an Array containing the starting latitude and longitude
     * @param distance the distance of the run in KM
     * @param options  an Array of booleans representing options such as acceptable terrain types
     */
    public void buildGraph(double[] coords, double distance, boolean[] options) {
        repo.reset();

        Long originID = originParser.getOriginWayID(coords, options);

        System.out.println(originID);

        wayDirector.setOptions(options);
        wayDirector.buildQuery(coords[0], coords[1], distance);
        ResultSet results = wayDirector.getResults();

        try {
            while (results.next()) {
                int i = 0;

                long id = results.getLong(++i);
                wayBuilder.reset();
                wayBuilder.createInstance(id); // generate a new
                // instance of Way from the way builder

                if (id == originID) { // update repo reference to the origin
                    repo.setOriginWay(wayBuilder.getElement());
                }

                Array tags = results.getArray(++i);
                String[] tagArray = (String[]) tags.getArray();
                // iterate over tag values and update corresponding fields
                for (int t = 0; t < tagArray.length; t++) {
                    if (tagArray[t].equals("lit") && tagArray[t + 1].equals("yes")) {
                        wayBuilder.setAsLit();
                    }

                    // the type of road (eg primary or pathway)
                    if (tagArray[t].equals("highway")) {
                        if (t + 1 < tagArray.length) {
                            wayBuilder.setHighWay(tagArray[t + 1]);
                        }
                    }

                    // the name of the Way
                    if (tagArray[t].equals("name")) {
                        if (t + 1 < tagArray.length) {
                            wayBuilder.setName(tagArray[t + 1]);
                        }
                    }

                    // the surface type (eg asphalt or paved)
                    if (tagArray[t].equals("surface")) {
                        if (t + 1 < tagArray.length) {
                            wayBuilder.setSurface(tagArray[t + 1]);
                        }
                    }
                }

                // the nodes that compose the Way
                Array nodes = results.getArray(++i);
                List<Long> nodeVals = Arrays.asList((Long[]) nodes.getArray());

                // add to hash table mapping Node's id -> Way
                for (long nodeId : nodeVals) {
                    repo.addNodeToWay(nodeId, wayBuilder.getElement());
                }

                double length = results.getDouble(++i);
                wayBuilder.setLength(length);

                // take coordinate in String format, clean, and map to List of List<Double>
                // containing the pair of coordinates
                String StrCoords = results.getString(++i);
                StrCoords = StrCoords.replace("LINESTRING(", "").replace(")", "");
                List<List<Double>> coordsList = Arrays.stream(StrCoords.split(","))
                        .map(x -> Arrays.stream(x.split(" "))
                                .map(coord -> Double.valueOf(coord))
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());

                wayBuilder.setNodes(nodeVals, coordsList);

                long startElevation = results.getLong(++i);
                long endElevation = results.getLong(++i);
                wayBuilder.setElevationPair(startElevation, endElevation);

                // add way to the repo
                repo.getWayRepo().put(id, wayBuilder.getElement());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (repo.getOriginWay() == null) {
            throw new IllegalArgumentException("The provided coordinates do not correspond" +
                    " to any found starting position");
        }
    }
}
