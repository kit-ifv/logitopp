package edu.kit.ifv.mobitopp.simulation.parcels.demand.quantity;

public class NullNumerOfParcelsSelector<R> implements ParcelQuantityModel<R> {

	@Override
	public int select(R recipient, double randomNumber) {
		return 0;
	}

}
