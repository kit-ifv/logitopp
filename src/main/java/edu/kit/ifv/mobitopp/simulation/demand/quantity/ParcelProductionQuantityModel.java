package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;

public class ParcelProductionQuantityModel<P extends ParcelAgent> implements ParcelQuantityModel<P> {
		
	@Override
	public int select(P producer, double randomNumber) {
		return producer.getDemandQuantity().getProduction();
	}

}
