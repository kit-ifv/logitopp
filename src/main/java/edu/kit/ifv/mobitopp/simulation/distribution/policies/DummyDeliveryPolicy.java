package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;


public class DummyDeliveryPolicy<P extends IParcel> implements ParcelDeliveryPolicy<P> {
	
	private RecipientType recipientType;
	
	public DummyDeliveryPolicy(RecipientType recipientType) {
		this.recipientType = recipientType;	
	}
	
	public DummyDeliveryPolicy() {
		this(null);
	}
	
	
	
	/**
	 * Returns the dummy recipient type as {@link Optional}.
	 * If it is null, returns empty {@link Optional}.
	 *
	 * @param parcel the parcel
	 * @return an optional {@link RecipientType}
	 */
	@Override
	public Optional<RecipientType> canDeliver(P parcel, Time currentTime) {
		if (parcel.isPickUp()) {
			return Optional.of(RecipientType.DISTRIBUTION_CENTER);
		}
		
		if (this.recipientType != null) {
			return Optional.of(recipientType);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * No parcel updates
	 *
	 * @param parcel the parcel
	 * @return false, as the parcel order is not updated
	 */
	@Override
	public boolean updateParcelDelivery(P parcel, Time currentTime) {
		return false;
	}
	
}
