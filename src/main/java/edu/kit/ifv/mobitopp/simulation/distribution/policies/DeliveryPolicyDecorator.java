package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public interface DeliveryPolicyDecorator<P extends IParcel> {
	
	public ParcelDeliveryPolicy<P> of(ParcelDeliveryPolicy<P> delegate);

}
