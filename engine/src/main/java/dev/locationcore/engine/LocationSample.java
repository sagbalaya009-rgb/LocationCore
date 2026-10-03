package dev.locationcore.engine;

/** Coherent engine output; integration layers map it to Android Location objects. */
public final class LocationSample {
    public final GeoPoint point;
    public final double horizontalAccuracyMetres;
    public final double speedMetresPerSecond;
    public final Double bearingDegrees;
    public final long wallTimeMillis;
    public final long elapsedRealtimeNanos;
    public final double routeFractionComplete;

    LocationSample(GeoPoint point, double horizontalAccuracyMetres, double speedMetresPerSecond,
                   Double bearingDegrees, long wallTimeMillis, long elapsedRealtimeNanos,
                   double routeFractionComplete) {
        this.point = point;
        this.horizontalAccuracyMetres = horizontalAccuracyMetres;
        this.speedMetresPerSecond = speedMetresPerSecond;
        this.bearingDegrees = bearingDegrees;
        this.wallTimeMillis = wallTimeMillis;
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
        this.routeFractionComplete = routeFractionComplete;
    }
}

