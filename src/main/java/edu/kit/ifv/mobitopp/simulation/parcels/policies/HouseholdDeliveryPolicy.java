package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.populationsynthesis.neighborhood.NeighborhoodRelationship;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;


/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class HouseholdDeliveryPolicy implements ParcelDeliveryPolicy {

	private final ParcelDeliveryPolicy policy;
	
	public HouseholdDeliveryPolicy(ParcelDeliveryPolicy policy) {
		this.policy = policy;
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
	public Optional<RecipientType> canDeliver(Parcel parcel) {

		Optional<RecipientType> canDeliver = policy.canDeliver(parcel);
		
		if (canDeliver.isEmpty() && parcel.getDestinationType().equals(HOME)) {
			
			boolean anybodyHome = parcel.getPerson().household().persons().anyMatch(p -> p.currentActivity().activityType().equals(ActivityType.HOME));
			
			return optionalRecipient(anybodyHome, RecipientType.HOUSEHOLDMEMBER);
		}
 
		
		return canDeliver;
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
	
	private Optional<RecipientType> optionalRecipient(boolean check, RecipientType recipientType) {
		if (check) {
			return Optional.of(recipientType);
		} else {
			return Optional.empty();
		}
	}

}
