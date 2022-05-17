package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

/**
 * The Interface PackStationModel provides a method for assigning pack station locations to persons.
 */
public interface PackStationModel {

	/**
	 * Selects the {@link Zone} of a pack station for the given {@link PersonBuilder person}.
	 *
	 * @param p the person
	 * @return the zone of a pack station
	 */
	public Zone select(PersonBuilder p);
	
}
