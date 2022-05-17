package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.util.Collection;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;

/**
 * The Interface Node represents nodes within a {@link KdTree} and provides
 * methods to measure the distance between Nodes.
 * Nodes can encapsulate entities of generic type.
 *
 * @param <O> the generic type of the encapsulated entities.
 */
public interface Node<O> {

	/**
	 * Compute the distance of this {@link Node} to the given {@link Node}.
	 *
	 * @param node the target node
	 * @return the distance
	 */
	public float distance(Node<O> node);

	/**
	 * Compute the distance of this {@link Node} to the given {@link Leaf}.
	 *
	 * @param leaf the target leaf node
	 * @return the distance
	 */
	public float distanceTo(Leaf<O> leaf);

	/**
	 * Compute the distance of this {@link Node} to the given {@link InnerNode}.
	 *
	 * @param innerNode the target inner node
	 * @return the distance
	 */
	public float distanceTo(InnerNode<O> innerNode);

	/**
	 * Gets the {@link Leaf leaves} in the sub-tree of this {@link Node}.
	 *
	 * @return the nested leaves
	 */
	public Collection<Leaf<O>> getNestedLeaves();

	/**
	 * Accepts the given {@link PriorityQueue} to process the children of this {@link Node}.
	 *
	 * @param queue  the priority queue
	 * @param target the target leaf node
	 * @return the leaf if there are no children
	 */
	public Leaf<O> acceptQueue(PriorityQueue<Node<O>> queue, Leaf<O> target);

	/**
	 * Returns a tree representation of the sub-tree with this {@link Node} as root.
	 *
	 * @param indent the indentation
	 * @return the tree representation
	 */
	public String treeFormat(String indent);
}
