package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;

/**
 * The Class DummyDeliveryTourStrategy is an exemplary implementation of the DeliveryTourAssignmentStrategy interface.
 */
public class DummyDeliveryTourStrategy implements DeliveryTourAssignmentStrategy {

	/**
	 * Assign parcels to the given delivery person based on the duration of the working activity and the delivery duration per parcel.
	 *
	 * @param dc the dc
	 * @param person the person
	 * @param work the work
	 * @return the collection
	 */
	@Override
	public List<Parcel> assignParcels(DistributionCenter dc, DeliveryPerson person,  ActivityIfc work) {
		DeliveryEfficiencyProfile efficiency = person.getEfficiency();
		
		List<Parcel> toBeDelivered = dc.getAvailableParcels(work.startDate());
		
		
		int maxNumOfParcels = (int) Math.floorDiv(work.duration() - efficiency.getLoadDuration() - efficiency.getUnloadDuration(), (int) (efficiency.getTripDuration()+efficiency.getDeliveryDurBase()) );
		maxNumOfParcels = Math.max(maxNumOfParcels, 1);
		
		List<Parcel> assigned = toBeDelivered.stream().limit(maxNumOfParcels).collect(Collectors.toList());
				
		return assigned;
		
	}
	
}
