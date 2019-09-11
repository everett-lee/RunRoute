package com.lee.runrouter.dbconnection.queries;

import org.junit.Before;
import org.junit.Test;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

public class OriginQueryBuilderTest {
    OriginQueryBuilder oqb;

    @Before
    public void setUp() {
        this.oqb = new OriginQueryBuilder();
    }

    @Test
    public void testPreparedStatementCorrect() {
        String expected = "SELECT l.osm_id\n" +
                "FROM planet_osm_line l\n" +
                "WHERE l.way && ST_MakeEnvelope(-0.12821094451410836, 51.444895356788166, -0.12243905548589162, 51.448492643211836, 4326)\n" +
                "AND (l.highway IN ('trunk', 'primary', 'secondary', 'tertiary', 'unclassified', 'residential', 'living_street', 'service', 'pedestrian', 'track', 'road', 'footway', 'bridleway', 'steps', 'path', 'bicycle'))\n" +
                "AND l.name IS NOT NULL\n" +
                "ORDER BY ST_Distance(l.way, ST_MakePoint(51.446694, -0.125325)) limit 1";

        double[] coords = {-0.12821094451410836, 51.444895356788166, -0.12243905548589162, 51.448492643211836};
        double[] origin = {-0.125325, 51.446694};
        oqb.setBBCoords(coords, origin);
        oqb.setHighWayOptions(new boolean[] {true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true});
        PreparedStatement out = oqb.getPreparedStament();

        assertEquals(expected, out.toString());
    }

    @Test
    public void testPreparedStatementCorrectAllFalse() {
        String expected = "SELECT l.osm_id\n" +
                "FROM planet_osm_line l\n" +
                "WHERE l.way && ST_MakeEnvelope(1.0, 2.0, 3.0, 4.0, 4326)\n" +
                "AND (l.highway IN ('', '', '', '', '', '', '', '', '', '', '', '', '', '', '', 'bicycle'))\n" +
                "AND l.name IS NOT NULL\n" +
                "ORDER BY ST_Distance(l.way, ST_MakePoint(-2.0, -4.0)) limit 1";

        double[] coords = {1, 2, 3, 4};
        double[] origin = {-4, -2};
        oqb.setBBCoords(coords, origin);
        oqb.setHighWayOptions(new boolean[] {false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false});
        PreparedStatement out = oqb.getPreparedStament();

        assertEquals(expected, out.toString());
    }
}