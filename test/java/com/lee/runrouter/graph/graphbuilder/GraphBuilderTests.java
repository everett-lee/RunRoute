package com.lee.runrouter.graph.graphbuilder;

import com.lee.runrouter.dbconnection.queries.*;
import com.lee.runrouter.graph.elementbuilder.WayBuilder;
import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.graphelement.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.*;
import static org.mockito.Mockito.*;

public class GraphBuilderTests {
    private QueryDirector mockWayQueryDirector;
    private OriginParser mockOriginParser;
    private WayBuilder wayBuilder;
    private ElementRepo repo;
    private GraphBuilder gb;
    private ResultSet mockWayResults;

    @Before
    public void setUp() {
        mockWayResults = mock(ResultSet.class);
        mockWayQueryDirector = mock(WayQueryDirector.class);
        // query director should return the ResultSet
        when(mockWayQueryDirector.getResults()).thenReturn(mockWayResults);

        mockOriginParser = mock(OriginParser.class);

        wayBuilder = new WayBuilder();
        repo = new ElementRepo();
        gb = new GraphBuilder(mockWayQueryDirector, mockOriginParser, wayBuilder, repo);
    }

    @Test
    public void testBuildOne() throws SQLException {
        String LINESTRING = "LINESTRING(0.87 51.92,0.80 51.01)";
        double[] coords = new double[]{0.0, 0.0};
        boolean[] opts = new boolean[]{};

        // return an ID corresponding to the origin Way
        when(mockOriginParser.getOriginWayID(coords, opts)).thenReturn(111L);

        when(mockWayResults.next()).thenReturn(true).thenReturn(false);
        when(mockWayResults.getLong(1)).thenReturn(111L); // id of the Way

        Array mockArr1 = mock(Array.class);
        when(mockWayResults.getArray(2)).thenReturn(mockArr1);
        // return the surface types
        when(mockArr1.getArray()).thenReturn(new String[] {"surface", "gravel", "highway", "living_street"});

        Array mockArr2 = mock(Array.class);
        when(mockWayResults.getArray(3)).thenReturn(mockArr2);
        // return mock node ids
        when(mockArr2.getArray()).thenReturn(new Long[]{10L, 20L});

        when(mockWayResults.getDouble(4)).thenReturn(42D);
        // return the mock coordinates
        when(mockWayResults.getString(5)).thenReturn(LINESTRING);
        // return mock elevation longs
        when(mockWayResults.getLong(6)).thenReturn(81L);
        when(mockWayResults.getLong(7)).thenReturn(82L);

        gb.buildGraph(coords, 5, opts);

        Way res = repo.getWayRepo().get(0);
        assertEquals(repo.getOriginWay(), res);// assert created Way (the only one) is equal to
        // the Origin
        /**
         * Assert Way contains fields matching the provided data
         */
        assertEquals(res.getSurface(), "GRAVEL");
        assertEquals(res.getHighway(), "LIVING_STREET");
        assertFalse(res.isLit());
        assertNull(res.getName());
        assertEquals(res.getLength(), 42D, 0.00001);
        assertEquals(res.getNodeContainer().getStartNode().getId(), 10L);
        assertEquals(res.getNodeContainer().getStartNode().getLat(), 51.92, 0.00001);
        assertEquals(res.getNodeContainer().getStartNode().getLon(), 0.87, 0.00001);
        assertEquals(res.getNodeContainer().getEndNode().getId(), 20L);
        assertEquals(res.getNodeContainer().getEndNode().getLat(), 51.01, 0.00001);
        assertEquals(res.getNodeContainer().getEndNode().getLon(), 0.80, 0.00001);
        assertEquals(res.getElevationPair().getStartElevation(), 81L);
        assertEquals(res.getElevationPair().getEndElevation(), 82L);
    }

    @Test
    public void testBuildTwo() throws SQLException {
        String LINESTRING = "LINESTRING(0.87 51.92)";
        double[] coords = new double[]{0.0, 0.0};
        boolean[] opts = new boolean[]{};

        when(mockOriginParser.getOriginWayID(coords, opts)).thenReturn(116L);

        when(mockWayResults.next()).thenReturn(true).thenReturn(false);
        when(mockWayResults.getLong(1)).thenReturn(116L); // id

        Array mockArr1 = mock(Array.class);
        when(mockWayResults.getArray(2)).thenReturn(mockArr1);
        when(mockArr1.getArray()).thenReturn(new String[] {"name", "test road",
                "surface", "cheese", "highway", "motorway"});

        Array mockArr2 = mock(Array.class);
        when(mockWayResults.getArray(3)).thenReturn(mockArr2);
        when(mockArr2.getArray()).thenReturn(new Long[]{10L});

        when(mockWayResults.getDouble(4)).thenReturn(42D);
        when(mockWayResults.getString(5)).thenReturn(LINESTRING);
        when(mockWayResults.getLong(6)).thenReturn(-81L);
        when(mockWayResults.getLong(7)).thenReturn(-82L);

        gb.buildGraph(coords, 5, opts);

        Way res = repo.getWayRepo().get(0);
        assertEquals(repo.getOriginWay(), res); // assert created Way (the only one) is equal to
        // the Origin
        /**
         * Assert Way contains fields matching the provided data
         */
        assertEquals(res.getSurface(), "UNDEFINED");
        assertEquals(res.getHighway(), "UNCLASSIFIED");
        assertFalse(res.isLit());
        assertEquals(res.getName(), "test road");
        assertEquals(res.getLength(), 42D, 0.00001);
        assertEquals(res.getNodeContainer().getStartNode().getId(), 10L);
        assertEquals(res.getNodeContainer().getStartNode().getLat(), 51.92, 0.00001);
        assertEquals(res.getNodeContainer().getStartNode().getLon(), 0.87, 0.00001);
        assertEquals(res.getNodeContainer().getEndNode().getId(), 10L);
        assertEquals(res.getNodeContainer().getEndNode().getLat(), 51.92, 0.00001);
        assertEquals(res.getNodeContainer().getEndNode().getLon(), 0.87, 0.00001);
        assertEquals(res.getElevationPair().getEndElevation(), -82L);
    }
}