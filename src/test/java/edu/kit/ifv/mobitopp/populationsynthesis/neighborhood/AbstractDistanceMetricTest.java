package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractDistanceMetricTest {
	
	private static final double eps = 0.01;
	private DistanceMetric metric;
	private double expectedDistance;

	@BeforeEach
	public void setUp() {
		this.metric = createMetric();
		this.expectedDistance = getExpectedDistance();
	}
	
	protected abstract DistanceMetric createMetric();
	
	protected abstract double getExpectedDistance();
	
	@Test
	public void pointToPointDistance() {	
		Point2D p1 = new Point2D.Double(8.0, 49.0);
		Point2D p2 = new Point2D.Double(9.0, 48.0);
		
		assertEquals(expectedDistance, metric.distance(p1, p2), eps);
		assertEquals(expectedDistance, metric.distance(p2, p1), eps);
	}
	
	@Test
	public void pointToRectangleDistance() {
		Rectangle2D rect = new Rectangle2D.Double(7.0, 47.0, 2.0, 2.0);
		
		Point2D inside = new Point2D.Double(8.0, 48.0);
		assertEquals(0.0, metric.distance(inside, rect), eps);
		assertEquals(0.0, metric.distance(rect, inside), eps);
		
		Point2D border = new Point2D.Double(9.0, 48.5);
		assertEquals(0.0, metric.distance(border, rect), eps);
		assertEquals(0.0, metric.distance(rect, border), eps);
		
		Point2D corner = new Point2D.Double(9.0, 49.0);
		assertEquals(0.0, metric.distance(corner, rect), eps);
		assertEquals(0.0, metric.distance(rect, corner), eps);
		
		Point2D left = new Point2D.Double(6.0, 48.0);
		Point2D ref = new Point2D.Double(7.0, 48.0);
		assertEquals(metric.distance(left, ref), metric.distance(left, rect), eps);
		assertEquals(metric.distance(left, ref), metric.distance(rect, left), eps);
		
		Point2D right = new Point2D.Double(10.0, 48.0);
		ref = new Point2D.Double(9.0, 48.0);
		assertEquals(metric.distance(right, ref), metric.distance(right, rect), eps);
		assertEquals(metric.distance(right, ref), metric.distance(rect, right), eps);
		
		Point2D above = new Point2D.Double(8.0, 50.0);
		ref = new Point2D.Double(8.0, 49.0);
		assertEquals(metric.distance(above, ref), metric.distance(above, rect), eps);
		assertEquals(metric.distance(above, ref), metric.distance(rect, above), eps);
		
		Point2D below = new Point2D.Double(8.0, 46.0);
		ref = new Point2D.Double(8.0, 47.0);
		assertEquals(metric.distance(below, ref), metric.distance(below, rect), eps);
		assertEquals(metric.distance(below, ref), metric.distance(rect, below), eps);
		
		Point2D aboveLeft = new Point2D.Double(6.0, 50.0);
		ref = new Point2D.Double(7.0, 49.0);
		assertEquals(metric.distance(aboveLeft, ref), metric.distance(aboveLeft, rect), eps);
		assertEquals(metric.distance(aboveLeft, ref), metric.distance(rect, aboveLeft), eps);
		
		Point2D aboveRight = new Point2D.Double(10.0, 50.0);
		ref = new Point2D.Double(9.0, 49.0);
		assertEquals(metric.distance(aboveRight, ref), metric.distance(aboveRight, rect), eps);
		assertEquals(metric.distance(aboveRight, ref), metric.distance(rect, aboveRight), eps);
		
		Point2D belowLeft = new Point2D.Double(6.0, 46.0);
		ref = new Point2D.Double(7.0, 47.0);
		assertEquals(metric.distance(belowLeft, ref), metric.distance(belowLeft, rect), eps);
		assertEquals(metric.distance(belowLeft, ref), metric.distance(rect, belowLeft), eps);
		
		Point2D belowRight = new Point2D.Double(10.0, 46.0);
		ref = new Point2D.Double(9.0, 47.0);
		assertEquals(metric.distance(belowRight, ref), metric.distance(belowRight, rect), eps);
		assertEquals(metric.distance(belowRight, ref), metric.distance(rect, belowRight), eps);
	}
	
	@Test
	public void rectangleToRectangleDistance() {
		Rectangle2D rect = new Rectangle2D.Double(1.0, 1.0, 1.0, 1.0);
		
		assertThrows(UnsupportedOperationException.class, () -> {metric.distance(rect, rect);});
	}
	

}
