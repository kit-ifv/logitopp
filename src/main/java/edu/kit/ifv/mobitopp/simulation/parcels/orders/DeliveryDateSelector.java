package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface for DeliveryDateSelectors.
 * A model to select the planned delivery date of a parcel.
 */
public interface DeliveryDateSelector {

	/**
	 * Selects a planned arrival date for a parcel.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param destination the parcel's {@link ParcelDestinationType}
	 * @param otherParcels the other {@link PrivateParcel}s the recipient already ordered
	 * @param randomNumber a random number
	 * @return the planned arrival date
	 */
	public Time select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Collection<PrivateParcel> otherParcels, double randomNumber);
	
}
