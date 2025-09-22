package com.ridematch.driver.util;

public final class GeoUtils {
    private static final double EARTH_RADIUS_M = 6_371_000.0;

    /** Compute [latMin, latMax, lngMin, lngMax] for given point + radius (meters). */
    public static double[] bbox(double lat, double lng, double radiusMeters) {
        double metersPerDegLat = 111_320.0;
        double metersPerDegLng = 111_320.0 * Math.max(0.0001, Math.cos(Math.toRadians(lat)));
        double latDelta = radiusMeters / metersPerDegLat;
        double lngDelta = radiusMeters / metersPerDegLng;
        return new double[] {lat - latDelta, lat + latDelta, lng - lngDelta, lng + lngDelta};
    }
}
