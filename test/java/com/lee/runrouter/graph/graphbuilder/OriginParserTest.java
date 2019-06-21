package com.lee.runrouter.graph.graphbuilder;

import com.lee.runrouter.dbconnection.queries.OriginQueryBuilder;
import com.lee.runrouter.dbconnection.queries.OriginQueryDirector;
import com.lee.runrouter.distancecalc.ScaledBBCalculator;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

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
    public void checkReturnedIDMatchesSQL() throws SQLException {
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446586, -0.125309};
        long id = op.getOriginWayID(coords, opts);

        // originWay closest to the giving coordinates
        assertEquals(12540900, id);
    }

    @Test
    public void checkReturnedIDMatchesSQLTwo() throws SQLException {
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446586, -0.125309};
        long id = op.getOriginWayID(coords, opts);

        // originWay closest to the giving coordinates
        assertEquals(12540900, id);
    }
}