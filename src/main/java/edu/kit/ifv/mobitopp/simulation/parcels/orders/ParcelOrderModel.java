package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public interface ParcelOrderModel {
	
	public Collection<Parcel> createParcelOrdes(PickUpParcelPerson person, DeliveryResults results);
	
}
