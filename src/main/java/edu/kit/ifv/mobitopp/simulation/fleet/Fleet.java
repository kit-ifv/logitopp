package edu.kit.ifv.mobitopp.simulation.fleet;

import java.util.Collection;

import lombok.Getter;

@Getter
public class Fleet {
	
	private final Collection<DeliveryVehicle> vehicles;
	
	public Fleet() {
		this.vehicles = null;
	}
	
	public Fleet(Collection<DeliveryVehicle> vehicles) {
		this.vehicles = vehicles;
	}

}
