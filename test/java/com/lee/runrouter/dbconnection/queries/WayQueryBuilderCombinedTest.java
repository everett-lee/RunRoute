package com.lee.runrouter.dbconnection.queries;

import com.lee.runrouter.bbcalculator.BBCalculator;
import com.lee.runrouter.bbcalculator.ScaledBBCalculator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WayQueryBuilderCombinedTest {
    QueryDirector qd;
    BBCalculator calc;
    QueryBuilder qb;

    // the PostGIS SQL query
    private final String SELECT = "SELECT id, tags, nodes, length, coords, startElevation, endElevation \n";
    private final String FROM = "\tFROM lineCombinedWithWay \n";
    private final String BB = "\tWHERE l.way @ ST_MakeEnvelope(?,?,?,?, 4326)\n";
    private final String ROAD_OPTIONS = "\tAND ((highway IN ('trunk', 'primary', 'secondary', 'tertiary', 'unclassified', 'residential', 'living_street', 'service', 'pedestrian', 'track', 'road', 'footway', 'bridleway', 'steps', 'path') \n";
    private final String FOOT = "\tAND (foot <> 'no' OR foot IS NULL))";
    private final String END = "\tOR (highway='cycleway' and foot='yes'))";
    private String sql;

    @Before
    public void setUp() {
        qb = new WayQueryBuilderEnvelope();
        calc = new ScaledBBCalculator();
        qd = new WayQueryDirectorEnvelope(qb, calc);

        sql = SELECT + FROM;
    }

    @Test
    public void testPreparedStatementCoordsCorrectOne() {
        qd.buildQuery(52, 4, 5000);
        double[] BBCords = calc.calcBoundingBox(52, 4, 5000);

        String newBB = String.format("\tWHERE way @ ST_MakeEnvelope(%s,%s,%s,%s, 4326)\n",
                BBCords[0], BBCords[1], BBCords[2], BBCords[3]);

        sql += newBB + ROAD_OPTIONS + FOOT + END;

        assertEquals(sql, qd.ps.toString()); // Assert the query string generated by the query director is
        // equal to the required query string
    }

    @Test
    public void testPreparedStatementCoordsCorrectTwo() {
        qd.buildQuery(25, 0, 0.2);
        double[] BBCords = calc.calcBoundingBox(25, 0, 0.2);

        String newBB = String.format("\tWHERE way @ ST_MakeEnvelope(%s,%s,%s,%s, 4326)\n",
                BBCords[0], BBCords[1], BBCords[2], BBCords[3]);

        sql += newBB + ROAD_OPTIONS + FOOT + END;

        assertEquals(sql, qd.ps.toString()); // Assert the query string generated by the query director is
        // equal to the required query string
    }

    @Test
    public void testPreparedStatementOptionsCorrectOne() {

        double[] BBCords = calc.calcBoundingBox(50, 0, 5);

        qd.setOptions(new boolean[] {true, false, false, false, false, false, false, false, false,
                false, false, false, false, true, true}); // set the new options
        qd.buildQuery(50, 0, 5);


        String newBB = String.format("\tWHERE way @ ST_MakeEnvelope(%s,%s,%s,%s, 4326)\n",
                BBCords[0], BBCords[1], BBCords[2], BBCords[3]);

        String newRoadOptions = "\tAND ((highway IN ('trunk', '', '', ''," +
                " '', '', '', '', '', '', '', '', '', 'steps', 'path') \n";

        sql += newBB + newRoadOptions + FOOT + END;

        assertEquals(sql, qd.ps.toString()); // Assert the query string generated by the query director is
        // equal to the required query string
    }


    @Test
    public void testPreparedStatementOptionsCorrectTwo() {

        double[] BBCords = calc.calcBoundingBox(50, 0, 5);

        qd.setOptions(new boolean[] {false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false}); // set the new options
        qd.buildQuery(50, 0, 5);


        String newBB = String.format("\tWHERE way @ ST_MakeEnvelope(%s,%s,%s,%s, 4326)\n",
                BBCords[0], BBCords[1], BBCords[2], BBCords[3]);

        String newRoadOptions = "\tAND ((highway IN ('', '', '', ''," +
                " '', '', '', '', '', '', '', '', '', '', '') \n";

        sql += newBB + newRoadOptions + FOOT + END;

        assertEquals(sql, qd.ps.toString()); // Assert the query string generated by the query director is
        // equal to the required query string
    }
}