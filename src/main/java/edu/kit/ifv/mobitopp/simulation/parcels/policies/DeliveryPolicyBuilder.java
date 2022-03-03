package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import edu.kit.ifv.mobitopp.populationsynthesis.neighborhood.NeighborhoodRelationship;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.model.PrivateParcel;

public class DeliveryPolicyBuilder {

	private ParcelDeliveryPolicy<PrivateParcel> policy;
	
	public DeliveryPolicyBuilder() {
		this.policy = new BaseDeliveryPolicy();
	}
	
	public DeliveryPolicyBuilder basedOn(ParcelDeliveryPolicy<PrivateParcel> policy) {
		this.policy = policy;
		return this;
	}
	
	public DeliveryPolicyBuilder checkOtherHouseholdMembers() {
		this.policy = new HouseholdDeliveryPolicy(policy);
		return this;
	}
		
	public DeliveryPolicyBuilder checkNeighbors(NeighborhoodRelationship neighborhood, DeliveryResults results) {
		this.policy = new NeighborhoodDeliveryPolicy(policy, neighborhood, results);
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

	public DeliveryPolicyBuilder wrapWith(DeliveryPolicyDecorator<PrivateParcel> decorator) {
		this.policy = decorator.of(this.policy);
		return this;
	}
	
	public ParcelDeliveryPolicy<PrivateParcel> build() {
		return this.policy;
	}
	
	
}
