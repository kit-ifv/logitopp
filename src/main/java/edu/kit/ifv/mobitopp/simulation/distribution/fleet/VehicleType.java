package edu.kit.ifv.mobitopp.simulation.distribution.fleet;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public enum VehicleType {
	
	OTHER(0) {
		@Override
		public	Mode getMode() {
			return StandardMode.CAR;
		}

	},
	BIKE(1) {
		@Override
		public	Mode getMode() {
			return StandardMode.BIKE;
		}

	},
	TRUCK(2) {
		@Override
		public	Mode getMode() {
			return StandardMode.TRUCK;
		}

	},
	TRAM(3) {
		@Override
		public	Mode getMode() {
			return StandardMode.PUBLICTRANSPORT;//TODO check if exception could be used here
		}

	};
	
	private final int number;
	
	private VehicleType(int number) {
		this.number = number;
	}
	
	public abstract Mode getMode();

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
					 .orElseThrow(() -> new IllegalArgumentException("Cannot parse " + number + " as vehicle type!"));
	}
}
