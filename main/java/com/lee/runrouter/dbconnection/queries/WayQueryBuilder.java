package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.dbconnection.DBconnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Builder class for the PostGIS query to retrieve details for ways contained within the bounding box
 * The query is also assigned the correct configuration of road options.
 */
@Component
@Qualifier("WayQueryBuilder")
public class WayQueryBuilder implements QueryBuilder {
    private Connection conn;
    private String sql;
    private PreparedStatement preparedStatement;

    private final String[] ROAD_OPTIONS_ARR
            = {"trunk", "primary", "secondary", "tertiary", "unclassified",
            "residential", "living_street", "service", "pedestrian", "track", "road",
            "footway", "bridleway", "steps", "path"};

    // the PostGIS SQL query
    private final String SELECT_ID_TAGS_NODES = "SELECT w.id, w.tags, w.nodes, \n";
    private final String SELECT_LENGTH = "\tST_Length(l.way::geography) AS length, \n";
    private final String SELECT_CORDS = "\tST_AsText(l.way) AS coords, \n";
    private final String SELECT_START_ELEVATION = "\t(SELECT ST_Value(rast, ST_SetSRID(ST_StartPoint(l.way),4326)) as startElevation\n" +
            "\tFROM elevation\n" +
            "\tWHERE ST_Intersects(rast, ST_SetSRID(ST_StartPoint(l.way),4326))), \n";
    private final String SELECT_END_ELEVATION = "\t(SELECT ST_Value(rast, ST_SetSRID(ST_EndPoint(l.way),4326)) as endElevation \n" +
            "\tFROM elevation\n" +
            "\tWHERE ST_Intersects(rast, ST_SetSRID(ST_EndPoint(l.way),4326))) \n";
    private final String FROM = "\tFROM planet_osm_ways w, planet_osm_line l \n";
    private final String JOIN = "\tWHERE l.osm_id = w.id\n";
    private final String BB = "\tAND l.way && ST_Transform( ST_MakeEnvelope(?,?,?,?, 4326),4326)\n";
    private final String ROAD_OPTIONS = "\tAND (l.highway IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)) \n";
    private final String END = "\tAND l.name IS NOT NULL";

    public WayQueryBuilder() {
        conn = DBconnection.getInstance().getConnection();
        reset();
    }

    @Override
    public void reset() {
        sql = SELECT_ID_TAGS_NODES + SELECT_LENGTH + SELECT_CORDS +
                SELECT_START_ELEVATION + SELECT_END_ELEVATION + FROM + JOIN + BB +
                ROAD_OPTIONS + END;
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
    public PreparedStatement getPreparedStament() {
        return preparedStatement;
    }
}
