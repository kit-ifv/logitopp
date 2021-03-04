package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;

public class DummyDeliveryEfficiencyModel implements DeliveryEfficiencyModel {

	@Override
	public DeliveryEfficiencyProfile select(DistributionCenter center, Person person) {
		return new DeliveryEfficiencyProfile(selectLoadingDuration(center, person),
												selectUnloadingDuration(center, person), 
												selectTripDuration(center, person),
												selectDeliveryDurationAdd(center, person),
												selectDeliveryDurationMul(center, person)
											);
	}

	@Override
	public int selectLoadingDuration(DistributionCenter center, Person person) {
		return 20;
	}

	@Override
	public int selectTripDuration(DistributionCenter center, Person person) {
		return 15;
	}

	@Override
	public float selectDeliveryDurationAdd(DistributionCenter center, Person person) {
		return 1.0f;
	}
	
	@Override
	public float selectDeliveryDurationMul(DistributionCenter center, Person person) {
		return 0.5f;
	}

	@Override
	public int selectUnloadingDuration(DistributionCenter center, Person person) {
		return 10;
	}

}
