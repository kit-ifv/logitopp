package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.lang.Math.max;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The Class GeoDistanceMetric is a {@link DistanceMetric} based on the
 * haversine distance between geo-coordinates.
 */
public class GeoDistanceMetric implements DistanceMetric {

	private static final int R = 6371; // Radius of the earth

	/**
	 * Computes the distance between the given {@link Rectangle2D rectangles}. 
	 * This distance is not supported!!
	 *
	 * @param a the rectangle a
	 * @param b the rectangle b
	 * @return throws {@link UnsupportedOperationException}
	 */
	@Override
	public float distance(Rectangle2D a, Rectangle2D b) {
		throw new UnsupportedOperationException("Distance between two rectangels is not supported.");
	}

	/**
	 * Computes the distance between the given {@link Rectangle2D rectangle} and
	 * {@link Point2D point}. Determine haversine distance between the given point
	 * and the closest point on the border of the given rectangle. This operator has
	 * the commutative property, the symmetric method being
	 * {@link GeoDistanceMetric#distance(Point2D, Rectangle2D)}.
	 *
	 * @param rect the rectangle
	 * @param point the point
	 * @return the haversine distance
	 */
	@Override
	public float distance(Rectangle2D rect, Point2D point) {
		if (rect.contains(point)) {
			return 0.0f;

		} else {

			double dx = max(rect.getMinX() - point.getX(), point.getX() - rect.getMaxX());
			dx = max(0, dx);
			dx *= (point.getX() > rect.getMaxX()) ? -1.0 : 1.0;
			double dy = max(rect.getMinY() - point.getY(), point.getY() - rect.getMaxY());
			dy = max(0, dy);
			dy *= (point.getY() > rect.getMaxY()) ? -1.0 : 1.0;

			return distance(point, new Point2D.Double(point.getX() + dx, point.getY() + dy));

		}
	}

	/**
	 * Computes the distance between the given {@link Point2D point} and
	 * {@link Rectangle2D rectangle}. Determine haversine distance  between the given
	 * point and the closest point on the border of the given rectangle. This
	 * operator has the commutative property, the symmetric method being
	 * {@link GeoDistanceMetric#distance(Rectangle2D, Point2D)}.
	 *
	 * @param point the point
	 * @param rect the rectangle
	 * @return the haversine distance
	 */
	@Override
	public float distance(Point2D point, Rectangle2D rect) {
		return distance(rect, point);
	}

	/**
	 * Computes the haversine distance between the given {@link Point2D points}.
	 *
	 * @param a the point a
	 * @param b the point b
	 * @return the haversine distance
	 */
	@Override
	public float distance(Point2D a, Point2D b) {
		double lat1 = a.getY();
		double lat2 = b.getY();
		double lon1 = a.getX();
		double lon2 = b.getX();
		
		// distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
 
        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        // apply formulae
        double term = Math.pow(Math.sin(dLat / 2), 2) +
                   	  Math.pow(Math.sin(dLon / 2), 2) *
                      Math.cos(lat1) *
                      Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(term));
        return (float) (R * c);
//		double deltaLat = toRadians(b.getY() - a.getY());
//		double deltaLon = toRadians(b.getX() - a.getX());
//
//		double t = pow(sin(deltaLat / 2), 2)
//				+ cos(toRadians(a.getY())) * cos(toRadians(b.getY())) * pow(sin(deltaLon / 2), 2);
//
//		double c = 2 * atan2(sqrt(t), sqrt(1 - t));
//
//		return (float) (c * R);
	}

}
