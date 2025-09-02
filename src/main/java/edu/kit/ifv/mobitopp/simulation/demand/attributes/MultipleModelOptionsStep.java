package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public abstract class MultipleModelOptionsStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> extends CopyModelStep<A, P, T> implements ParcelDemandModelStep<A, P, T> {

	private final Collection<ParcelDemandModelStep<A, P, T>> choiceSet;
	
	public MultipleModelOptionsStep(Collection<ParcelDemandModelStep<A, P, T>> choiceSet) {
		this.choiceSet = choiceSet;
	}
	
	@SafeVarargs
	public MultipleModelOptionsStep(ParcelDemandModelStep<A, P, T> ... choiceSet) {
		this(Arrays.asList(choiceSet));
	}
		
	@Override
	public boolean determinePreSimulation(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		throw new UnsupportedOperationException("determinePreSimulation should not be called MultipleModelOptionsStep, since it is delegated in set");
	}
	
	@Override
	public T select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		throw new UnsupportedOperationException("select should not be called MultipleModelOptionsStep, since it is delegated in set");
	}
	
	@Override
	public void set(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber,
			BiConsumer<P, ValueProvider<T>> propertySetter) {

		Optional<Function<P, ValueProvider<T>>> copyGetter = isBundleCopyActive();
		if (!otherParcels.isEmpty() && copyGetter.isPresent()) {
			P firstBundleParcel = otherParcels.stream().findFirst().get();
			Supplier<T> supplier = () -> copyGetter.get().apply(firstBundleParcel).getValue();
			propertySetter.accept(parcel, new LatentValueProvider<T>(supplier));

		} else {
			Random rand = new Random((long) (Long.MAX_VALUE * randomNumber));
			this.selectModelStep(choiceSet, rand.nextDouble()).set(parcel, otherParcels, numOfParcels, randomNumber, propertySetter);
		}

	}
	
	protected abstract ParcelDemandModelStep<A, P, T> selectModelStep(Collection<ParcelDemandModelStep<A, P, T>> steps, double randomNumber);
	

}
