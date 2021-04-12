package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface DistanceMetric {
	
	public float distance(Rectangle2D a, Rectangle2D b);
	
	public float distance(Rectangle2D a, Point2D b);
	
	public float distance(Point2D a, Rectangle2D b);
	
	public float distance(Point2D a, Point2D b);

}
