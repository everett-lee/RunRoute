package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.distancecalc.BBCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Qualifier("OriginQueryDirector")
public class OriginQueryDirector extends QueryDirector {

    @Autowired
    public OriginQueryDirector(@Qualifier("OriginQueryBuilder") QueryBuilder qb, BBCalculator distanceCalc) {
        super(qb, distanceCalc);
    }

    @Override
    public void buildQuery(double startLat, double startLon, double runLength) {
        double[] BBCoords = distanceCalc.calcBoundingBox(startLat, startLon, runLength);
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

