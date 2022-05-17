package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import lombok.Getter;

/**
 * The Class Leaf is a {@link Node} that has no children and encapsulates an
 * entity of generic a type.
 *
 * @param <O> the generic type of the ecapsulated entity
 */
public class InnerNode<O> implements Node<O> {

	@Getter
	private Rectangle2D rect = null;

	@Getter
	private List<Node<O>> children = new ArrayList<Node<O>>();

	private DistanceMetric metric;

	/**
	 * Instantiates a new {@link InnerNode} covering the given {@link Rectangle2D
	 * area} with the given {@link DistanceMetric}.
	 *
	 * @param area   the area covered by the inner node
	 * @param metric the distance metric to be used
	 */
	public InnerNode(Rectangle2D area, DistanceMetric metric) {
		this.rect = area;
		this.metric = metric;
	}

	/**
	 * Compute the distance of this {@link InnerNode} to the given {@link Node}.
	 * Following the visitor pattern, this visits the {@link Node#distanceTo(Leaf)}
	 * or {@link Node#distanceTo(InnerNode)} method to determine the concrete
	 * distance operator.
	 *
	 * @param node the target node
	 * @return the distance
	 */
	@Override
	public float distance(Node<O> node) {
		return node.distanceTo(this);
	}

	/**
	 * Adds the given {@link Node} as child of this {@link InnerNode}.
	 *
	 * @param node the child node to be added
	 */
	public void addChild(Node<O> node) {
		this.children.add(node);
	}

	/**
	 * Adds the given {@link Node nodes} as children of this {@link InnerNode}.
	 *
	 * @param nodes the nodes
	 */
	public void addChildren(Collection<Node<O>> nodes) {
		this.children.addAll(nodes);
	}

	/**
	 * Gets the {@link Leaf leaves} in the sub-tree of this {@link InnerNode}.
	 *
	 * @return the nested leaves
	 */
	public Collection<Leaf<O>> getNestedLeaves() {
		List<Leaf<O>> leaves = new ArrayList<>();

		for (Node<O> child : getChildren()) {
			leaves.addAll(child.getNestedLeaves());
		}

		return leaves;
	}

	/**
	 * Compute the distance of this {@link InnerNode} to the given {@link Leaf}.
	 *
	 * @param leaf the target leaf node
	 * @return the distance
	 */
	@Override
	public float distanceTo(Leaf<O> leaf) {
		return metric.distance(this.getRect(), leaf.getPoint());
	}

	/**
	 * Compute the distance of this {@link InnerNode} to the given {@link InnerNode}.
	 *
	 * @param innerNode the target inner node
	 * @return the distance
	 */
	@Override
	public float distanceTo(InnerNode<O> innerNode) {
		return metric.distance(this.getRect(), innerNode.getRect());
	}

	/**
	 * Accepts the given {@link PriorityQueue} to process the children of this {@link InnerNode}.
	 * Adds all children to the queue using their distance to the given target node as priority.
	 * Returns null as this is not a {@link Leaf} node.
	 * 
	 * @param queue  the priority queue
	 * @param target the target leaf node
	 * @return null as this is not a leaf node
	 */
	@Override
	public Leaf<O> acceptQueue(PriorityQueue<Node<O>> queue, Leaf<O> target) {

		for (Node<O> child : this.children) {
			queue.add(child, child.distance(target));
		}

		return null;
	}

	/**
	 * Returns a tree representation of the sub-tree with this {@link Node} as root.
	 *
	 * @param indent the indentation
	 * @return the tree representation
	 */
	public String treeFormat(String indent) {
		String str = indent + "Box [x=" + this.rect.getMinX() + ",y=" + this.rect.getMinY() + ",w="
				+ this.rect.getWidth() + ",h=" + this.rect.getHeight() + "]: \n";

		for (Node<O> child : this.children) {
			str += child.treeFormat(indent + "  ");
		}

		return str;
	}

}
