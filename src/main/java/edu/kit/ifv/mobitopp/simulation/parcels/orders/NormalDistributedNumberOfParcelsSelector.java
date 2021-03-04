package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;


public class NormalDistributedNumberOfParcelsSelector implements NumberOfParcelsSelector {

	private double mean;
	private double stdDev;
	private int capMin;
	private int capMax;


	public NormalDistributedNumberOfParcelsSelector(double mean, double stdDev, int capMax) {
		this(mean, stdDev, 0, capMax);
	}
	
	public NormalDistributedNumberOfParcelsSelector(double mean, double stdDev, int capMin, int capMax) {
		this.mean = mean;
		this.stdDev = stdDev;
		
		validate(capMin, capMax);
		
		this.capMin = capMin;
		this.capMax = capMax;
	}

	private void validate(int capMin, int capMax) {
		if (capMin < 0) {
			throw new IllegalArgumentException("CapMin should not be less than 0. Otherwise a negative amount of parcels could be ordered.");
		}
		
		if (capMax < capMin) {
			throw new IllegalArgumentException("CapMax should not be less than CapMin.");
		}
	}
	
	@Override
	public int select(PickUpParcelPerson person, double randomNumber) {
		double standardGauss = new Random((long) (randomNumber * Long.MAX_VALUE)).nextGaussian();
		
		double scaledGauss = stdDev*standardGauss + mean;
		
		return (int) min(max(capMin, scaledGauss), capMax);
	}

}
