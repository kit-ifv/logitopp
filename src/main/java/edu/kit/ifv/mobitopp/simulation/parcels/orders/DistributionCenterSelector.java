package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface for DistributionCenterSelectors.
 * A model for selecting distribution centers for parcels.
 */
public interface DistributionCenterSelector {

	/**
	 * Selects a distribution center from where a parcel will be delivered.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param destination the {@link ParcelDestinationType}
	 * @param arrivalDate the planned arrival date
	 * @param deliveryService a delivery service tag
	 * @param otherParcels the other {@link Parcel}s the recipient already ordered
	 * @param randomNumber a random number
	 * @return the selected distribution center
	 */
	public DistributionCenter select(PickUpParcelPerson recipient, int numOfParcels,
		ParcelDestinationType destination, Time arrivalDate, String deliveryService,
		Collection<Parcel> otherParcels, double randomNumber);

}
