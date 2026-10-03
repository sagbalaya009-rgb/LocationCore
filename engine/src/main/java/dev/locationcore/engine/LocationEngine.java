package dev.locationcore.engine;

/** Volatile REAL-on-start state machine; has no Android provider privileges itself. */
public final class LocationEngine {
    public enum Mode { REAL, STATIC, MOVEMENT, ROUTE }

    private Mode mode = Mode.REAL;
    private GeoPoint staticPoint;
    private Route route;
    private double speedMetresPerSecond;
    private double accuracyMetres;
    private long startedElapsedNanos;
    private long pausedAtNanos = -1;

    public Mode mode() { return mode; }
    public boolean paused() { return pausedAtNanos >= 0; }

    public void setStatic(GeoPoint point, double accuracyMetres) {
        if (point == null) throw new IllegalArgumentException("Point required");
        validateAccuracy(accuracyMetres);
        clear();
        this.staticPoint = point;
        this.accuracyMetres = accuracyMetres;
        mode = Mode.STATIC;
    }

    public void startMovement(Route route, double speedMetresPerSecond, double accuracyMetres, long elapsedRealtimeNanos) {
        startPath(Mode.MOVEMENT, route, speedMetresPerSecond, accuracyMetres, elapsedRealtimeNanos);
    }

    public void startRoute(Route route, double replaySpeedMetresPerSecond, double accuracyMetres, long elapsedRealtimeNanos) {
        startPath(Mode.ROUTE, route, replaySpeedMetresPerSecond, accuracyMetres, elapsedRealtimeNanos);
    }

    private void startPath(Mode newMode, Route newRoute, double speed, double accuracy, long elapsed) {
        if (newRoute == null) throw new IllegalArgumentException("Route required");
        if (!Double.isFinite(speed) || speed <= 0 || speed > 100) throw new IllegalArgumentException("Speed must be (0, 100] m/s");
        validateAccuracy(accuracy);
        if (elapsed < 0) throw new IllegalArgumentException("Elapsed realtime must be nonnegative");
        clear();
        route = newRoute;
        speedMetresPerSecond = speed;
        accuracyMetres = accuracy;
        startedElapsedNanos = elapsed;
        mode = newMode;
    }

    public void pause(long elapsedRealtimeNanos) {
        if (mode != Mode.MOVEMENT && mode != Mode.ROUTE) throw new IllegalStateException("No moving route to pause");
        if (paused()) throw new IllegalStateException("Already paused");
        if (elapsedRealtimeNanos < startedElapsedNanos) throw new IllegalArgumentException("Time moved backwards");
        pausedAtNanos = elapsedRealtimeNanos;
    }

    public void resume(long elapsedRealtimeNanos) {
        if (!paused()) throw new IllegalStateException("Not paused");
        if (elapsedRealtimeNanos < pausedAtNanos) throw new IllegalArgumentException("Time moved backwards");
        startedElapsedNanos += elapsedRealtimeNanos - pausedAtNanos;
        pausedAtNanos = -1;
    }

    public LocationSample sample(long wallTimeMillis, long elapsedRealtimeNanos) {
        if (mode == Mode.REAL) return null;
        if (wallTimeMillis < 0 || elapsedRealtimeNanos < 0) throw new IllegalArgumentException("Invalid clock value");
        if (mode == Mode.STATIC) {
            return new LocationSample(staticPoint, accuracyMetres, 0, null, wallTimeMillis, elapsedRealtimeNanos, 0);
        }
        long effectiveElapsed = paused() ? pausedAtNanos : elapsedRealtimeNanos;
        if (effectiveElapsed < startedElapsedNanos) throw new IllegalArgumentException("Time moved backwards");
        double metres = (effectiveElapsed - startedElapsedNanos) / 1_000_000_000.0 * speedMetresPerSecond;
        Route.RoutePosition position = route.atDistance(metres);
        double speed = paused() || metres >= route.lengthMetres() ? 0 : speedMetresPerSecond;
        return new LocationSample(position.point, accuracyMetres, speed, position.bearingDegrees,
                wallTimeMillis, elapsedRealtimeNanos, position.fractionComplete);
    }

    public void stop() { clear(); mode = Mode.REAL; }

    private void clear() {
        staticPoint = null;
        route = null;
        speedMetresPerSecond = 0;
        accuracyMetres = 0;
        startedElapsedNanos = 0;
        pausedAtNanos = -1;
    }

    private static void validateAccuracy(double accuracy) {
        if (!Double.isFinite(accuracy) || accuracy <= 0 || accuracy > 100000) {
            throw new IllegalArgumentException("Accuracy must be (0, 100000] metres");
        }
    }
}

