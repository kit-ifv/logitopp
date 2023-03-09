package edu.kit.ifv.mobitopp.simulation.distribution.region;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import lombok.Getter;

public class TransportChain {

	private final List<DistributionCenter> hubs;
	@Getter private final boolean deliveryDirection;
	
	public TransportChain(List<DistributionCenter> hubs, boolean deliveryDirection) { //TODO validate relations and size
		this.hubs = hubs;
		this.deliveryDirection = deliveryDirection;
	}
	
	public static TransportChain inDeliveryDirection(List<DistributionCenter> hubs) {
		return new TransportChain(hubs, true);
	}
	
	public static TransportChain inPickUpDirection(List<DistributionCenter> hubs) {
		return new TransportChain(hubs, false);
	}
	
	public DistributionCenter first() {
		return hubs.get(0);
	}
	
	public DistributionCenter last() {
		return this.hubs.get(hubs.size() - 1);
	}
	
	public List<DistributionCenter> intermediate() {
		if (hubs.size() <= 2) {
			return List.of();
		}
		
		return hubs.subList(1, hubs.size() - 1);
	}
	
	public boolean canTransport(IParcel parcel) {
		return last().getRegionalStructure().getServiceArea().canServe(parcel);
	}
	
}
