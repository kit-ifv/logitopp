package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

/**
 * The Interface ParcelDestinationSelector.
 */
public interface ParcelDestinationSelector {

	/**
	 * Selects the {@link ParcelDestinationType} for a parcel.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param otherParcels the other {@link Parcel}s the recipient already ordered
	 * @param randomNumber a random number
	 * @return the selected {@link ParcelDestinationType}
	 */
	public ParcelDestinationType select(PickUpParcelPerson recipient, int numOfParcels, Collection<Parcel> otherParcels, double randomNumber);
	
}
