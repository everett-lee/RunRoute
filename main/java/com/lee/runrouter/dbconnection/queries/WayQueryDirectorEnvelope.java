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
@Qualifier("WayQueryDirectorEnvelope")
public class WayQueryDirectorEnvelope extends QueryDirector {
    @Autowired
    public WayQueryDirectorEnvelope(@Qualifier("WayQueryBuilderEnvelope") QueryBuilder qb, BBCalculator distanceCalc) {
        super(qb, distanceCalc);
    }

    public void buildQuery(double startLat, double startLon, double runLength) {
        double[] BBCoords = boundingBoxCalculator.calcBoundingBox(startLat, startLon, runLength);
        double[] origin = new double[] {startLat, startLon};

        qb.reset();
        qb.setBBCoords(BBCoords, origin);
        qb.setHighWayOptions(this.options);
        this.ps = qb.getPreparedStament();

        // reset options to all true
        this.options = new boolean[]{true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
    }
}
