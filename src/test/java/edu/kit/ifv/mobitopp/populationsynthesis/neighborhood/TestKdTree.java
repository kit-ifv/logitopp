package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class TestKdTree {
	private static final int NODE_CAPACITY = 10;
	private static final double EPS = 0.000001;
	private KdTree<Point2D> tree;
	private Collection<Point2D> points;
	
	@BeforeEach
	public void setUp() {
		points = new RandomPointProvider().nRandomPoints(50, 950, 1000);
	}
	
	@Test
	public void testSimpleTreeOfPoints() throws InterruptedException {
		tree = new KdTree<Point2D>(points, NODE_CAPACITY, new EuclidDistanceMetric());
		
		assertContainment((InnerNode<Point2D>) tree.getRoot());
	}
	
	@Test
	public void testSimpleTree() throws InterruptedException {
		tree = new KdTree<Point2D>(points, Function.identity(), NODE_CAPACITY, new EuclidDistanceMetric());
		
		assertContainment((InnerNode<Point2D>) tree.getRoot());
	}
	
	private void assertContainment(InnerNode<Point2D> node) {
		Rectangle2D rect = node.getRect();
		
		for (Point2D point : node.getNestedLeaves().stream().map(Leaf::getPoint).collect(toList())) {
			String msg = "rect " + rect.toString() + " does not contain leaf " +  String.valueOf(point);
			
			assertTrue(rect.getMinX() - EPS <= point.getX(), msg);
			assertTrue(rect.getMinY() - EPS <= point.getY(), msg);
			assertTrue(point.getX() <= rect.getMaxX() + EPS, msg);
			assertTrue(point.getY() <= rect.getMaxY() + EPS, msg);

		}
		
		for (Node<Point2D> child : node.getChildren()) {
			if (child instanceof InnerNode<?>) {				
				assertEquals(2, node.getChildren().size());
				assertContainment((InnerNode<Point2D>) child);
			} else {
				assertTrue(node.getChildren().size() <= NODE_CAPACITY);
			}
		}
		
	}
	
	@Test
	public void emptyTree() {
		tree = new KdTree<Point2D>(new ArrayList<>(), Function.identity(), NODE_CAPACITY, new EuclidDistanceMetric());
		
		assertNull(tree.getRoot());
		assertTrue(tree.isEmpty());
	}
	
	@Test
	public void singeltonTree() {
		Leaf<Point2D> leaf = new Leaf<>(1,1);
		tree = new KdTree<Point2D>(Arrays.asList(leaf), NODE_CAPACITY, new EuclidDistanceMetric());
		
		assertEquals(tree.getRoot(), leaf);
		assertFalse(tree.isEmpty());
	}
	
}
