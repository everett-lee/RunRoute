package com.lee.runrouter.dbconnection.queries;

import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

/**
 * Abstraction for generating and retrieving PostGIS query
 */

@Component
public interface QueryBuilder {
    public void reset();

    public void setBBCoords(double[] BBCoords, double[] origin);

    public void setHighWayOptions(boolean[] opts);

    public PreparedStatement getPreparedStament();
}
