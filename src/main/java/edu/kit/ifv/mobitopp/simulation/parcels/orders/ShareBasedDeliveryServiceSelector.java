package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class ShareBasedDeliveryServiceSelector extends ShareBasedSelector<String> implements DeliveryServiceSelector {

	public ShareBasedDeliveryServiceSelector(Map<String, Double> shares) {
		super(shares);
	}
	
	
	public ShareBasedDeliveryServiceSelector(List<String> values) {
		super(values);
	}


	@Override
	public String select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Time arrivalDate, Collection<Parcel> otherParcels, double randomNumber) {

		return this.select(randomNumber);
	}

}
