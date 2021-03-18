package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;

/**
 * The Interface DeliveryTourAssignmentStrategy provides a method for computing parcel deliveries tours.
 */
public interface DeliveryTourAssignmentStrategy {

	/**
	 * Compute parcels at the given distribution center to be delivered by the given delivery person.
	 *
	 * @param distributionCenter the distribution center
	 * @param person the delivery person
	 * @param work the work activity
	 * @return the collection of parcels to be delivered by the delivery person (subset of the given distribution centers parcels)
	 */
	public List<Parcel> assignParcels(DistributionCenter distributionCenter, DeliveryPerson person,  ActivityIfc work);
	
}
