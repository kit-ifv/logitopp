package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class TestKdTree {
	private KdTree<Integer> tree;
	private static Random random = new Random(42);
	private int cnt = 1;
	
	@BeforeEach
	public void setUp() {
		cnt = 1;
		//List<Point2D> points = IntStream.range(0, 10).mapToObj(i -> randomPoint()).collect(toList());
		tree = new KdTree<Integer>(getPoints(), 5);
	}
	
	public static Point2D randomPoint() {
		return new Point2D.Double(random.nextDouble()*100, random.nextDouble()*100);
	}
	
	public static List<Point2D> getPoints() {
		return Arrays.asList(new Point2D.Double(2.0, 1.0),
							 new Point2D.Double(1.0, 8.5),
							 new Point2D.Double(6.0, 4.0),
							 new Point2D.Double(8.5, 5.5),
							 new Point2D.Double(4.0, 6.5),
							 new Point2D.Double(3.5, 3.5),
							 new Point2D.Double(8.0, 9.5),
							 new Point2D.Double(2.5, 5.0),
							 new Point2D.Double(7.0, 6.0),
							 new Point2D.Double(4.5, 2.0)
							);
	}
	
	@Test
	public void testSimpleTree() {
		tree.print();
		
	}
}
