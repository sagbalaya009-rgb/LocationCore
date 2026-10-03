package dev.locationcore.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A route measured on a spherical Earth; no network or Android dependency. */
public final class Route {
    private static final double EARTH_RADIUS_M = 6371008.8;
    private final List<GeoPoint> points;
    private final double[] cumulativeMetres;

    public Route(List<GeoPoint> points) {
        if (points == null || points.size() < 2 || points.size() > 10000) {
            throw new IllegalArgumentException("Route requires 2 to 10000 points");
        }
        this.points = Collections.unmodifiableList(new ArrayList<>(points));
        cumulativeMetres = new double[points.size()];
        for (int i = 1; i < points.size(); i++) {
            cumulativeMetres[i] = cumulativeMetres[i - 1] + distance(points.get(i - 1), points.get(i));
        }
        if (cumulativeMetres[points.size() - 1] <= 0) {
            throw new IllegalArgumentException("Route has no movement");
        }
    }

    public List<GeoPoint> points() { return points; }
    public double lengthMetres() { return cumulativeMetres[cumulativeMetres.length - 1]; }

    public RoutePosition atDistance(double metres) {
        if (!Double.isFinite(metres)) throw new IllegalArgumentException("Distance must be finite");
        double clamped = Math.max(0, Math.min(lengthMetres(), metres));
        int low = 0, high = cumulativeMetres.length - 1;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (cumulativeMetres[middle] < clamped) low = middle + 1;
            else high = middle;
        }
        int next = Math.max(1, low);
        while (next < points.size() - 1 && cumulativeMetres[next] == cumulativeMetres[next - 1]) next++;
        int segment = next - 1;
        double span = cumulativeMetres[segment + 1] - cumulativeMetres[segment];
        double fraction = span == 0 ? 0 : (clamped - cumulativeMetres[segment]) / span;
        GeoPoint from = points.get(segment);
        GeoPoint to = points.get(segment + 1);
        GeoPoint position = interpolate(from, to, fraction);
        return new RoutePosition(position, bearing(from, to), clamped / lengthMetres());
    }

    public static double distance(GeoPoint from, GeoPoint to) {
        double lat1 = Math.toRadians(from.latitude);
        double lat2 = Math.toRadians(to.latitude);
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(to.longitude - from.longitude);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_RADIUS_M * Math.asin(Math.min(1, Math.sqrt(a)));
    }

    public static double bearing(GeoPoint from, GeoPoint to) {
        double lat1 = Math.toRadians(from.latitude);
        double lat2 = Math.toRadians(to.latitude);
        double dLon = Math.toRadians(to.longitude - from.longitude);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    public static GeoPoint interpolate(GeoPoint from, GeoPoint to, double fraction) {
        if (!Double.isFinite(fraction) || fraction < 0 || fraction > 1) {
            throw new IllegalArgumentException("Fraction must be within [0, 1]");
        }
        if (fraction == 0) return from;
        if (fraction == 1) return to;
        double angle = distance(from, to) / EARTH_RADIUS_M;
        if (angle < 1e-12) return from;
        double a = Math.sin((1 - fraction) * angle) / Math.sin(angle);
        double b = Math.sin(fraction * angle) / Math.sin(angle);
        double lat1 = Math.toRadians(from.latitude), lon1 = Math.toRadians(from.longitude);
        double lat2 = Math.toRadians(to.latitude), lon2 = Math.toRadians(to.longitude);
        double x = a * Math.cos(lat1) * Math.cos(lon1) + b * Math.cos(lat2) * Math.cos(lon2);
        double y = a * Math.cos(lat1) * Math.sin(lon1) + b * Math.cos(lat2) * Math.sin(lon2);
        double z = a * Math.sin(lat1) + b * Math.sin(lat2);
        double latitude = Math.toDegrees(Math.atan2(z, Math.hypot(x, y)));
        double longitude = Math.toDegrees(Math.atan2(y, x));
        Double altitude = from.altitude != null && to.altitude != null
                ? from.altitude + (to.altitude - from.altitude) * fraction : null;
        return new GeoPoint(latitude, longitude, altitude);
    }

    public static final class RoutePosition {
        public final GeoPoint point;
        public final double bearingDegrees;
        public final double fractionComplete;

        private RoutePosition(GeoPoint point, double bearingDegrees, double fractionComplete) {
            this.point = point;
            this.bearingDegrees = bearingDegrees;
            this.fractionComplete = fractionComplete;
        }
    }
}

