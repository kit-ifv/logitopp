package edu.kit.ifv.mobitopp.simulation.business.partners;

import edu.kit.ifv.mobitopp.simulation.business.Business;

public interface NumberOfPartnersModel {

	public int select(Business business, double randomNumber);
}
