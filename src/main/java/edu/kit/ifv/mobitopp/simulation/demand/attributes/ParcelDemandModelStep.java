package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public interface ParcelDemandModelStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> {

	public T select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber);
	
	default public boolean determinePreSimulation(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		return true;
	}
	
	default void set(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber, BiConsumer<P, ValueProvider<T>> propertySetter) {
		
		if (determinePreSimulation(parcel, otherParcels, numOfParcels, randomNumber)) {
			T result = select(parcel, otherParcels, numOfParcels, randomNumber);
			propertySetter.accept(parcel, new DeterminedValueProvider<T>(result));
			
		} else {
			Supplier<T> provider = () -> select(parcel, otherParcels, numOfParcels, randomNumber);
			propertySetter.accept(parcel, new LatentValueProvider<T>(provider));
			
		}

	}

	
}
