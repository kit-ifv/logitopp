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

		@Override
		public int getVolume() {
			return 0;
		}
	},
	BIKE(1) {
		@Override
		public	Mode getMode() {
			return StandardMode.BIKE;
		}

		@Override
		public int getVolume() {
			return 120*80*940;
		}
	},
	TRUCK(2) {
		@Override
		public	Mode getMode() {
			return StandardMode.TRUCK;
		}

		@Override
		public int getVolume() {
			return 12*100*100*100;
		}
	},
	TRAM(3) {
		@Override
		public	Mode getMode() {
			return StandardMode.PUBLICTRANSPORT;//TODO check if exception ok
		}

		@Override
		public int getVolume() {
			return 0;
		}
	};
	
	private final int number;
	
	private VehicleType(int number) {
		this.number = number;
	}
	
	public abstract Mode getMode();
	
	public abstract int getVolume();
	

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
