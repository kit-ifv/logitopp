package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public interface DistributionCenterSelector {

	public DistributionCenter select(PickUpParcelPerson recipient, int numOfParcels,
		ParcelDestinationType destination, Time arrivalDate, String deliveryService,
		Collection<Parcel> otherParcels, double randomNumber);

}
