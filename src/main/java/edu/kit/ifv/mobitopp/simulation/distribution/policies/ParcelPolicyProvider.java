package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import lombok.Setter;

@Setter
public class ParcelPolicyProvider {

	private ParcelDeliveryPolicy<PrivateParcel> privateParcelPolicy = new DummyDeliveryPolicy<>(RecipientType.PERSONAL);
	private ParcelDeliveryPolicy<BusinessParcel> businessParcelPolicy = new DummyDeliveryPolicy<>(RecipientType.BUSINESS);
	
	
	public ParcelDeliveryPolicy<PrivateParcel> forPrivate() {
		return this.privateParcelPolicy;
	}
	
	public ParcelDeliveryPolicy<BusinessParcel> forBusiness() {
		return this.businessParcelPolicy;
	}
	
	
}
