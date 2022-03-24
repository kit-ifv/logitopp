package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public abstract class MultipleModelOptionsStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> implements ParcelDemandModelStep<A, P, T> {

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
		Random rand = new Random((long) (Long.MAX_VALUE * randomNumber));
		
		this.selectModelStep(choiceSet, rand.nextDouble()).set(parcel, otherParcels, numOfParcels, randomNumber, propertySetter);
	}
	
	protected abstract ParcelDemandModelStep<A, P, T> selectModelStep(Collection<ParcelDemandModelStep<A, P, T>> steps, double randomNumber);
	

}
