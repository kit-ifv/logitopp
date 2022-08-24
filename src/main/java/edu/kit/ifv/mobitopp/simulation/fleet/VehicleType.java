package edu.kit.ifv.mobitopp.simulation.fleet;

import java.util.Arrays;

public enum VehicleType {
	
	CAR(1),
	TRUCK(2),
	OTHER(3);
	
	private final int number;
	
	private VehicleType(int number) {
		this.number = number;
	}
	

	public int asInt() {
		return this.number;
	}
	

	public String asString() {
		return this.name();
	}

	public static VehicleType fromInt(int number) {
		return Arrays.stream(VehicleType.values())
					 .filter(p -> p.asInt() == number)
					 .findFirst()
					 .orElseGet(() -> {
						 throw new IllegalArgumentException("Cannot parse " + number + " as vehicle type!");
					 });
	}
}
