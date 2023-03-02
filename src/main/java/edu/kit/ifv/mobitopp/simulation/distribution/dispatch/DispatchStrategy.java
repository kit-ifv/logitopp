package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.time.Time;

public interface DispatchStrategy {

	public Optional<PlannedDeliveryTour> canDispatch(Collection<PlannedDeliveryTour> tours, Fleet fleet, Time time);
	
	//TODO estimate dispatch times?
	
}
