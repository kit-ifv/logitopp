package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.TRAM;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
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
		return this.hubs.get(hubs.size() - 1).getVehicleType();
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
	
	public int size() {
		return hubs.size();
	}
	
	public boolean canTransport(IParcel parcel) {
		return last().getRegionalStructure().getServiceArea().canServe(parcel);
	}
	public boolean contains(DistributionCenter hub) {
		return this.hubs.contains(hub);
	}
	
	public Collection<VehicleType> getVehicleTypes() {
		return hubs.stream().map(dc -> dc.getVehicleType()).collect(Collectors.toList());
	}
	
	public boolean uses(VehicleType vehicle) {
		return getVehicleTypes().contains(vehicle);
	}
	
	public Collection<Pair<DistributionCenter, DistributionCenter>> legsOfType(VehicleType type) {
		List<Pair<DistributionCenter, DistributionCenter>> legs = new ArrayList<>();
		
		DistributionCenter origin = first();
		for (DistributionCenter destination: tail()) {
			
			if (origin.getVehicleType().equals(type)) {
				legs.add(new Pair<>(origin, destination));
			}
			
			origin = destination;
		}
		
		return legs;
	}
	
	public Optional<DistributionCenter> nextHubAfter(DistributionCenter hub) {
		if (!hubs.contains(hub)) {
			throw new IllegalArgumentException("This transport chain [" + this.hubs + "] does not contain the requested hub: " + hub);
		}
		
		int index = hubs.indexOf(hub) + 1;
		
		return index < hubs.size() ? Optional.of(hubs.get(index)) : Optional.empty();
	}
	
	public int maxNumberOfTripsOnDayAfter(TimeTable timeTable, Time time) {
		
		int minConnectionCapacity = Integer.MAX_VALUE;
		for (Pair<DistributionCenter, DistributionCenter> leg : legsOfType(TRAM)) {
			int capacity =
				timeTable.getConnectionsOnDay(leg.getFirst(), leg.getSecond(), time)
						 .mapToInt(Connection::freeCapacity)
						 .sum();
			minConnectionCapacity = Math.min(capacity, minConnectionCapacity);
		}
		
		int lastMileDepartures = last().getFleet().size();
		
		if (lastMileVehicle().equals(BIKE)) {
			lastMileDepartures *= 3; //TODO configurable? 2,3 or 4
		}
		
		return Math.min(lastMileDepartures, minConnectionCapacity);
	}
	
	@Override
	public String toString() {
		return hubs.stream().map(DistributionCenter::toString).collect(joining(", "));
	}
}
