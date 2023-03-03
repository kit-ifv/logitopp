package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DummyDeliveryTourStrategy is an exemplary implementation of the DeliveryTourAssignmentStrategy interface.
 */
public class DummyDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	private final ImpedanceIfc impedance;
	
	public DummyDeliveryTourStrategy(ImpedanceIfc impedance) {
		this.impedance = impedance;
	}
	
	
	@Override
	public List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle,
			Time currentTime, RelativeTime maxTourDuration) {
		
		List<PlannedDeliveryTour> tours = new ArrayList<>();
		
		List<ParcelActivityBuilder> assigned = new ArrayList<>();
		RelativeTime counter = copy(maxTourDuration);
		
		for (ParcelActivityBuilder activity : activities) {
			counter = counter.minusMinutes(activity.estimateDuration() + 5);
			
			if (counter.isNegative())  {
				tours.add(new PlannedDeliveryTour(vehicle.getType(), assigned, maxTourDuration.minus(counter), currentTime, impedance));
				assigned = new ArrayList<>();
				counter = copy(maxTourDuration);
				
			} else {
				assigned.add(activity);
			}

		}
		
		if (!assigned.isEmpty()) {
			tours.add(new PlannedDeliveryTour(vehicle.getType(), assigned, maxTourDuration.minus(counter), currentTime, impedance));
		}
		

		return tours;
	}

	private RelativeTime copy(RelativeTime maxTourDuration) {
		return RelativeTime.ofSeconds(maxTourDuration.seconds());
	}
	
}
