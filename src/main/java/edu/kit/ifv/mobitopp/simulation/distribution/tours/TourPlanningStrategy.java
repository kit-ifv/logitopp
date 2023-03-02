package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
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
	 * @param parcels the parcels
	 * @param center  the center
	 * @return a list of planned tours for the given parcels operated by the given {@link DistributionCenter}
	 */
	public List<PlannedDeliveryTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet);
	
	public boolean shouldReplanTours(DistributionCenter center, Time time);

}
