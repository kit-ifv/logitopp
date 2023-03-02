package edu.kit.ifv.mobitopp.simulation.distribution.fleet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class Fleet {
	
	private final DistributionCenter distributionCenter;
	
	private final int numVehicles;
	private final Collection<DeliveryVehicle> vehicles;
	private final List<DeliveryVehicle> availableVehicles;
	private final Map<DeliveryVehicle, Time> returnTimes;
	private final VehicleType vehicleType;
	
	public Fleet(VehicleType vehicleType, int numVehicles, DistributionCenter distributionCenter) {
		this.distributionCenter = distributionCenter;
		this.numVehicles = numVehicles;
		
		this.returnTimes = new LinkedHashMap<>();
		this.vehicleType = vehicleType;
		this.vehicles = new ArrayList<>();
		this.availableVehicles = new ArrayList<>();
		
		initVehicles();
		this.availableVehicles.addAll(vehicles);
	}

	private void initVehicles() {
			
		for (int i = 0; i < numVehicles; i++) {
			vehicles.add(new DeliveryVehicle(vehicleType, 150, distributionCenter)); //TODO determine capacity in vehicle type
		}
		
	}
	
	public void bookVehicleUntil(DeliveryVehicle vehicle, Time returnTime) {
		if (this.returnTimes.containsKey(vehicle)) {
			throw new IllegalArgumentException("Vehicle is already booked:" + vehicle + " until " + returnTimes.get(vehicle));
		}
		
		this.returnTimes.put(vehicle, returnTime);
		this.availableVehicles.remove(vehicle);		
	}
	
	public void returnVehicle(DeliveryVehicle vehicle) {
		if (!this.returnTimes.containsKey(vehicle)) {
			throw new IllegalArgumentException("The given vehicle is not booked and cannot be returned: " + vehicle);
		}
		
		this.returnTimes.remove(vehicle);
		this.availableVehicles.add(vehicle);
	}
	
	public int size() {
		return this.numVehicles;
	}
	
	public Optional<DeliveryVehicle> getAvailableVehicle() {
		if (availableVehicles.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(availableVehicles.get(0));
		}
	}
	
}
