package edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.model.ParcelBuilder;

public class CopyProviderModelStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> implements ParcelDemandModelStep<A, P, T> {

	private final Function<P, ValueProvider<? extends T>> getter;
	
	public CopyProviderModelStep(Function<P, ValueProvider<? extends T>> getter) {
		this.getter = getter;
	}
	
	@Override
	public T select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		return null;
	}
	
	@Override
	public void set(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber,
			BiConsumer<P, ValueProvider<T>> propertySetter) {

		propertySetter.accept(parcel, new LatentValueProvider<>(() -> (T) getter.apply(parcel).getValue()) );
	}

}
