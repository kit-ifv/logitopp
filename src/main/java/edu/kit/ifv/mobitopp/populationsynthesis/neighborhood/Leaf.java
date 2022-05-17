package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class Leaf is a {@link Node} that has no children and encapsulates an
 * entity of generic a type.
 *
 * @param <O> the generic type of the ecapsulated entity
 */
public class Leaf<O> implements Node<O> {

	@Getter
	private Point2D point = null;

	@Getter
	private O object = null;

	@Setter
	private DistanceMetric metric = new EuclidDistanceMetric();

	/**
	 * Instantiates a new {@link Leaf} located at the given {@link Point2D point}.
	 *
	 * @param point the location of the {@link Leaf}.
	 */
	public Leaf(Point2D point) {
		this.point = point;
	}

	/**
	 * Instantiates a new leaf at the location specified by the given coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Leaf(double x, double y) {
		this(new Point2D.Double(x, y));
	}

	/**
	 * Instantiates a new leaf encapsulating the given object. The leafs location is
	 * derived by applying the given {@link Function mapping} to the given object.
	 *
	 * @param object   the encapsulated object
	 * @param coordMap the coordinate mapping
	 */
	public Leaf(O object, Function<O, Point2D> coordMap) {
		this(coordMap.apply(object));
		this.object = object;
	}

	/**
	 * Instantiates a new leaf encapsulation the given object at the given
	 * {@link Point2D location}.
	 *
	 * @param object the encapsulated object
	 * @param point  the location
	 */
	public Leaf(O object, Point2D point) {
		this(object, o -> point);
	}

	/**
	 * Gets the {@link Leaf leaves} in the sub-tree of this {@link Node}. As this is
	 * a {@link Leaf} node, it only returns this.
	 *
	 * @return the nested leaves i.e. a list containing this {@link Leaf}
	 */
	@Override
	public Collection<Leaf<O>> getNestedLeaves() {
		return List.of(this);
	}

	/**
	 * Compute the distance of this {@link Leaf} to the given {@link Node}.
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
	 * Compute the distance of this {@link Leaf} to the given {@link Leaf}.
	 *
	 * @param leaf the target leaf node
	 * @return the distance
	 */
	@Override
	public float distanceTo(Leaf<O> leaf) {
		return metric.distance(this.getPoint(), leaf.getPoint());
	}

	/**
	 * Compute the distance of this {@link Leaf} to the given {@link InnerNode}.
	 *
	 * @param innerNode the target inner node
	 * @return the distance
	 */
	@Override
	public float distanceTo(InnerNode<O> innerNode) {
		return metric.distance(this.getPoint(), innerNode.getRect());
	}

	/**
	 * Accepts the given {@link PriorityQueue} to process the children of this {@link Node}.
	 * As this is a {@link Leaf} node, only returns itself.
	 *
	 * @param queue  the priority queue
	 * @param target the target leaf node
	 * @return this leaf
	 */
	@Override
	public Leaf<O> acceptQueue(PriorityQueue<Node<O>> queue, Leaf<O> target) {
		return this;
	}

	/**
	 * Gets the x coordinate.
	 *
	 * @return the x coordinate
	 */
	public double getX() {
		return this.point.getX();
	}

	/**
	 * Gets the y coordinate.
	 *
	 * @return the y coordinate
	 */
	public double getY() {
		return this.point.getY();
	}

	/**
	 * Returns a tree representation of the sub-tree with this {@link Node} as root.
	 *
	 * @param indent the indentation
	 * @return the tree representation
	 */
	public String treeFormat(String indent) {
		return indent + String.valueOf(this.object) + " [" + this.point.getX() + "," + this.point.getY() + "]" + "\n";
	}

}
