package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The Interface DistanceMetric provides methods for computing the distance
 * between {@link Rectangle2D rectangles} and {@link Point2D points}.
 */
public interface DistanceMetric {

	/**
	 * Computes the distance between the given {@link Rectangle2D rectangles}.
	 *
	 * @param a the rectangle a
	 * @param b the rectangle b
	 * @return the distance
	 */
	public float distance(Rectangle2D a, Rectangle2D b);

	/**
	 * Computes the distance between the given {@link Rectangle2D rectangle} and
	 * {@link Point2D point}.
	 *
	 * @param a the rectangle a
	 * @param b the point b
	 * @return the distance
	 */
	public float distance(Rectangle2D a, Point2D b);

	/**
	 * Computes the distance between the given {@link Point2D point} and
	 * {@link Rectangle2D rectangle}.
	 *
	 * @param a the point a
	 * @param b the rectangle b
	 * @return the distance
	 */
	public float distance(Point2D a, Rectangle2D b);

	/**
	 * Computes the distance between the given {@link Point2D points}.
	 *
	 * @param a the point a
	 * @param b the point b
	 * @return the distance
	 */
	public float distance(Point2D a, Point2D b);

}
