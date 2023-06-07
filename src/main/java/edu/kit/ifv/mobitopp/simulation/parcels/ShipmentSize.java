package edu.kit.ifv.mobitopp.simulation.parcels;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.Random;

public enum ShipmentSize {

	SMALL(420, 18240) {
		@Override
		public ParcelUnit toParcelUnits() {
			return ParcelUnit.of(1);
		}
	},
	MEDIUM(18240, 38760) {
		@Override
		public ParcelUnit toParcelUnits() {
			return ParcelUnit.of(2);
		}
	},
	LARGE(38760, 82080) {
		@Override
		public ParcelUnit toParcelUnits() {
			return ParcelUnit.of(3);
		}
	},
	EXTRA_LARGE(820080, 171000) {
		@Override
		public ParcelUnit toParcelUnits() {
			return ParcelUnit.of(5);
		}
	},
	PALLET(0, 0) {
		@Override
		public ParcelUnit toParcelUnits() {
			return ParcelUnit.of(-1);
		}
	},
	CONTAINER(0, 0) {
		@Override
		public ParcelUnit toParcelUnits() {
			return ParcelUnit.of(-1);
		}
	};
	
	private final int minVolume;
	private final int meanVolume;
	private final int maxVolume;
	private final double stdDev;
	
	private ShipmentSize(int minVolume, int maxVolume) {
		this.minVolume  = minVolume;
		this.meanVolume = minVolume + (int) ((maxVolume - minVolume) / 2.0);
		this.maxVolume  = maxVolume;
		this.stdDev     = (maxVolume - minVolume) * 0.1;
	}
	
	public int getVolume(Object parcel) {
		Random rand = new Random(parcel.hashCode());

		double standardGauss = rand.nextGaussian();
		double scaledGauss = stdDev*standardGauss + meanVolume;
		
		return (int) min(max(minVolume, round(scaledGauss)), maxVolume);
	}
	
	public abstract ParcelUnit toParcelUnits();
	
}
