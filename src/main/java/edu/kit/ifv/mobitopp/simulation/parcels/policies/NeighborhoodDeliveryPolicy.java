package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;

import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.neighborhood.NeighborhoodRelationship;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;


/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class NeighborhoodDeliveryPolicy implements ParcelDeliveryPolicy {

	private final ParcelDeliveryPolicy policy;
	private final NeighborhoodRelationship neighborhood;
	
	public NeighborhoodDeliveryPolicy(ParcelDeliveryPolicy policy, NeighborhoodRelationship neighborhood) {
		this.policy = policy;
		this.neighborhood = neighborhood;
	}
	
	/**
	 * Checks whether the given parcel can be delivered.
	 * Home delivery: check if the recipient, another household member or a neighbor is at home.
	 * (If not uses the wrapped policy, to check whether the parcel can be delivered anyway).
	 * For other delivery types uses wrapped policy.
	 * 
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel can be delivered
	 */
	@Override
	public boolean canDeliver(Parcel parcel) {

		if (parcel.getDestinationType().equals(HOME)) {
			Collection<Household> neighbors = neighborhood.getNeighborsOf(parcel.getPerson().household());
			
			System.out.println("Found " + neighbors.size() + " neighbors. incl? " + (neighbors.contains(parcel.getPerson().household())));
			
			boolean anybodyHome = neighbors
									.stream()
									.flatMap(Household::persons)
									.anyMatch(p -> p.currentActivity().activityType().equals(ActivityType.HOME));
			if (anybodyHome) {
				return true;
			}
		} 
		
		return policy.canDeliver(parcel);
	}

	
	/**
	 * Update the parcel delivery.
	 * Uses the wrapped policy.
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel order was updated
	 */
	@Override
	public boolean updateParcelDelivery(Parcel parcel) {
		return policy.updateParcelDelivery(parcel);
	}

}
