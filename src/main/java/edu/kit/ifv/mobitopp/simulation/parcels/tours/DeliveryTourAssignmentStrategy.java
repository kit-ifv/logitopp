package edu.kit.ifv.mobitopp.simulation.parcels.tours;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface DeliveryTourAssignmentStrategy provides a method for computing parcel deliveries tours.
 */
public interface DeliveryTourAssignmentStrategy {

	/**
	 * Compute parcels at the given distribution center to be delivered by the given delivery person.
	 *
	 * @param deliveries the deliveries
	 * @param person the delivery person
	 * @param currentTime the current time
	 * @param remainingWorkTime the remaining work time
	 * @return the collection of parcels to be delivered by the delivery person (subset of the given distribution centers parcels)
	 */
	public List<DeliveryActivityBuilder> assignParcels(Collection<DeliveryActivityBuilder> deliveries, DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime);
	
}
