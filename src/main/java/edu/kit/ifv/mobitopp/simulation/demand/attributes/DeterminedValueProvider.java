package edu.kit.ifv.mobitopp.simulation.demand.attributes;

public class DeterminedValueProvider<T> implements ValueProvider<T> {
	
	private final T value;
	
	public DeterminedValueProvider(T value) {
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
