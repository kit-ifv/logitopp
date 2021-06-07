package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * ShareBasedDeliveryServiceSelector is a {@link DeliveryServiceSelector} extending {@link ShareBasedSelector}.
 */
public class ShareBasedDeliveryServiceSelector extends ShareBasedSelector<String> implements DeliveryServiceSelector {

	/**
	 * Instantiates a new {@link ShareBasedSelector}
	 * with the given shares.
	 *
	 * @param shares the shares of delivery service tags
	 */
	public ShareBasedDeliveryServiceSelector(Map<String, Double> shares) {
		super(shares);
	}
	
	
	/**
	 * Instantiates a new {@link ShareBasedDeliveryServiceSelector}
	 * with the given delivery service tags and equal shares for all items.
	 *
	 * @param values the delivery service tags
	 */
	public ShareBasedDeliveryServiceSelector(List<String> values) {
		super(values);
	}


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
	@Override
	public String select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Time arrivalDate, Collection<PrivateParcel> otherParcels, double randomNumber) {

		return this.select(randomNumber);
	}

}
