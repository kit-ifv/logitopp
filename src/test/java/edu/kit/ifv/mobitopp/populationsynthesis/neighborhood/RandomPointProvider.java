package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.util.stream.Collectors.toList;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomPointProvider {
	private Random random = new Random(42);
	
	public Point2D randomPoint(int min, int max) {
		int delta = max-min;
		return new Point2D.Double(min+random.nextDouble()*delta, min+random.nextDouble()*delta);
	}
	
	public Point2D randomPoint(double minX, double maxX, double minY, double maxY) {
		double deltaX = maxX-minX;
		double deltaY = maxY-minY;
		return new Point2D.Double(minX+random.nextDouble()*deltaX, minY+random.nextDouble()*deltaY);
	}
	
	
	public Collection<Point2D> nRandomPoints(int min, int max, int n) {
		return IntStream.range(0, n).mapToObj(i -> randomPoint(min, max)).collect(toList());
	}
	
	public Collection<Point2D> nRandomPoints(double minX, double maxX, double minY, double maxY, int n) {
		return IntStream.range(0, n).mapToObj(i -> randomPoint(minX, maxX, minY, maxY)).collect(toList());
	}
}
