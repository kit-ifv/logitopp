package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

/**
 * The Class HomeZonePackstationModel is an exemplary implementation of the {@link PackStationModel} interface.
 * It assigns the home zone of each person as their pack station zone.
 */
public class HomeZonePackstationModel implements PackStationModel {

	/**
	 * Returns the given {@link PersonBuilder person}'s home {@link Zone} as their pack station zone.
	 *
	 * @param person the person
	 * @return the person's home zone
	 */
	@Override
	public ZoneAndLocation select(Person person) {
		return new ZoneAndLocation(person.homeZone(), person.homeZone().centroidLocation());
	}

}
