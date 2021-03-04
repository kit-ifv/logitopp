package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public interface NumberOfParcelsSelector {

	public int select(PickUpParcelPerson person, double randomNumber);
	
}
