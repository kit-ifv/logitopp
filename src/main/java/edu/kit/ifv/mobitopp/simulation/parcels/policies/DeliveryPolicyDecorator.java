package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import edu.kit.ifv.mobitopp.simulation.parcels.BaseParcel;

public interface DeliveryPolicyDecorator<P extends BaseParcel> {
	
	public ParcelDeliveryPolicy<P> of(ParcelDeliveryPolicy<P> delegate);

}
