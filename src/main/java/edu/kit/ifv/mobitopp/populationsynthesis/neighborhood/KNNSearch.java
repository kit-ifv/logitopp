package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import edu.kit.ifv.mobitopp.routing.util.SimplePQ;

/**
 * The Class KNNSearch provides methods to find the nearest neighbors of a
 * target in a {@link KdTree}.
 *
 * @param <O> the generic type of the entities in the kD-tree
 */
public class KNNSearch<O> {

	/**
	 * Find the k nearest neighbors of the given target entity in the given {@link KdTree}.
	 *
	 * @param <O>     the generic type of the entities
	 * @param tree    the kD-tree
	 * @param target  the target entity
	 * @param k       the maximum number of neighbors to find
	 * @param maxDist the maximum distance of a nearest neighbor
	 * @return the k nearest neighbors within a maxDist radius around the target entity
	 */
	public static <O> Collection<O> findKNN(KdTree<O> tree, O target, int k, double maxDist) {
		Leaf<O> targetLeaf = tree.getLeafs().stream().filter(l -> l.getObject().equals(target)).findFirst()
				.orElse(null);

		if (targetLeaf == null) {
			throw new IllegalArgumentException("The given target object is not contained within the given KdTree!");
		}

		return findKNN(tree, targetLeaf, k, maxDist).stream().map(Leaf::getObject).collect(Collectors.toList());
	}

	/**
	 * Find the k nearest neighbors of the given target {@link Leaf} in the given {@link KdTree}.
	 *
	 * @param <O>     the generic type of the entities
	 * @param tree    the kD-tree
	 * @param target  the target leaf
	 * @param k       the maximum number of neighbors to find
	 * @param maxDist the maximum distance of a nearest neighbor
	 * @return the k nearest neighbors within a maxDist radius around the target leaf
	 */
	public static <O> Collection<Leaf<O>> findKNN(KdTree<O> tree, Leaf<O> target, int k, double maxDist) {
		PriorityQueue<Node<O>> queue = new SimplePQ<Node<O>>();
		queue.add(tree.getRoot(), 0.0f);

		Collection<Leaf<O>> neighbors = new ArrayList<Leaf<O>>();

		while (!queue.isEmpty() && neighbors.size() < k) {

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
