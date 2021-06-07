package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface  for DeliveryServiceSelectors.
 * A model for selecting a delivery service for parcels.
 */
public interface DeliveryServiceSelector {

	/**
	 * Selects a delivery service for a parcel order.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param destination the parcel's {@link ParcelDestinationType}
	 * @param arrivalDate the planned arrival date
	 * @param otherParcels the other {@link PrivateParcel}s the recipient already ordered
	 * @param randomNumber a random number
	 * @return the delivery service tag
	 */
	public String select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Time arrivalDate, Collection<PrivateParcel> otherParcels, double randomNumber);
	
}
