package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DummyDeliveryTourStrategy is an exemplary implementation of the DeliveryTourAssignmentStrategy interface.
 */
public class DummyDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	/**
	 * Assign parcels to the given delivery person based on the duration of the working activity and the delivery duration per parcel.
	 *
	 * @param deliveries the deliveries
	 * @param person the delivery person
	 * @param remainingWorkTime the remaining work time
	 * @return the collection of assigned deliveries
	 */
	@Override
	public List<DeliveryActivityBuilder> assignParcels(Collection<DeliveryActivityBuilder> deliveries,
			DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime) {
		
		DeliveryEfficiencyProfile efficiency = person.getEfficiency();
		List<DeliveryActivityBuilder> assigned = new ArrayList<>();
		RelativeTime counter = RelativeTime.ofSeconds(remainingWorkTime.seconds());
		
		for (DeliveryActivityBuilder delivery : deliveries) {
			counter = counter.minusMinutes(delivery.estimateDuration(efficiency) + efficiency.getTripDuration());
			
			if (!counter.isNegative()) {
				assigned.add(delivery);
			} else {
				break;
			}
		}
		
		return assigned;
		
	}

	
}
