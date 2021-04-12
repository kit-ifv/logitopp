package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import edu.kit.ifv.mobitopp.populationsynthesis.neighborhood.NeighborhoodRelationship;

public class DeliveryPolicyBuilder {

	private ParcelDeliveryPolicy policy;
	
	public DeliveryPolicyBuilder() {
		this.policy = new BaseDeliveryPolicy();
	}
	
	public DeliveryPolicyBuilder basedOn(ParcelDeliveryPolicy policy) {
		this.policy = policy;
		return this;
	}
		
	public DeliveryPolicyBuilder checkNeighbors(NeighborhoodRelationship neighborhood) {
		this.policy = new NeighborhoodDeliveryPolicy(policy, neighborhood);
		return this;
	}
	
	public DeliveryPolicyBuilder abortAfter(int attempts) {
		this.policy = new DeliveryAttemptsPolicy(policy, attempts);
		return this;
	}

	public ParcelDeliveryPolicy build() {
		return this.policy;
	}
	
	
}
