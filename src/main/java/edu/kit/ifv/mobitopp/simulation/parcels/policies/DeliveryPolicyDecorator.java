package edu.kit.ifv.mobitopp.simulation.parcels.policies;

public interface DeliveryPolicyDecorator {
	
	public ParcelDeliveryPolicy of(ParcelDeliveryPolicy delegate);

}
