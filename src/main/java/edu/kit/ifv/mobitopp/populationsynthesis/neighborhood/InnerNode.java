package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

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
	
	private DistanceMetric metric;
	
	//Constructor for inner Nodes
	public InnerNode(Rectangle2D area, DistanceMetric metric) {
		this.rect = area;
		this.metric = metric;
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
	
	public Collection<Leaf<O>> getNestedLeaves() {
		List<Leaf<O>> leaves = new ArrayList<>();
		
		for (Node<O> child : getChildren()) {
			leaves.addAll(child.getNestedLeaves());			
		}
		
		return leaves;
	}
	
	
	@Override
	public float distanceTo(Leaf<O> leaf) {
		return metric.distance(this.getRect(), leaf.getPoint());
	}

	@Override
	public float distanceTo(InnerNode<O> innerNode) {
		return metric.distance(this.getRect(), innerNode.getRect());
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
