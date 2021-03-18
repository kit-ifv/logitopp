package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class ShareBasedDeliveryServiceSelector.
 */
public class ShareBasedDeliveryServiceSelector extends ShareBasedSelector<String> implements DeliveryServiceSelector {

	/**
	 * Instantiates a new share based delivery service selector.
	 *
	 * @param shares the shares
	 */
	public ShareBasedDeliveryServiceSelector(Map<String, Double> shares) {
		super(shares);
	}
	
	
	/**
	 * Instantiates a new share based delivery service selector.
	 *
	 * @param values the values
	 */
	public ShareBasedDeliveryServiceSelector(List<String> values) {
		super(values);
	}


	/**
	 * Select.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the num of parcels
	 * @param destination the destination
	 * @param arrivalDate the arrival date
	 * @param otherParcels the other parcels
	 * @param randomNumber the random number
	 * @return the string
	 */
	@Override
	public String select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Time arrivalDate, Collection<Parcel> otherParcels, double randomNumber) {

		return this.select(randomNumber);
	}

}
