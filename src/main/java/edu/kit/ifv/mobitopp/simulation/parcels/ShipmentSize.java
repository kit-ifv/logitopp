package edu.kit.ifv.mobitopp.simulation.parcels;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.Random;

public enum ShipmentSize {

	SMALL(1, 10, 20, 8, 60, 40),
	MEDIUM(8, 10, 20, 17, 60, 40),
	LARGE(17, 10, 20, 36, 60, 40),
	EXTRA_LARGE(36, 20, 20, 75, 60, 40),
	PALLET(0, 0, 0, 1, 1, 1),
	CONTAINER(0, 0, 0, 1, 1, 1);
	
	private final int minX;
	private final int minY;
	private final int minZ;
	private final int maxX;
	private final int maxY;
	private final int maxZ;
	
	private ShipmentSize(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public double getVolume(Random random) {
		double x = getParcelEdge(minX, maxX, random);
		double y = getParcelEdge(minY, maxY, random);
		double z = getParcelEdge(minZ, maxZ, random);
		
		return x*y*z;
	}

	private double getParcelEdge(double min, double max, Random random) {
		double mean = (min + max) / 2.0;
		double stdDev = (max-min) * 0.1;


		double standardGauss = random.nextGaussian();
		double scaledGauss = stdDev*standardGauss + mean;

		return (int) min(max(min, round(scaledGauss)), max);
	}
	
}
