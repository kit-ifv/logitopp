package edu.kit.ifv.mobitopp.simulation.distribution.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import lombok.Getter;

@Getter
public class RegionalReach {

		private final DistributionCenter distributionCenter;
	 	private final ServiceArea serviceArea;	 
	 
	 	private final Collection<DistributionCenter> relatedDeliveryHubs;
		private final Collection<DistributionCenter> relatedPickUpHubs;
		
		private Collection<TransportChain> deliveryChains;
		private Collection<TransportChain> pickUpChains;
		
		public RegionalReach(DistributionCenter distributionCenter, ServiceArea serviceArea) {
			this.distributionCenter = distributionCenter;
			this.serviceArea = serviceArea;
			
			this.relatedDeliveryHubs = new ArrayList<>();
			this.relatedPickUpHubs = new ArrayList<>();
			this.deliveryChains = null;
			this.pickUpChains = null;
		}
		
		
		public void addRelatedDeliveryHub(DistributionCenter hub) {
			if (!this.relatedDeliveryHubs.contains(hub)) {
				this.relatedDeliveryHubs.add(hub);
				
				hub.getRegionalStructure().addRelatedPickUpHub(distributionCenter);
			}
		}
		
		public void addRelatedPickUpHub(DistributionCenter hub) {
			if (!this.relatedPickUpHubs.contains(hub)) {
				this.relatedPickUpHubs.add(hub);
				
				hub.getRegionalStructure().addRelatedDeliveryHub(distributionCenter);
			}
		}
		
		public void printRelations() {
			System.out.println(this.relatedDeliveryHubs.stream().map(dc -> dc.getId() + "").collect(Collectors.joining(",")) 
					+ " -> " + this.distributionCenter.getId() + " <- " +
					this.relatedPickUpHubs.stream().map(dc -> dc.getId() + "").collect(Collectors.joining(","))
			);
		}
		
		public Collection<TransportChain> getDeliveryChains() {
			if (this.deliveryChains == null) {
				this.deliveryChains = TransportChainFactory.buildDeliveryChains(distributionCenter);
			}
			
			return this.deliveryChains;
		}
		
		public Collection<TransportChain> getPickUpChains() {
			if (this.pickUpChains == null) {
				this.pickUpChains = TransportChainFactory.buildPickUpChains(distributionCenter);
			}
			
			return this.pickUpChains;
		}
		
}
