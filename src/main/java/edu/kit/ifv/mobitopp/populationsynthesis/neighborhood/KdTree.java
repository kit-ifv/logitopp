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

public class KdTree<O> {
	
	@Getter
	private Node<O> root = null;
	@Getter
	private List<Leaf<O>> leafs;
	
	private int nodeCapacity;
	private DistanceMetric metric;
	
	
	public KdTree(List<Leaf<O>> nodes, int nodeCapacity, DistanceMetric metric) {
		nodes.forEach(n -> n.setMetric(metric));
		this.leafs = new ArrayList<Leaf<O>>();
		this.leafs.addAll(nodes);
		this.nodeCapacity = nodeCapacity;
		this.root = buildTree(nodes);
		this.metric = metric;
		
	}
	
	public KdTree(Collection<Point2D> points, int nodeCapacity, DistanceMetric metric) {
		this(points.stream()
				   .map(p -> new Leaf<O>(p))
				   .collect(toList()),
			nodeCapacity, metric);
	}
	
	public KdTree(Collection<O> objects, Function<O, Point2D> coordMap, int nodeCapacity, DistanceMetric metric) {
		this(objects.stream()
				   .map(o -> new Leaf<O>(o, coordMap))
				   .collect(toList()),
			nodeCapacity, metric);
	}
	
	
	
	public boolean isEmpty() {
		return this.root == null;
	}
	

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
		
		
		
		innerNode.addChild(
				buildTree(leafs.stream().filter(node -> valueMap.apply(node) < splitValue).collect(toList()))
		);
		
		innerNode.addChild(
				buildTree(leafs.stream().filter(node -> valueMap.apply(node) >= splitValue).collect(toList()))
		);
		
		return innerNode;
	}
	
	public void print() {
		System.out.println(this.root.treeFormat(""));
	}

}
