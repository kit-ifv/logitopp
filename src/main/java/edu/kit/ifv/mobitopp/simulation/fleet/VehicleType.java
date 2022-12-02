package edu.kit.ifv.mobitopp.simulation.fleet;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public enum VehicleType {
	
	OTHER(0) {
		@Override
		public	Mode getMode() {
			return StandardMode.CAR;
		}

		@Override
		public int getCapacity() {
			return 0;
		}
	},
	BIKE(1) {
		@Override
		public	Mode getMode() {
			return StandardMode.BIKE;
		}

		@Override
		public int getCapacity() {
			return 50;
		}
	},
	TRUCK(2) {
		@Override
		public	Mode getMode() {
			return StandardMode.TRUCK;
		}

		@Override
		public int getCapacity() {
			return 160;
		}
	},
	TRAM(3) {
		@Override
		public	Mode getMode() {
			return StandardMode.UNKNOWN;//TODO check if exception ok
		}

		@Override
		public int getCapacity() {
			return 4;
		}
	};
	
	private final int number;
	
	private VehicleType(int number) {
		this.number = number;
	}
	
	public abstract Mode getMode();
	
	public abstract int getCapacity();
	

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
