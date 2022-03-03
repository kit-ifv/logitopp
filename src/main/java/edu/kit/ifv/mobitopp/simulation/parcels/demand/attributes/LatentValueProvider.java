package edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes;

import java.util.Optional;
import java.util.function.Supplier;

public class LatentValueProvider<T> implements ValueProvider<T> {

	private Supplier<T> provider;
	private Optional<T> value;
	
	public LatentValueProvider(Supplier<T> provider) {
		this.provider = provider;
		this.value = Optional.empty();
	}
	
	@Override
	public T getValue() {
		if (this.value.isEmpty()) {
			this.value = Optional.of(this.provider.get());
		}
		
		return this.value.get();
	}

	@Override
	public boolean isDetermined() {
		return value.isPresent();
	}

}
