package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface DeliveryTourAssignmentStrategy provides a method for computing parcel deliveries tours.
 */
public interface DeliveryTourAssignmentStrategy {

	/**
	 * Compute delivery tours for the given delivery/pickup activities.
	 *
	 * @param deliveries the delivery/pickup activities
	 * @param vehicle the delivery vehicle
	 * @param currentTime the current time
	 * @param maxTourDuration the maximum tour duration
	 * @return the collection of parcels to be delivered by the delivery person (subset of the given distribution centers parcels)
	 */
	public List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> deliveries, DeliveryVehicle vehicle, Time currentTime, RelativeTime maxTourDuration);
	
}
