package com.lee.runrouter.graph.graphbuilder;

import com.lee.runrouter.dbconnection.queries.OriginQueryBuilder;
import com.lee.runrouter.dbconnection.queries.OriginQueryDirector;
import com.lee.runrouter.bbcalculator.ScaledBBCalculator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OriginParserTest {
    OriginParser op;
    OriginQueryBuilder oqb;
    OriginQueryDirector oqd;
    ScaledBBCalculator calc;

    @Before
    public void setUp() {
        this.oqb = new OriginQueryBuilder();
        this.calc = new ScaledBBCalculator();
        this.oqd = new OriginQueryDirector(oqb, calc);
        this.op = new OriginParser(oqd);
    }

    @Test
    public void testReturnedIDMatchesSQL() {
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446573, -0.125348};
        long id = op.getOriginWayID(coords, opts);

        // originWay closest to the giving coordinates
        assertEquals(12540900, id);
    }

    @Test
    public void testkReturnedIDMatchesSQLTwo() {
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};

        //System.out.println(calc.calcBoundingBox());

        double[] coords = {51.919199, 1.045613};
        long id = op.getOriginWayID(coords, opts);

        // originWay closest to the giving coordinates
        assertEquals(60886410, id);
    }


    @Test
    public void testkReturnedIDMatchesSQLThree() {
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};

        //System.out.println(calc.calcBoundingBox());

        double[] coords = {51.925080, 0.986514};
        long id = op.getOriginWayID(coords, opts);

        // originWay closest to the giving coordinates
        assertEquals(42202876, id);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testThrowErrorOnNoResult() {
         boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {-180, -180};
        long id = op.getOriginWayID(coords, opts);

    }
}