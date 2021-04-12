package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GeoDistanceMetric implements DistanceMetric {
	private static final int R = 6371; // Radius of the earth
	
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
			dx *= (point.getX() > rect.getMaxX()) ? -1.0 : 1.0;
			double dy = max(rect.getMinY() - point.getY(), point.getY() - rect.getMaxY());
			dy = max(0, dy);
			dy *= (point.getY() > rect.getMaxY()) ? -1.0 : 1.0;
			
			return distance(point, new Point2D.Double(point.getX() + dx, point.getY() + dy));
			
		}
	}

	@Override
	public float distance(Point2D point, Rectangle2D rect) {
		return distance(rect, point);
	}

	@Override
	public float distance(Point2D p1, Point2D p2) {
		double deltaLat = toRadians(p2.getY() - p1.getY());
		double deltaLon = toRadians(p2.getX() - p1.getX());
		
		double a = pow(sin(deltaLat/2),2) 
					+ cos(toRadians(p1.getY())) * cos(toRadians(p2.getY())) * pow(sin(deltaLon/2), 2);
		
		double c = 2 * atan2(sqrt(a), sqrt(1-a));

		return (float) (c * R);
	}

}
