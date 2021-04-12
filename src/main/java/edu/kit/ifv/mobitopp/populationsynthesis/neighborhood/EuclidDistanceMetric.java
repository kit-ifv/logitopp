package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EuclidDistanceMetric implements DistanceMetric {

	@Override
	public float distance(Rectangle2D a, Rectangle2D b) {
		throw new UnsupportedOperationException("Distance between two rectangels is not supported.");
	}

	@Override
	public float distance(Rectangle2D rect, Point2D point) {
		if (rect.contains(point)) {
			return 0.0f;
		
		} else {

			double dx = max(rect.getMinX() - point.getX(), point.getX() - rect.getMaxX());
			dx = max(0, dx);
			double dy = max(rect.getMinY() - point.getY(), point.getY() - rect.getMaxY());
			dy = max(0, dy);
			
			return (float) sqrt(dx*dx + dy*dy);
			
		}
	}

	@Override
	public float distance(Point2D point, Rectangle2D rect) {
		return distance(rect, point);
	}

	@Override
	public float distance(Point2D a, Point2D b) {
		return (float) a.distance(b);
	}


}
