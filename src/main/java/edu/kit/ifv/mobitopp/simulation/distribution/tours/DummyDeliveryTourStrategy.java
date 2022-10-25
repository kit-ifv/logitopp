package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivityBuilder;
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
	public List<ParcelActivityBuilder> assignParcels(Collection<ParcelActivityBuilder> deliveries,
			DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime) {
		
		List<ParcelActivityBuilder> assigned = new ArrayList<>();
		RelativeTime counter = RelativeTime.ofSeconds(remainingWorkTime.seconds());
		
		for (ParcelActivityBuilder delivery : deliveries) {
			counter = counter.minusMinutes(delivery.estimateDuration() + 5);
			
			if (!counter.isNegative()) {
				assigned.add(delivery);
			} else {
				break;
			}
		}
		
		return assigned;
		
	}

	@Override
	public Mode getMode() {
		return StandardMode.TRUCK;
	}

	
}
