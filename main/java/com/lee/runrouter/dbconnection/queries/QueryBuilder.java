package com.lee.runrouter.dbconnection.queries;

import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

/**
 * Interfae for generating and retrieving PostGIS query
 */

@Component
public interface QueryBuilder {
    void reset();

    void setBBCoords(double[] BBCoords, double[] origin);

    void setHighWayOptions(boolean[] opts);

    void setRunLength(double distance);

    PreparedStatement getPreparedStament();
}
