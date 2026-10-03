package dev.locationcore.engine;

/** Immutable WGS84 coordinate with optional altitude in metres. */
public final class GeoPoint {
    public final double latitude;
    public final double longitude;
    public final Double altitude;

    public GeoPoint(double latitude, double longitude, Double altitude) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be finite and within [-90, 90]");
        }
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be finite and within [-180, 180]");
        }
        if (altitude != null && !Double.isFinite(altitude)) {
            throw new IllegalArgumentException("Altitude must be finite");
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public GeoPoint(double latitude, double longitude) { this(latitude, longitude, null); }
}

