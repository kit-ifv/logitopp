package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public interface ParcelDestinationSelector {

	public ParcelDestinationType select(PickUpParcelPerson recipient, int numOfParcels, Collection<Parcel> otherParcels, double randomNumber);
	
}
