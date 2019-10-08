package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.dbconnection.DBconnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;

import java.sql.*;

/**
 * Builder class for the PostGIS query to retrieve the closest Way to the origin point.
 */
@Component
@Qualifier("OriginQueryBuilder")
public class OriginQueryBuilder implements QueryBuilder {
    PreparedStatement preparedStatement;
    Connection conn;
    String sql;

    private final String[] ROAD_OPTIONS_ARR
            = {"trunk", "primary", "secondary", "tertiary", "unclassified",
            "residential", "living_street", "service", "pedestrian", "track", "road",
            "footway", "bridleway", "steps", "path"};


    public OriginQueryBuilder() {
        try {
            conn = DBconnection.getInstance().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reset();
    }

    // the PostGIS SQL query
    private final String SELECT = "SELECT id\n";
    private final String FROM = "FROM lineCombinedWithWay\n";
    private final String BB = "WHERE way && ST_MakeEnvelope(?, ?, " +
            "?, ?, 4326)\n";
    private final String ROAD_OPTIONS = "AND (highway IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'bicycle'))\n";
    private final String ORDER_BY = "ORDER BY ST_Distance(way::geography, ST_MakePoint(?, ?)) limit 1;";

    @Override
    public void reset() {
        this.sql = SELECT + FROM + BB + ROAD_OPTIONS + ORDER_BY;
        try {
            this.preparedStatement =
                    conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBBCoords(double[] BBCoords, double[] origin) {
        try {
            this.preparedStatement.setDouble(1, BBCoords[0]); // set BB min lon
            this.preparedStatement.setDouble(2, BBCoords[1]); // set BB min lat
            this.preparedStatement.setDouble(3, BBCoords[2]); // set BB max lon
            this.preparedStatement.setDouble(4, BBCoords[3]); // set BB max lat

            this.preparedStatement.setDouble(20, origin[1]); // set origin lon
            this.preparedStatement.setDouble(21, origin[0]); // set origin lat
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
                    this.preparedStatement.setString(i, ROAD_OPTIONS_ARR[j]);
                } else {
                    this.preparedStatement.setString(i, "");
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
        return this.preparedStatement;
    }
}
