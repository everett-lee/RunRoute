package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.distancecalc.BBCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Peforms duties of a director class. Executes the query
 * produced by the QueryBuilder implementation and returns the
 * procued ResultSet
 */
@Component
public abstract class QueryDirector {
    protected QueryBuilder qb;
    protected BBCalculator distanceCalc;
    protected PreparedStatement ps;
    protected boolean[] options;

    @Autowired
    public QueryDirector(QueryBuilder qb, BBCalculator distanceCalc) {
        this.qb = qb;
        this.distanceCalc = distanceCalc;

        // all options are set to true by default
        options = new boolean[]{true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
    }

    public abstract void buildQuery(double startLat, double startLon, double runLength);

    public void setOptions(boolean[] options) {
        this.options = options;
    }

    public ResultSet getResults() {
        ResultSet results = null;
        try {
            results = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}