package edu.kit.ifv.mobitopp.simulation.demand.attributes;

public class InstantValueProvider<T> implements ValueProvider<T> {
	
	private final T value;
	
	public InstantValueProvider(T value) {
		this.value = value;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public boolean isDetermined() {
		return true;
	}

}
