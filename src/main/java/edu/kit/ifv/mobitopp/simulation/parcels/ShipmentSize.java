package edu.kit.ifv.mobitopp.simulation.parcels;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.Random;

public enum ShipmentSize {

	SMALL(420, 18240),
	MEDIUM(18240, 38760),
	LARGE(38760, 82080),
	EXTRA_LARGE(820080, 171000),
	PALLET(0, 0),
	CONTAINER(0, 0);
	
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
	
}
