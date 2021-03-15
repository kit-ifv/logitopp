package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeliveryTourAssignmentStrategy provides a method for assigning parcel deliveries to delivery persons.
 */
public interface DeliveryTourAssignmentStrategy {

	/**
	 * Assign parcels to the given delivery person with the given work activity and efficiency profile.
	 *
	 * @param distributionCenter the distribution center
	 * @param person the delivery person
	 * @param work the work activity
	 * @return the collection of parcels to be delivered by the delivery person (subset of the given parcels)
	 */
	public List<Parcel> assignParcels(DistributionCenter distributionCenter, DeliveryPerson person,  ActivityIfc work);
	
}
