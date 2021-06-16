package edu.kit.ifv.mobitopp.simulation.parcels.orders;

public class NullNumerOfParcelsSelector<R> implements NumberOfParcelsSelector<R> {

	@Override
	public int select(R recipient, double randomNumber) {
		return 0;
	}

}
