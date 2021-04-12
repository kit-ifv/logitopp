package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestKnnSearch {
	
	private Collection<Point2D> points;
	private Point2D target;
	
	private Collection<Point2D> geoPoints;
	private Point2D geoTarget;
	
	@BeforeEach
	public void setUp() {
		RandomPointProvider rpp = new RandomPointProvider();
		
		points = rpp.nRandomPoints(0, 100, 1000);
		target = rpp.randomPoint(0, 100);
		
		geoPoints = rpp.nRandomPoints(8.3461,8.4488, 48.9871, 49.0261, 100000);
		geoTarget = rpp.randomPoint(8.3461,8.4488, 48.9871, 49.0261);
	}
	
	@Test
	public void testLargeKnn() {
		KdTree<Point2D> tree = new KdTree<Point2D>(points, Function.identity(), 4, new EuclidDistanceMetric());
		Leaf<Point2D> targetLeaf = new Leaf<Point2D>(target, Function.identity());
		
		Collection<Leaf<Point2D>> knn = KNNSearch.findKNN(tree, targetLeaf, 5, 100);
		assertCorrectNeighbors(knn, tree, targetLeaf, 5, 100);
	}
	
	@Test
	public void testLargeGeoKnn() {
		KdTree<Point2D> tree = new KdTree<Point2D>(geoPoints, Function.identity(), 4, new GeoDistanceMetric());
		Leaf<Point2D> target = new Leaf<Point2D>(geoTarget, Function.identity());
		target.setMetric(new GeoDistanceMetric());
		
		Collection<Leaf<Point2D>> knn = KNNSearch.findKNN(tree, target, 25, 0.05);
		assertCorrectNeighbors(knn, tree, target, 25, 100);
	}
	
	@Test
	public void testLargeKnnAll() {
		KdTree<Point2D> tree = new KdTree<Point2D>(points, Function.identity(), 4, new EuclidDistanceMetric());

		for (Leaf<Point2D> target: tree.getLeafs()) {

			Collection<Leaf<Point2D>> knn = KNNSearch.findKNN(tree, target, 5, 10);

			assertCorrectNeighbors(knn, tree, target, 5, 100);
			
		}

	}
	
	
	private void assertCorrectNeighbors(Collection<Leaf<Point2D>> knn, KdTree<Point2D> tree, Leaf<Point2D> target, int k, double maxDist) {
		assertTrue(knn.size() <= k);
		
		float maxMinDist = knn.stream().map(p -> p.distance(target)).max(Comparator.naturalOrder()).orElse(0.0f);
		
		for (Leaf<Point2D> n : knn) {
			assertTrue(n.distance(target) <= maxDist, "" + n.getObject() + " surpasses the maximum distance of "+ maxDist + ": " + n.distance(target));
		}
		
		for(Leaf<Point2D> n : tree.getLeafs()) {
			if (!knn.contains(n) && !target.equals(n)) {
				assertTrue(n.distance(target) >= maxMinDist, "" + n.getObject() + " is closer to " + target + " than found neighbors: " + n.distance(target) + " < " + maxMinDist);
			}
		}
	}
	
}
