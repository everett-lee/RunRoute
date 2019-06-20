package com.lee.runrouter.dbconnection.queries;


import com.lee.runrouter.distancecalc.BBCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Director class for the QueryBuilder implementation. Sets optional parameters and initialises
 * the Prepared Statement. The parent class' get method is then used to execute the query
 * and retrieve the returned ResultSet.
 */
@Component
public class WayQueryDirector extends QueryDirector {
    @Autowired
    public WayQueryDirector(QueryBuilder qb, BBCalculator distanceCalc) {
        super(qb, distanceCalc);
    }

    public void buildQuery(double startLat, double startLon, double distance) {
        double[] BBCoords = distanceCalc.calcBoundingBox(startLat, startLon, distance);

        qb.reset();
        qb.setBBCoords(BBCoords);
        qb.setHighWayOptions(this.options);
        this.ps = qb.getPreparedStament();

        // reset options to all true
        this.options = new boolean[]{true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
    }
}
