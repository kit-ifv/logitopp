package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;

public interface DeliveryDurationModel {

	public float estimateDuration(DeliveryPerson person, Collection<IParcel> parcels);
	
}
