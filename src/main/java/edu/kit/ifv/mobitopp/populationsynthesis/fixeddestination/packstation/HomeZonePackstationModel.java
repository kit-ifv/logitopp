package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

/**
 * The Class HomeZonePackstationModel is an exemplary implementation of the PackStationModel interface.
 * It assigns the home zone of each person as their pack station zone.
 */
public class HomeZonePackstationModel implements PackStationModel {

	/**
	 * Returns the given person's home zone as their pack station zone.
	 *
	 * @param person the person
	 * @return the person's home zone
	 */
	@Override
	public Zone select(PersonBuilder person) {
		return person.homeZone();
	}

}
