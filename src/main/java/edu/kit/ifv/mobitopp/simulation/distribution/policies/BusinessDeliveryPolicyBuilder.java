package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;

public class BusinessDeliveryPolicyBuilder {

	private ParcelDeliveryPolicy<BusinessParcel> policy;
	
	public BusinessDeliveryPolicyBuilder() {
		this.policy = new DummyDeliveryPolicy<BusinessParcel>(RecipientType.BUSINESS);
	}
	
	public BusinessDeliveryPolicyBuilder basedOn(ParcelDeliveryPolicy<BusinessParcel> policy) {
		this.policy = policy;
		return this;
	}
	
	public BusinessDeliveryPolicyBuilder respectOpeningHours() {
		this.policy = new OpeningHoursDeliveryPolicy(policy);
		return this;
	}

	public BusinessDeliveryPolicyBuilder wrapWith(DeliveryPolicyDecorator<BusinessParcel> decorator) {
		this.policy = decorator.of(this.policy);
		return this;
	}
	
	public ParcelDeliveryPolicy<BusinessParcel> build() {
		return this.policy;
	}
	
	
}
