package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.dbconnection.DBconnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Builder class for the PostGIS query to retrieve details for ways contained within the bounding box
 * The query is also assigned the correct configuration of road options. This version of the query
 * separates the query on each table into subqueries.
 */
@Component
@Qualifier("WayQueryBuilderSeparated")
public class WayQueryBuilderSeparated implements QueryBuilder {
    private Connection conn;
    private String sql;
    private PreparedStatement preparedStatement;

    private final String[] ROAD_OPTIONS_ARR
            = {"trunk", "primary", "secondary", "tertiary", "unclassified",
            "residential", "living_street", "service", "pedestrian", "track", "road",
            "footway", "bridleway", "steps", "path"};

    // the PostGIS SQL query

    private final String TOP_SELECT = "SELECT id, tags, nodes, length, coords, startElevation, endElevation FROM \n";
    private final String WAYS_SUBQUERY = "(SELECT w.id, w.tags, w.nodes FROM planet_osm_ways w) as T1,\n";
    private final String LINE_SUBQUERY_SELECT = "(SELECT  l.osm_id,";
    private final String SELECT_LENGTH = "\tST_Length(l.way::geography) AS length, \n";
    private final String SELECT_CORDS = "\tST_AsText(l.way) AS coords, \n";
    private final String SELECT_START_ELEVATION = "\t(SELECT ST_Value(rast, ST_SetSRID(ST_StartPoint(l.way),4326)) as startElevation\n" +
            "\tFROM elevation\n" +
            "\tWHERE ST_Intersects(rast, ST_SetSRID(ST_StartPoint(l.way),4326)) limit 1), \n";
    private final String SELECT_END_ELEVATION = "\t(SELECT ST_Value(rast, ST_SetSRID(ST_EndPoint(l.way),4326)) as endElevation \n" +
            "\tFROM elevation\n" +
            "\tWHERE ST_Intersects(rast, ST_SetSRID(ST_EndPoint(l.way),4326)) limit 1) \n";
    private final String FROM = "\tFROM planet_osm_line l \n";
    private final String BB = "\tWHERE l.way && ST_Transform( ST_MakeEnvelope(?,?,?,?, 4326),4326)\n";
    private final String ROAD_OPTIONS = "\tAND ((l.highway IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n";
    private final String FOOT = "\tAND (l.foot <> 'no' OR l.foot IS NULL))";
    private final String HIGHWAY = "\tOR (l.highway='cycleway' and l.foot='yes'))\n";
    private final String END_LINE_SUBQUERY = ") as T2\n";
    private final String JOIN = "WHERE T1.id = T2.osm_id";

    public WayQueryBuilderSeparated() {
        conn = DBconnection.getInstance().getConnection();
        reset();
    }

    @Override
    public void reset() {
        sql = TOP_SELECT + WAYS_SUBQUERY + LINE_SUBQUERY_SELECT + SELECT_LENGTH + SELECT_CORDS +
                SELECT_START_ELEVATION + SELECT_END_ELEVATION + FROM + BB +
                ROAD_OPTIONS + FOOT + HIGHWAY + END_LINE_SUBQUERY + JOIN;
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
