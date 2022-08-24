package edu.kit.ifv.mobitopp.simulation.fleet;

import edu.kit.ifv.mobitopp.simulation.business.BusinessBuilder;

public interface FleetModel {

	public Fleet estimate(BusinessBuilder business);
	
}
