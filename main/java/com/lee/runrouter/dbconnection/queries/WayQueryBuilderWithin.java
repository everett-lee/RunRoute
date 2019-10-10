package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.dbconnection.DBconnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Builder class for the PostGIS query to retrieve details for ways contained within
 * a circular projection.
 * The query is also assigned the correct configuration of road options.
 */
@Component
@Qualifier("WayQueryBuilderWithin")
public class WayQueryBuilderWithin implements QueryBuilder {
    private Connection conn;
    private String sql;
    private PreparedStatement preparedStatement;

    private final String[] ROAD_OPTIONS_ARR
            = {"trunk", "primary", "secondary", "tertiary", "unclassified",
            "residential", "living_street", "service", "pedestrian", "track", "road",
            "footway", "bridleway", "steps", "path"};

    // the PostGIS SQL query
    private final String SELECT = "SELECT id, tags, nodes, length, coords, startElevation, endElevation \n";
    private final String FROM = "\tFROM lineCombinedWithWay \n";
    private final String BB = "\tWHERE ST_DWithin(way, ST_MakePoint(?, ?), ?)\n";
    private final String ROAD_OPTIONS = "\tAND ((highway IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n";
    private final String FOOT = "\tAND (foot <> 'no' OR foot IS NULL))";
    private final String END = "\tOR (highway='cycleway' and foot='yes'))";

    public WayQueryBuilderWithin() {
        try {
            conn = DBconnection.getInstance().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reset();
    }

    @Override
    public void reset() {
        sql = SELECT + FROM + BB + ROAD_OPTIONS + FOOT + END;
        try {
            preparedStatement =
                    conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // select the correct coordinates for the queries bounding box
    @Override
    public void setBBCoords(double[] BBCoords, double[] origin) {
        try {
            preparedStatement.setDouble(2, origin[0]);
            preparedStatement.setDouble(1, origin[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // set the run distance
    @Override
    public void setRunLength(double distance) {
        double radius =  distance/2; // radius of the projected circle is half
        // the run distance;

        try {
            preparedStatement.setDouble(3, distance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // set the highway option equal to the corresponding entry in the array where
    // selected by the user
    @Override
    public void setHighWayOptions(boolean[] bools) {
        try {
            for (int i = 4, j = 0; j < ROAD_OPTIONS_ARR.length; i++, j++) {
                if (bools[j]) {
                    preparedStatement.setString(i, ROAD_OPTIONS_ARR[j]);
                } else {
                    preparedStatement.setString(i, "");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PreparedStatement getPreparedStament() {
        return preparedStatement;
    }
}
