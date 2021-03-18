package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

/**
 * The Interface for ParcelOrderModels.
 * A model for creating parcel orders for simulated persons.
 */
public interface ParcelOrderModel {
	
	/**
	 * Creates the parcel orders for the given person.
	 *
	 * @param person the person
	 * @param results the results
	 * @return the collection
	 */
	public Collection<Parcel> createParcelOrders(PickUpParcelPerson person, DeliveryResults results);
	
}
