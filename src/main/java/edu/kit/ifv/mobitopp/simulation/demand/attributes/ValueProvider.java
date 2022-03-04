package edu.kit.ifv.mobitopp.simulation.demand.attributes;

public interface ValueProvider<T> {
	
	public T getValue();
		
	public boolean isDetermined();

}
