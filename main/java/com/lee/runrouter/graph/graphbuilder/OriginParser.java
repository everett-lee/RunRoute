package com.lee.runrouter.graph.graphbuilder;

import com.lee.runrouter.dbconnection.queries.QueryDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  Responsible for retrieving query results from the originQueryDirector class
 *  then parsing the ResultSet.
 */
@Component
public class OriginParser {
    private QueryDirector originQueryDirector;

    @Autowired
    public OriginParser(@Qualifier("OriginQueryDirector") QueryDirector originQueryDirector) {
        this.originQueryDirector = originQueryDirector;
    }

    /**
     *
     * @param coords containing the latitude and longitude of origin point
     * @param options containing booleans corresponding to optional choices
     * @throws IllegalArgumentException where the ResultSet is empty, meaning
     *         no relevant location was found.
     * @return a long corresponding to the origin Way's id number.
     */
    public long getOriginWayID(double[] coords, boolean[] options) {
        originQueryDirector.setOptions(options);
        originQueryDirector.buildQuery(coords[0], coords[1], 500);

        ResultSet originRes = originQueryDirector.getResults();

        long originWayID = -1;
        try {
            if (originRes.next()) {
                originWayID = originRes.getLong(1);// id number
                // corresponding to the starting way
            } else {
                // the ResultSet is empty, meaning no Way was found corresponding to these
                // coordinates
                throw new IllegalArgumentException("The provided coordinates do not correspond" +
                        " to any found starting position");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return originWayID;
    }
}
