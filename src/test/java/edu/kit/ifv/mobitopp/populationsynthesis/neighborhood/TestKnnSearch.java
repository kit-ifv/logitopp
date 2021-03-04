package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TestKnnSearch {


	public static void main(String[] args) {
		new TestKnnSearch().testLargeKnn();
	}
	
	
	//@Test
	public void testLargeKnn() {
		List<Point2D> points = IntStream.range(0, 100).mapToObj(i -> TestKdTree.randomPoint()).collect(toList());
		
		long start = System.currentTimeMillis();
		KdTree<Point2D> tree = new KdTree<Point2D>(points, Function.identity(), 4);
		long treeDone = System.currentTimeMillis() - start;
		Leaf<Point2D> target = new Leaf<Point2D>(TestKdTree.randomPoint(), Function.identity());
		Collection<Leaf<Point2D>> knn = KNNSearch.findKNN(tree, target, 5, 100);

		long searchDone = System.currentTimeMillis() - start - treeDone;
		
		System.out.println("Build Tree: " + treeDone + "ms, Search: " + searchDone + "ms");
		
		System.out.println(knn.size());
		//System.out.println(search.getVisited());
		for (Leaf<Point2D> p : knn) {
			System.out.println(p.getPoint() + ": " + p.distance(target));
		}
		
		new KDTreeVisualizer(tree);
		
		//assertCorrectNeighbors(knn, tree, target, 5, 100);
		
		
	}
	
	//@Test
	public void testLargeKnnAll() {
		List<Point2D> points = IntStream.range(0, 10).mapToObj(i -> TestKdTree.randomPoint()).collect(toList());
		
		long start = System.currentTimeMillis();
		KdTree<Point2D> tree = new KdTree<Point2D>(points, Function.identity(), 4);
		long treeDone = System.currentTimeMillis() - start;
		System.out.println("Build Tree: " + treeDone + "ms");
		
		for (Leaf<Point2D> target: tree.getLeafs()) {
			start = System.currentTimeMillis();
			Collection<Leaf<Point2D>> knn = KNNSearch.findKNN(tree, target, 5, 100);
			long searchDone = System.currentTimeMillis() - start;
			
			System.out.print("Search: " + searchDone + "ms, found ");
			
			System.out.println("Target: " + target.getObject().toString());
			System.out.println("Knn: {" + knn.stream().map(n -> n.getObject().toString()).collect(Collectors.joining(", \n")) + "}");
			
			assertCorrectNeighbors(knn, tree, target, 5, 100);
			
		}

	}
	
	
	private void assertCorrectNeighbors(Collection<Leaf<Point2D>> knn, KdTree<Point2D> tree, Leaf<Point2D> target, int k, double maxDist) {
		System.out.println(knn.size());
		assertTrue(knn.size() <= k);
		
		tree.print();
		
		Point2D targetPoint = target.getObject();
		
		double maxMinDist = knn.stream().map(p -> p.getObject().distance(targetPoint)).max(Comparator.naturalOrder()).orElse(0.0);
		
		for (Leaf<Point2D> n : knn) {
			assertTrue(n.getObject().distance(targetPoint) <= maxDist, "" + n.getObject() + " surpasses the maximum distance of "+ maxDist + ": " + n.getObject().distance(targetPoint));
		}
		
		for(Leaf<Point2D> n : tree.getLeafs()) {
			if (!knn.contains(n) && !target.equals(n)) {
				assertTrue(n.getObject().distance(targetPoint) >= maxMinDist, "" + n.getObject() + " is closer to " + targetPoint + " than found neighbors: " + n.getObject().distance(targetPoint) + " < " + maxMinDist);
			}
		}
	}
	
}
