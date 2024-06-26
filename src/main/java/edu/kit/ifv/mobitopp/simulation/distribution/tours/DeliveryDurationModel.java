package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public interface DeliveryDurationModel {

	public float estimateDuration(Collection<IParcel> parcels);
	
}
