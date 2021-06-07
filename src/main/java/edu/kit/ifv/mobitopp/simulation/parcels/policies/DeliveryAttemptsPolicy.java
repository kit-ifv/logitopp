package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.time.Time;


public class DeliveryAttemptsPolicy implements ParcelDeliveryPolicy<PrivateParcel> {

	private final ParcelDeliveryPolicy<PrivateParcel> policy;
	private final int maxAttempts;
	
	public DeliveryAttemptsPolicy(ParcelDeliveryPolicy<PrivateParcel> policy, int maxAttempts) {
		this.policy = policy;
		this.maxAttempts = maxAttempts;
	}
	
	@Override
	public Optional<RecipientType> canDeliver(PrivateParcel parcel, Time currentTime) {
		return policy.canDeliver(parcel, currentTime);
	}

	/**
	 * Update the parcel delivery.
	 * During the n-th attempt: updates the parcel to send it to a pack station.
	 * (n = maxAttempts)
	 * 
	 * @param parcel the parcel
	 * @return true, if the parcel order was updated
	 */
	@Override
	public boolean updateParcelDelivery(PrivateParcel parcel, Time currentTime) {
		
		if (parcel.getDeliveryAttempts() >= this.maxAttempts -1) {
			parcel.setDestinationType(PACK_STATION);
			return true;
		}
		
		return false;
	}

}
