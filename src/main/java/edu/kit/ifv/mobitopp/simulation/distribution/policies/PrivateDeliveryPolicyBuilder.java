package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import edu.kit.ifv.mobitopp.populationsynthesis.neighborhood.NeighborhoodRelationship;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;

public class PrivateDeliveryPolicyBuilder {

	private ParcelDeliveryPolicy<PrivateParcel> policy;
	
	public PrivateDeliveryPolicyBuilder() {
		this.policy = new BaseDeliveryPolicy();
	}
	
	public PrivateDeliveryPolicyBuilder basedOn(ParcelDeliveryPolicy<PrivateParcel> policy) {
		this.policy = policy;
		return this;
	}
	
	public PrivateDeliveryPolicyBuilder checkOtherHouseholdMembers() {
		this.policy = new HouseholdDeliveryPolicy(policy);
		return this;
	}
		
	public PrivateDeliveryPolicyBuilder checkNeighbors(NeighborhoodRelationship neighborhood, DeliveryResults results) {
		this.policy = new NeighborhoodDeliveryPolicy(policy, neighborhood, results);
		return this;
	}
	
	public PrivateDeliveryPolicyBuilder checkNeighbors(NeighborhoodRelationship neighborhood) {
		this.policy = new NeighborhoodDeliveryPolicy(policy, neighborhood);
		return this;
	}
	
	public PrivateDeliveryPolicyBuilder abortAfter(int attempts) {
		this.policy = new DeliveryAttemptsPolicy(policy, attempts);
		return this;
	}

	public PrivateDeliveryPolicyBuilder wrapWith(DeliveryPolicyDecorator<PrivateParcel> decorator) {
		this.policy = decorator.of(this.policy);
		return this;
	}
	
	public ParcelDeliveryPolicy<PrivateParcel> build() {
		return this.policy;
	}
	
	
}
