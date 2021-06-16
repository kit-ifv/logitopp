package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public interface ParcelOrderStep<T> {

	public T select(ParcelBuilder parcel, Collection<ParcelBuilder> otherParcels, int numOfParcels, double randomNumber);
	
	default void set(ParcelBuilder parcel, Collection<ParcelBuilder> otherParcels, int numOfParcels, double randomNumber, BiConsumer<ParcelBuilder, T> propertySetter) {
		T result = select(parcel, otherParcels, numOfParcels, randomNumber);
		propertySetter.accept(parcel, result);
	}
}
