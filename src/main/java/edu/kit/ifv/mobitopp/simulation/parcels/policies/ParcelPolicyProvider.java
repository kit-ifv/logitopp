package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParcelPolicyProvider {

	private final ParcelDeliveryPolicy<PrivateParcel> privateParcelPolicy;
	
	
	public ParcelDeliveryPolicy<PrivateParcel> forPrivate() {
		return this.privateParcelPolicy;
	}
	
	
}
