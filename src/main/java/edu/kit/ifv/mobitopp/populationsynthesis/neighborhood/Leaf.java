package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.awt.geom.Point2D;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import lombok.Getter;

public class Leaf<O> implements Node<O> {
	
	@Getter
	private Point2D point = null;
	@Getter
	private O object = null;

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
		this(point);
		this.object = object;
	}
	
	
	@Override
	public float distance(Node<O> node) {
		return node.distanceTo(this);
	}
	
	@Override
	public float distanceTo(Leaf<O> leaf) {
		return (float) this.point.distance(leaf.point);
	}

	@Override
	public float distanceTo(InnerNode<O> innerNode) {
		if (innerNode.getRect().contains(point)) {
			return 0.0f;
		
		} else {
			
			double dx = max(innerNode.getRect().getMinX() - point.getX(), point.getX() - innerNode.getRect().getMaxX());
			dx = max(0, dx);
			double dy = max(innerNode.getRect().getMinY() - point.getY(), point.getY() - innerNode.getRect().getMaxY());
			dy = max(0, dy);
			
			return (float) sqrt(dx*dx + dy*dy);
			
		}
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
		return indent + this.point.toString() + "\n";
	}

	

}
