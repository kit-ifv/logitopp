package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public class CopyProviderModelStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> implements ParcelDemandModelStep<A, P, T> {

	private final Function<P, ValueProvider<? extends T>> getter;
	
	public CopyProviderModelStep(Function<P, ValueProvider<? extends T>> getter) {
		this.getter = getter;
	}
	
	@Override
	public T select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		throw new UnsupportedOperationException("select should not be called on a CopyProviderModelStep");
	}
	
	@Override
	public void set(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber,
			BiConsumer<P, ValueProvider<T>> propertySetter) {
		
		ValueProvider<? extends T> source = getter.apply(parcel);
		
		if (source.isDetermined()) {
			propertySetter.accept(parcel, new DeterminedValueProvider<>(source.getValue()));
			
		} else {
			propertySetter.accept(parcel, new LatentValueProvider<>(() -> (T) source.getValue()));
		}

		
	}

}
