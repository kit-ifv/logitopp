package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import lombok.Getter;

public class InnerNode<O> implements Node<O> {

	@Getter
	private Rectangle2D rect = null;
	
	@Getter
	private List<Node<O>> children = new ArrayList<Node<O>>();
	
	
	//Constructor for inner Nodes
	public InnerNode(Rectangle2D area) {
		this.rect = area;
	}
	
	
	@Override
	public float distance(Node<O> node) {
		return node.distanceTo(this);
	}

	public void addChild(Node<O> node) {
		this.children.add(node);
	}
	
	public void addChildren(Collection<Node<O>> nodes) {
		this.children.addAll(nodes);
	}
	
	
	@Override
	public float distanceTo(Leaf<O> leaf) {
		
		if (this.rect.contains(leaf.getPoint())) {
			return 0.0f;
		
		} else {

			double dx = max(rect.getMinX() - leaf.getPoint().getX(), leaf.getPoint().getX() - rect.getMaxX());
			dx = max(0, dx);
			double dy = max(rect.getMinY() - leaf.getPoint().getY(), leaf.getPoint().getY() - rect.getMaxY());
			dy = max(0, dy);
			
			return (float) sqrt(dx*dx + dy*dy);
			
		}
	}

	@Override
	public float distanceTo(InnerNode<O> innerNode) {
		throw new RuntimeException("is this supported or used?");
//		if (this.rect.contains(innerNode.getRect())) {
//			return 0.0f;
//		}
		
		
		
		//return 0.0f;
	}
		

	@Override
	public Leaf<O> acceptQueue(PriorityQueue<Node<O>> queue, Leaf<O> target) {
		
		for (Node<O> child: this.children) {
			queue.add(child, child.distance(target));
		}
		
		
		return null;
	}
		
	
	public String treeFormat(String indent) {
		String str = indent + "Box " + this.rect.toString() + ":" + ": \n";
		
		for (Node<O> child : this.children) {
			str += child.treeFormat(indent + "  ");
		}
		
		return str;
	}




}
