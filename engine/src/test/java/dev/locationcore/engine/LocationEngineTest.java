package dev.locationcore.engine;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.Test;

public final class LocationEngineTest {
    @Test public void routeUsesShortDatelineArcAndInterpolatesAltitude() {
        Route route = new Route(Arrays.asList(new GeoPoint(0, 179, 10.0), new GeoPoint(0, -179, 30.0)));
        assertEquals(222390, route.lengthMetres(), 100);
        Route.RoutePosition halfway = route.atDistance(route.lengthMetres() / 2);
        assertEquals(0, halfway.point.latitude, 1e-6);
        assertEquals(180, Math.abs(halfway.point.longitude), 1e-6);
        assertEquals(20, halfway.point.altitude, 1e-6);
        assertEquals(90, halfway.bearingDegrees, 1e-6);
    }

    @Test public void realIsDefaultAndStopDiscardsVirtualState() {
        LocationEngine engine = new LocationEngine();
        assertEquals(LocationEngine.Mode.REAL, engine.mode());
        assertNull(engine.sample(1000, 1000));
        engine.setStatic(new GeoPoint(6, 3), 8);
        LocationSample staticSample = engine.sample(2000, 2_000_000);
        assertEquals(6, staticSample.point.latitude, 0);
        assertEquals(0, staticSample.speedMetresPerSecond, 0);
        assertEquals(2000, staticSample.wallTimeMillis);
        engine.stop();
        assertEquals(LocationEngine.Mode.REAL, engine.mode());
        assertNull(engine.sample(3000, 3_000_000));
    }

    @Test public void movementPausesResumesAndStopsAtEndpoint() {
        Route route = new Route(Arrays.asList(new GeoPoint(0, 0), new GeoPoint(0, 0.01)));
        LocationEngine engine = new LocationEngine();
        engine.startMovement(route, 10, 5, 1_000_000_000L);
        LocationSample moving = engine.sample(2000, 6_000_000_000L);
        assertEquals(10, moving.speedMetresPerSecond, 0);
        assertEquals(50, Route.distance(route.points().get(0), moving.point), 0.1);
        assertEquals(90, moving.bearingDegrees, 0.01);
        engine.pause(6_000_000_000L);
        LocationSample paused = engine.sample(9000, 9_000_000_000L);
        assertEquals(0, paused.speedMetresPerSecond, 0);
        assertEquals(moving.point.longitude, paused.point.longitude, 1e-9);
        engine.resume(10_000_000_000L);
        LocationSample resumed = engine.sample(11000, 11_000_000_000L);
        assertEquals(60, Route.distance(route.points().get(0), resumed.point), 0.1);
        LocationSample finished = engine.sample(200000, 201_000_000_000L);
        assertEquals(1, finished.routeFractionComplete, 0);
        assertEquals(0, finished.speedMetresPerSecond, 0);
        engine.stop();
        assertNull(engine.sample(201000, 202_000_000_000L));
    }

    @Test public void gpxParsesTrackAndRejectsUnsafeInput() throws Exception {
        String valid = "<gpx><trk><trkseg><trkpt lat='0' lon='0'><ele>10</ele></trkpt><trkpt lat='0' lon='1'><ele>20</ele></trkpt></trkseg></trk></gpx>";
        Route route = GpxParser.parse(new ByteArrayInputStream(valid.getBytes(StandardCharsets.UTF_8)));
        assertEquals(2, route.points().size());
        assertEquals(15, route.atDistance(route.lengthMetres() / 2).point.altitude, 1e-6);

        String unsafe = "<!DOCTYPE gpx [<!ENTITY xxe SYSTEM 'file:///etc/passwd'>]><gpx><rtept lat='0' lon='0'/><rtept lat='0' lon='1'/></gpx>";
        try {
            GpxParser.parse(new ByteArrayInputStream(unsafe.getBytes(StandardCharsets.UTF_8)));
            fail("DOCTYPE must be refused");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("unsafe"));
        }
    }

    @Test public void invalidCoordinatesAndSpeedAreRejected() {
        try { new GeoPoint(Double.NaN, 0); fail(); } catch (IllegalArgumentException expected) { }
        try { new GeoPoint(91, 0); fail(); } catch (IllegalArgumentException expected) { }
        try { new GeoPoint(0, 181); fail(); } catch (IllegalArgumentException expected) { }
        Route route = new Route(Arrays.asList(new GeoPoint(0, 0), new GeoPoint(0, 1)));
        try { new LocationEngine().startRoute(route, -1, 5, 0); fail(); } catch (IllegalArgumentException expected) { }
    }

    @Test public void duplicateWaypointsDoNotBreakEndpoints() {
        Route route = new Route(Arrays.asList(new GeoPoint(0, 0), new GeoPoint(0, 0),
                new GeoPoint(0, 0.01), new GeoPoint(0, 0.01)));
        assertEquals(0, route.atDistance(0).point.longitude, 0);
        assertEquals(0.01, route.atDistance(route.lengthMetres()).point.longitude, 1e-9);
        assertEquals(90, route.atDistance(route.lengthMetres()).bearingDegrees, 1e-6);
    }
}

