package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;

public interface DeliveryEfficiencyModel {

	public DeliveryEfficiencyProfile select(DistributionCenter center, Person person);
	
	public int selectLoadingDuration(DistributionCenter center, Person person);
	
	public int selectTripDuration(DistributionCenter center, Person person);
	
	public float selectDeliveryDurationAdd(DistributionCenter center, Person person);
	
	public float selectDeliveryDurationMul(DistributionCenter center, Person person);
		
	public int selectUnloadingDuration(DistributionCenter center, Person person);
}
