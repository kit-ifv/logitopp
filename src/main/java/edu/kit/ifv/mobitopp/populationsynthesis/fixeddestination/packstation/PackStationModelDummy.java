package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

/**
 * The Class PackStationModelDummy is an exemplary implementation of the PackStationModel interface.
 * To be replaced!!
 */
public class PackStationModelDummy implements PackStationModel {

	/**
	 * Returns the given person's home zone as their pack station zone.
	 *
	 * @param person the person
	 * @return the zone
	 */
	@Override
	public Zone select(PersonBuilder person) {
		return person.homeZone();
	}

}
