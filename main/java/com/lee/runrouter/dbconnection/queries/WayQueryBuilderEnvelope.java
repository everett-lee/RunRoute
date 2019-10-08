package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.dbconnection.DBconnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Builder class for the PostGIS query to retrieve details for Ways contained within the bounding box.
 * It queries a combined table where previously a join of two tables was used.
 * The query is also assigned the correct configuration of road options.
 */
@Component
@Qualifier("WayQueryBuilderEnvelope")
public class WayQueryBuilderEnvelope implements QueryBuilder {
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
    private final String BB = "\tWHERE way && ST_MakeEnvelope(?,?,?,?, 4326)\n";
    private final String ROAD_OPTIONS = "\tAND ((highway IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n";
    private final String FOOT = "\tAND (foot <> 'no' OR foot IS NULL))";
    private final String END = "\tOR (highway='cycleway' and foot='yes'))";

    public WayQueryBuilderEnvelope() {
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
            preparedStatement.setDouble(1, BBCoords[0]);
            preparedStatement.setDouble(2, BBCoords[1]);
            preparedStatement.setDouble(3, BBCoords[2]);
            preparedStatement.setDouble(4, BBCoords[3]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // set the highway option equal to the corresponding entry in the array where
    // selected by the user
    @Override
    public void setHighWayOptions(boolean[] bools) {

        try {

            for (int i = 5, j = 0; j < ROAD_OPTIONS_ARR.length; i++, j++) {
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
    public void setRunLength(double distance) {
    }

    @Override
    public PreparedStatement getPreparedStament() {
        return preparedStatement;
    }
}
