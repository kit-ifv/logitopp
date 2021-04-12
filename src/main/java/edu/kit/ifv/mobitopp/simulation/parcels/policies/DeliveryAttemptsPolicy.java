package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;

public class DeliveryAttemptsPolicy implements ParcelDeliveryPolicy {

	private final ParcelDeliveryPolicy policy;
	private final int maxAttempts;
	
	public DeliveryAttemptsPolicy(ParcelDeliveryPolicy policy, int maxAttempts) {
		this.policy = policy;
		this.maxAttempts = maxAttempts;
	}
	
	@Override
	public boolean canDeliver(Parcel parcel) {
		return policy.canDeliver(parcel);
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
	public boolean updateParcelDelivery(Parcel parcel) {
		
		if (parcel.getDeliveryAttempts() >= this.maxAttempts -1) {
			parcel.setDestinationType(PACK_STATION);
			return true;
		}
		
		return false;
	}

}
