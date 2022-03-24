package edu.kit.ifv.mobitopp.simulation.demand;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public interface ParcelDemandModel<A extends ParcelAgent, P extends ParcelBuilder<A>> {

	public Collection<P> createParcelDemand(A parcelAgent);
	
	public void printStatistics(String label);

}
