package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import edu.kit.ifv.mobitopp.routing.util.SimplePQ;

public class KNNSearch<O> {
	
	public static <O> Collection<O> findKNN(KdTree<O> tree, O target, int k, double maxDist) {
		Leaf<O> targetLeaf = tree.getLeafs().stream().filter(l -> l.getObject().equals(target)).findFirst().orElse(null);
		
		if (targetLeaf == null) {
			throw new IllegalArgumentException("The given target object is not contained within the given KdTree!");
		}
		
		return findKNN(tree, targetLeaf, k, maxDist).stream().map(Leaf::getObject).collect(Collectors.toList());
	}
	
	
	public static <O> Collection<Leaf<O>> findKNN(KdTree<O> tree, Leaf<O> target, int k, double maxDist) {
		PriorityQueue<Node<O>> queue = new SimplePQ<Node<O>>();
		queue.add(tree.getRoot(), 0.0f);
		
		Collection<Leaf<O>> neighbors = new ArrayList<Leaf<O>>();
		
		int step = 0;
		while (!queue.isEmpty() && neighbors.size() < k) {
			//System.out.println("Step " + (step++) + ": q-size = " + queue.size() + ", result = " + neighbors.size() + "/" + k);
			
			Node<O> minNode = queue.deleteMin();
			
			if (minNode.distance(target) <= maxDist) {
				Leaf<O> newNeighbor = minNode.acceptQueue(queue, target);				
				if (newNeighbor != null) {
					neighbors.add(newNeighbor);
				}
			}
			

		}
				
		return neighbors;
	}
	
	
}
