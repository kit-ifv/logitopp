package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.util.Collection;

import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;

public interface Node<O> {

	public float distance(Node<O> node);	
	
	public float distanceTo(Leaf<O> leaf);
	
	public float distanceTo(InnerNode<O> innerNode);
	
	public Collection<Leaf<O>> getNestedLeaves();
	
	public Leaf<O> acceptQueue(PriorityQueue<Node<O>> queue, Leaf<O> target);
	
	public String treeFormat(String indent);
}
