package com.lee.runrouter.graph.graphbuilder;

import com.lee.runrouter.dbconnection.queries.QueryDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OriginParser {
    private QueryDirector originQueryDirector;

    @Autowired
    public OriginParser(@Qualifier("OriginQueryDirector") QueryDirector originQueryDirector) {
        this.originQueryDirector = originQueryDirector;
    }

    public long getOriginWayID(double[] coords, boolean[] options) {
        originQueryDirector.setOptions(options);
        originQueryDirector.buildQuery(coords[0], coords[1], 0.001);

        ResultSet originRes = originQueryDirector.getResults();

        long originWayID = -1;
        try {
            originRes.next();
            originWayID = originRes.getLong(1);// id number
            // corresponding to the starting way
            return originWayID;

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return originWayID;
    }
}
