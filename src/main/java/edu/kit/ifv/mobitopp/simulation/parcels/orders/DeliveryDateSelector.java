package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public interface DeliveryDateSelector {

	public Time select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Collection<Parcel> otherParcels, double randomNumber);
	
}
