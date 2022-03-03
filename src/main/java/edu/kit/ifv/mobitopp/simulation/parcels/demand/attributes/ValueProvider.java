package edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes;

import java.util.function.Supplier;

public interface ValueProvider<T> {
	
	public T getValue();
	
	default public Supplier<T> asSupplier() {
		return () -> this.getValue();
	}
	
	public boolean isDetermined();

}
