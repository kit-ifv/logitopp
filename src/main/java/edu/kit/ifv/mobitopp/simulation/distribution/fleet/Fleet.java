package edu.kit.ifv.mobitopp.simulation.distribution.fleet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
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
	private final DeliveryResults results;
	private final double vehicleVolume;
	
	public Fleet(
			VehicleType vehicleType,
			int numVehicles,
			double vehicleVolume,
			DistributionCenter distributionCenter,
			DeliveryResults results
	) {
		this.distributionCenter = distributionCenter;
		this.numVehicles = numVehicles;
		this.vehicleVolume = vehicleVolume;
		this.results = results;

		this.returnTimes = new LinkedHashMap<>();
		this.vehicleType = vehicleType;
		this.vehicles = new ArrayList<>();
		this.availableVehicles = new ArrayList<>();
		
		initVehicles();
	}

	private void initVehicles() {
			
		for (int i = 0; i < numVehicles; i++) {
			addVehicle(
					new DeliveryVehicle(vehicleType, vehicleVolume, distributionCenter)
			);
		}
		
	}

	public void addVehicle(DeliveryVehicle vehicle) {
		this.vehicles.add(vehicle);
		this.availableVehicles.add(vehicle);
		results.logVehicle(vehicle);
	}
	
	public void bookVehicleUntil(DeliveryVehicle vehicle, Time returnTime) {
		if (vehicleType.equals(VehicleType.TRAM)) {return;}
		if (this.returnTimes.containsKey(vehicle)) {
			throw new IllegalArgumentException("Vehicle is already booked:" + vehicle + " until " + returnTimes.get(vehicle));
		}
		
		this.returnTimes.put(vehicle, returnTime);
		this.availableVehicles.remove(vehicle);		
	}
	
	public void returnVehicle(DeliveryVehicle vehicle) {
		if (vehicleType.equals(VehicleType.TRAM)) {return;}
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
	
	public boolean hasAvailableVehicle() {
		return !availableVehicles.isEmpty();
	}
	
}
