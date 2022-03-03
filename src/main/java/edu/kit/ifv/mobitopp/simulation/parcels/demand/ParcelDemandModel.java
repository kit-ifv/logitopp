package edu.kit.ifv.mobitopp.simulation.parcels.demand;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.model.ParcelBuilder;

public interface ParcelDemandModel<A extends ParcelAgent, P extends ParcelBuilder<A>> {

	public Collection<P> createParcelDemand(A parcelAgent);

}
