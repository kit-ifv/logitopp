package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public interface ParcelOrderModel<R> {

	public Collection<IParcel> createParcelOrders(R recipient);
}
