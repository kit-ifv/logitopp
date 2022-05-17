package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.util.stream.Collectors.toList;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;

/**
 * The Class KdTree is a collection of {@link Node nodes} encapsulation entities of some type.
 * The encapsulated entities have to mapped to a 2D coordinate.
 *
 * @param <O> the generic type of the encapsulated entities
 */
public class KdTree<O> {
	
	@Getter
	private Node<O> root = null;
	
	@Getter
	private List<Leaf<O>> leafs;
	
	/** The node capacity. */
	private int nodeCapacity;
	
	/** The metric. */
	private DistanceMetric metric;
	
	
	/**
	 * Instantiates a new kd tree built from the given list of {@link Leaf leaves}.
	 *
	 * @param nodes the leafs to be inserted in the tree
	 * @param nodeCapacity the capacity of inner nodes
	 * @param metric the metric to determine distance between leaves
	 */
	public KdTree(List<Leaf<O>> nodes, int nodeCapacity, DistanceMetric metric) {
		nodes.forEach(n -> n.setMetric(metric));
		this.leafs = new ArrayList<Leaf<O>>();
		this.leafs.addAll(nodes);
		this.nodeCapacity = nodeCapacity;
		this.root = buildTree(nodes);
		this.metric = metric;
		
	}
	
	/**
	 * Instantiates a new kd tree for the given points.
	 *
	 * @param points the points to be inserted as {@link Leaf leaves}
	 * @param nodeCapacity the capacity of inner nodes
	 * @param metric the metric to determine distance between leaves
	 */
	public KdTree(Collection<Point2D> points, int nodeCapacity, DistanceMetric metric) {
		this(points.stream()
				   .map(p -> new Leaf<O>(p))
				   .collect(toList()),
			nodeCapacity, metric);
	}
	
	/**
	 * Instantiates a new kd tree for the given entities.
	 * The given coordinate mapping is applied to the entities to determine their location.
	 *
	 * @param objects the objects to be inserted as {@link Leaf leaves} in the tree
	 * @param coordMap the coordinate mapping
	 * @param nodeCapacity the capacity of inner nodes
	 * @param metric the metric to determine distance between leaves
	 */
	public KdTree(Collection<O> objects, Function<O, Point2D> coordMap, int nodeCapacity, DistanceMetric metric) {
		this(objects.stream()
				   .map(o -> new Leaf<O>(o, coordMap))
				   .collect(toList()),
			nodeCapacity, metric);
	}
	
	
	
	/**
	 * Checks if the kD tree is empty.
	 *
	 * @return true, if the kD tree is empty
	 */
	public boolean isEmpty() {
		return this.root == null;
	}
	

	/**
	 * Builds the kD tree by recursively deviding the given list of {@link Leaf leaves} along the longest axis.
	 *
	 * @param leafs the leafs to be structured
	 * @return the root node of the (sub) tree
	 */
	private Node<O> buildTree(List<Leaf<O>> leafs) {
		
		if (leafs.size() == 0) {
			return null;
			
		} else if (leafs.size() == 1) {
			return leafs.get(0);
		}
		
		double minX = leafs.stream().map(Leaf::getX).min(Comparator.naturalOrder()).get();
		double maxX = leafs.stream().map(Leaf::getX).max(Comparator.naturalOrder()).get();
		double dx = maxX - minX;
		
		double minY = leafs.stream().map(Leaf::getY).min(Comparator.naturalOrder()).get();
		double maxY = leafs.stream().map(Leaf::getY).max(Comparator.naturalOrder()).get();
		double dy = maxY - minY;
		
		
		
		InnerNode<O> innerNode = new InnerNode<O>(new Rectangle2D.Double(minX, minY, dx, dy), metric);
		
		if (leafs.size() <= nodeCapacity) {
			leafs.forEach(l -> innerNode.addChild(l));
			return innerNode;
		}
		
		
		final Function<Leaf<O>, Double> valueMap = (dx >= dy) ? (Leaf::getX) : (Leaf::getY);
		leafs.sort(Comparator.comparing(valueMap));
		int n = (int) Math.floor(leafs.size() / 2);
		
		Leaf<O> median = leafs.get(n);
		double splitValue = valueMap.apply(median);
		
		
		//take n, drop n instead of filter?
		innerNode.addChild(
				buildTree(leafs.stream().filter(node -> valueMap.apply(node) < splitValue).collect(toList()))
		);
		
		innerNode.addChild(
				buildTree(leafs.stream().filter(node -> valueMap.apply(node) >= splitValue).collect(toList()))
		);
		
		return innerNode;
	}
	
	/**
	 * Prints the kD tree using the tree format of the root {@link Node}.
	 */
	public void print() {
		System.out.println(this.root.treeFormat(""));
	}

}
