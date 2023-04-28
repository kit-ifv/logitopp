package edu.kit.ifv.mobitopp.simulation.distribution.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.collections.Pair;
import lombok.Getter;

public class TransportChain {

	@Getter private final List<DistributionCenter> hubs;
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
	
	public VehicleType lastMileVehicle() {
		return this.hubs.get(hubs.size() - 1).getFleet().getVehicleType();
	}
	
	public List<DistributionCenter> tail() {
		if (hubs.size() <= 1) {
			return List.of();
		}
		
		return hubs.subList(1, hubs.size());
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
	
	public Collection<VehicleType> getVehicleTypes() {
		return hubs.stream().map(dc -> dc.getFleet().getVehicleType()).collect(Collectors.toList());
	}
	
	public Collection<Pair<DistributionCenter, DistributionCenter>> legsOfType(VehicleType type) {
		List<Pair<DistributionCenter, DistributionCenter>> legs = new ArrayList<>();
		
		DistributionCenter origin = first();
		for (DistributionCenter destination: tail()) {
			
			if (origin.getFleet().getVehicleType().equals(type)) {
				legs.add(new Pair<>(origin, destination));
			}
			
			origin = destination;
		}
		
		return legs;
	}
	
}
