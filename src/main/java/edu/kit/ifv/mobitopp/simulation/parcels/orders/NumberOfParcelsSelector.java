package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

/**
 * The Interface for NumberOfParcelsSelectors.
 * A model for selecting the number of parcels, a person will order during the simulation.
 */
public interface NumberOfParcelsSelector {

	/**
	 * Selects the number of parcels, the given {@link Person} orders.
	 *
	 * @param person the person
	 * @param randomNumber a random number
	 * @return the number of parcels
	 */
	public int select(PickUpParcelPerson person, double randomNumber);
	
}
