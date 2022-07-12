package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;

public class DummyDurationModel implements DeliveryDurationModel {

	@Override
	public float estimateDuration(DeliveryAgent agent, Collection<IParcel> parcels) {
		return 1.0f + 0.5f*parcels.size();
	}

}
