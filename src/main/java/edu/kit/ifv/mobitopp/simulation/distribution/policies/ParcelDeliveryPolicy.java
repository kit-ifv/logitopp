package edu.kit.ifv.mobitopp.simulation.distribution.policies;


import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;


/**
 * The Interface ParcelDeliveryPolicy provides methods deciding whether parcels can be delivered or should be updated.
 */
public interface ParcelDeliveryPolicy<P extends IParcel> {

	/**
	 * Checks whether the parcel can be delivered.
	 * E.g. may check if the recipient or a neighbor is at home.
	 *
	 * @param parcel the parcel
	 * @param currentTime the current time
	 * @return an optional {@link RecipientType} if the parcel can be delivered, an empty {@link Optional} otherwise
	 */
	public Optional<RecipientType> canDeliver(P parcel, Time currentTime);

	
	/**
	 * Update the parcel delivery.
	 * E.g. if the delivery was attempted 3 times, the delivery can be sent to a pack station.
	 *
	 * @param parcel the parcel
	 * @param currentTime the current time
	 * @return true, if successful
	 */
	public boolean updateParcelDelivery(P parcel, Time currentTime);
	
}
