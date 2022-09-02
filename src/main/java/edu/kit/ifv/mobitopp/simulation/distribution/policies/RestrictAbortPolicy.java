package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import java.util.Optional;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class RestrictAbortPolicy implements ParcelDeliveryPolicy<PrivateParcel> {

	private final ParcelDeliveryPolicy<PrivateParcel> policy;
	private final Predicate<PrivateParcel> restriction;
	
	public RestrictAbortPolicy(ParcelDeliveryPolicy<PrivateParcel> policy, Predicate<PrivateParcel> restriction) {
		this.policy = policy;
		this.restriction = restriction;
	}

	@Override
	public Optional<RecipientType> canDeliver(PrivateParcel parcel, Time currentTime) {
		return policy.canDeliver(parcel, currentTime);
	}


	@Override
	public boolean updateParcelDelivery(PrivateParcel parcel, Time currentTime) {
		if (restriction.test(parcel)) {
			return policy.updateParcelDelivery(parcel, currentTime);
		}
		
		return false;
	}


}
