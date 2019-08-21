package com.lee.runrouter.dbconnection.queries;


import com.lee.runrouter.bbcalculator.BBCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Director class for the QueryBuilder implementation. Sets optional parameters and initialises
 * the Prepared Statement. The parent class' get method is then used to execute the query
 * and retrieve the returned ResultSet.
 */
@Component
@Qualifier("WayQueryDirectorWithin")
public class WayQueryDirectorWithin extends QueryDirector {
    @Autowired
    public WayQueryDirectorWithin(@Qualifier("WayQueryBuilderWithin") QueryBuilder qb, BBCalculator boundingBoxCalculator) {
        super(qb, boundingBoxCalculator);
    }

    public void buildQuery(double startLat, double startLon, double runLength) {
        double[] origin = new double[] {startLat, startLon};
        qb.reset();
        qb.setBBCoords(null, origin);
        qb.setRunLength(runLength);
        qb.setHighWayOptions(this.options);
        this.ps = qb.getPreparedStament();

        // reset options to all true
        this.options = new boolean[]{true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
    }
}
