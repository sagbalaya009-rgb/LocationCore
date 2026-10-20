package dev.locationcore;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import dev.locationcore.engine.GeoPoint;
import dev.locationcore.engine.LocationEngine;
import dev.locationcore.engine.LocationSample;
import dev.locationcore.engine.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Owns Android test providers and guarantees best-effort cleanup on every stop path. */
final class MockLocationController {
    private static final List<String> PROVIDERS = Arrays.asList(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.FUSED_PROVIDER
    );

    private final LocationManager manager;
    private final LocationEngine engine = new LocationEngine();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<String> installed = new ArrayList<>();
    private boolean active;

    private final Runnable emitter = new Runnable() {
        @Override public void run() {
            if (!active) return;
            emitOnce();
            handler.postDelayed(this, 1000L);
        }
    };

    MockLocationController(LocationManager manager) {
        this.manager = manager;
    }

    void startStatic(double latitude, double longitude, float accuracy) {
        prepareProviders();
        try {
            engine.setStatic(new GeoPoint(latitude, longitude), accuracy);
            active = true;
            emitter.run();
        } catch (RuntimeException error) {
            stop();
            throw error;
        }
    }

    void startMovement(Route route, double speed, float accuracy) {
        startPath(route, speed, accuracy, false);
    }

    void startRoute(Route route, double speed, float accuracy) {
        startPath(route, speed, accuracy, true);
    }

    private void startPath(Route route, double speed, float accuracy, boolean gpxRoute) {
        prepareProviders();
        try {
            if (gpxRoute) engine.startRoute(route, speed, accuracy, SystemClock.elapsedRealtimeNanos());
            else engine.startMovement(route, speed, accuracy, SystemClock.elapsedRealtimeNanos());
            active = true;
            emitter.run();
        } catch (RuntimeException error) {
            stop();
            throw error;
        }
    }

    private void prepareProviders() {
        stop();
        try {
            for (String provider : PROVIDERS) {
                boolean satellite = LocationManager.GPS_PROVIDER.equals(provider);
                boolean network = LocationManager.NETWORK_PROVIDER.equals(provider);
                manager.addTestProvider(provider, network, satellite, network, false,
                        true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
                installed.add(provider);
                manager.setTestProviderEnabled(provider, true);
            }
        } catch (RuntimeException error) {
            stop();
            throw error;
        }
    }

    void stop() {
        active = false;
        handler.removeCallbacks(emitter);
        engine.stop();
        for (int i = installed.size() - 1; i >= 0; i--) {
            try { manager.removeTestProvider(installed.get(i)); }
            catch (IllegalArgumentException | SecurityException ignored) { }
        }
        installed.clear();
    }

    boolean hasMockLastKnown() {
        for (String provider : PROVIDERS) {
            try {
                Location value = manager.getLastKnownLocation(provider);
                if (value != null && value.isMock()) return true;
            } catch (IllegalArgumentException | SecurityException ignored) { }
        }
        return false;
    }

    private void emitOnce() {
        LocationSample sample = engine.sample(System.currentTimeMillis(), SystemClock.elapsedRealtimeNanos());
        if (sample == null) return;
        for (String provider : installed) {
            Location value = new Location(provider);
            value.setLatitude(sample.point.latitude);
            value.setLongitude(sample.point.longitude);
            value.setAccuracy((float) sample.horizontalAccuracyMetres);
            if (sample.point.altitude != null) value.setAltitude(sample.point.altitude);
            value.setSpeed((float) sample.speedMetresPerSecond);
            if (sample.bearingDegrees != null) value.setBearing(sample.bearingDegrees.floatValue());
            value.setTime(sample.wallTimeMillis);
            value.setElapsedRealtimeNanos(sample.elapsedRealtimeNanos);
            manager.setTestProviderLocation(provider, value);
        }
    }
}
