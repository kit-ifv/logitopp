package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

/**
 * The Interface PackStationModel provides a method for assigning pack station locations to persons.
 */
public interface PackStationModel {

	/**
	 * Selects the {@link Zone} of a pack station for the given {@link Person person}.
	 *
	 * @param p the person
	 * @return the zone of a pack station
	 */
	public ZoneAndLocation select(Person p);
	
}
