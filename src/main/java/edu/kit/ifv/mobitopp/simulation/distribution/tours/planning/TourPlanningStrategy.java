package edu.kit.ifv.mobitopp.simulation.distribution.tours.planning;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface DeliveryTourAssignmentStrategy provides a method for computing
 * parcel deliveries tours.
 */
public interface TourPlanningStrategy {

	/**
	 * Compute delivery tours for the given delivery/pickup activities.
	 *
	 * @param deliveries the deliveries
	 * @param pickUps    the pick ups
	 * @param fleet      the fleet
	 * @param time       the time
	 * @return a list of planned tours for the given parcels operated by the given
	 *         {@link DistributionCenter}
	 */
	public List<PlannedTour> planTours(
			Collection<IParcel> deliveries, 
			Collection<IParcel> pickUps, Fleet fleet,
			Time time
	);

	public boolean shouldReplanTours(DistributionCenter center, Time time);

}
