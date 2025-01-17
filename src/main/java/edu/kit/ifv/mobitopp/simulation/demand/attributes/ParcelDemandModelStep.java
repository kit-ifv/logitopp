package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

import javax.swing.text.html.Option;

public interface ParcelDemandModelStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> {

	public T select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber);
	
	default public boolean determinePreSimulation(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		return true;
	}
	
	default void set(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber, BiConsumer<P, ValueProvider<T>> propertySetter) {

		Optional<Function<P, ValueProvider<T>>> copyGetter = isBundleCopyActive();
		if (!otherParcels.isEmpty() && copyGetter.isPresent()) {
			P firstBundleParcel = otherParcels.stream().findFirst().get();
			Supplier<T> provider = () -> copyGetter.get().apply(firstBundleParcel).getValue();
			propertySetter.accept(parcel, new LatentValueProvider<T>(provider));

		} else if (determinePreSimulation(parcel, otherParcels, numOfParcels, randomNumber)) {
			T result = select(parcel, otherParcels, numOfParcels, randomNumber);
			propertySetter.accept(parcel, new InstantValueProvider<T>(result));
			
		} else {
			Supplier<T> provider = () -> select(parcel, otherParcels, numOfParcels, randomNumber);
			propertySetter.accept(parcel, new LatentValueProvider<T>(provider));
		}

	}

	default Optional<Function<P, ValueProvider<T>>> isBundleCopyActive() {
		return Optional.empty();
	}

	default void setBundleCopy(Function<P, ValueProvider<T>> copyGetter) {
		throw new UnsupportedOperationException(
				this.getClass().getSimpleName() + "does not support 'copy first bundle parcel'!"
		);
	}
	
}
