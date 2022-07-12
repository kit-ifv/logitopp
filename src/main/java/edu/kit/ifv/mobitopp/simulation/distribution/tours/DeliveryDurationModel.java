package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;

public interface DeliveryDurationModel {

	public float estimateDuration(DeliveryAgent agent, Collection<IParcel> parcels);
	
}
