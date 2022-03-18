package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public class LatentModelStepWarpper<A extends ParcelAgent, P extends ParcelBuilder<A>, T> implements ParcelDemandModelStep<A, P, T>{

	private final ParcelDemandModelStep<A, P, T> step;
	
	public LatentModelStepWarpper(ParcelDemandModelStep<A, P, T> step) {
		this.step = step;
	}
	
	@Override
	public boolean determinePreSimulation(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		return false;
	}
	
	@Override
	public T select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		return step.select(parcel, otherParcels, numOfParcels, randomNumber);
	}

}
