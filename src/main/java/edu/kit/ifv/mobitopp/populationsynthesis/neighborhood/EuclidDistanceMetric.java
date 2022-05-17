package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The Class EuclidDistanceMetric is a {@link DistanceMetric} based on the
 * euclidean distance.
 */
public class EuclidDistanceMetric implements DistanceMetric {

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
	 * {@link Point2D point}. Determine euclidean distance between the given point
	 * and the closest point on the border of the given rectangle. This operator has
	 * the commutative property, the symmetric method being
	 * {@link EuclidDistanceMetric#distance(Point2D, Rectangle2D)}.
	 *
	 * @param a the rectangle a
	 * @param b the point b
	 * @return the euclidean distance
	 */
	@Override
	public float distance(Rectangle2D rect, Point2D point) {
		if (rect.contains(point)) {
			return 0.0f;

		} else {

			double dx = max(rect.getMinX() - point.getX(), point.getX() - rect.getMaxX());
			dx = max(0, dx);
			double dy = max(rect.getMinY() - point.getY(), point.getY() - rect.getMaxY());
			dy = max(0, dy);

			return (float) sqrt(dx * dx + dy * dy);

		}
	}

	/**
	 * Computes the distance between the given {@link Point2D point} and
	 * {@link Rectangle2D rectangle}. Determine euclidean distance between the given
	 * point and the closest point on the border of the given rectangle. This
	 * operator has the commutative property, the symmetric method being
	 * {@link EuclidDistanceMetric#distance(Rectangle2D, Point2D)}.
	 *
	 * @param a the point a
	 * @param b the rectangle b
	 * @return the euclidean distance
	 */
	@Override
	public float distance(Point2D point, Rectangle2D rect) {
		return distance(rect, point);
	}

	/**
	 * Computes the euclidean distance between the given {@link Point2D points}.
	 *
	 * @param a the point a
	 * @param b the point b
	 * @return the euclidean distance
	 */
	@Override
	public float distance(Point2D a, Point2D b) {
		return (float) a.distance(b);
	}

}
