package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import lombok.Getter;
import lombok.Setter;

public class Leaf<O> implements Node<O> {
	
	@Getter
	private Point2D point = null;
	@Getter
	private O object = null;

	@Setter
	private DistanceMetric metric = new EuclidDistanceMetric();
	
	//Constructors for Leafs
	public Leaf(Point2D point) {
		this.point = point;
	}
	
	public Leaf(double x, double y) {
		this(new Point2D.Double(x, y));
	}
	
	public Leaf(O object, Function<O,Point2D> coordMap) {
		this(coordMap.apply(object));
		this.object = object;
	}
	
	public Leaf(O object, Point2D point) {
		this(object, o -> point);
	}
	
	@Override
	public Collection<Leaf<O>> getNestedLeaves() {
		return List.of(this);
	}
	
	
	@Override
	public float distance(Node<O> node) {
		return node.distanceTo(this);
	}
	
	@Override
	public float distanceTo(Leaf<O> leaf) {
		return metric.distance(this.getPoint(), leaf.getPoint());
	}

	@Override
	public float distanceTo(InnerNode<O> innerNode) {
		return metric.distance(this.getPoint(), innerNode.getRect());
	}
	
	@Override
	public Leaf<O> acceptQueue(PriorityQueue<Node<O>> queue, Leaf<O> target) {
		return this;
	}
	
	public double getX() {
		return this.point.getX();
	}
	
	public double getY() {
		return this.point.getY();
	}
	
			
	public String treeFormat(String indent) {
		return indent + String.valueOf(this.object) + " [" + this.point.getX() + "," + this.point.getY() + "]" + "\n";
	}	

}
