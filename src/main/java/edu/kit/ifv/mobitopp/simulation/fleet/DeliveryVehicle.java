package edu.kit.ifv.mobitopp.simulation.fleet;

import lombok.Getter;

@Getter
public class DeliveryVehicle {
	
	private final VehicleType type;
	private final int capacity;
	
	public DeliveryVehicle(VehicleType type, int capacity) {
		this.type = type;
		this.capacity = capacity;
	}

}
