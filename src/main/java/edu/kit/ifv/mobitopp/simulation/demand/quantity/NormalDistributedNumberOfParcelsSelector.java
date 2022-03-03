package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.Random;


/**
 * The Class NormalDistributedNumberOfParcelsSelector is a {@link ParcelQuantityModel}.
 * It selects a number drawn from a capped normal distribution.
 */
public class NormalDistributedNumberOfParcelsSelector<R> implements ParcelQuantityModel<R> {

	private double mean;
	private double stdDev;
	private int capMin;
	private int capMax;


	/**
	 * Instantiates a new {@link NormalDistributedNumberOfParcelsSelector}
	 * with the given mean, standard deviation and upper bound.
	 * The lower bound is set to 0.
	 *
	 * @param mean the mean
	 * @param stdDev the standard deviation
	 * @param capMax the cap max
	 */
	public NormalDistributedNumberOfParcelsSelector(double mean, double stdDev, int capMax) {
		this(mean, stdDev, 0, capMax);
	}
	
	/**
	 * Instantiates a new {@link NormalDistributedNumberOfParcelsSelector}
	 * with the given mean, standard deviation and lower and upper bound.
	 * @param mean the mean
	 * @param stdDev the standard deviation
	 * @param capMin the lower bound
	 * @param capMax the upper bound
	 */
	public NormalDistributedNumberOfParcelsSelector(double mean, double stdDev, int capMin, int capMax) {
		this.mean = mean;
		this.stdDev = stdDev;
		
		validate(capMin, capMax);
		
		this.capMin = capMin;
		this.capMax = capMax;
	}

	/**
	 * Validates the given lower and upper bound.
	 *
	 * @param capMin the lower bound
	 * @param capMax the upper bound
	 */
	private void validate(int capMin, int capMax) {
		if (capMin < 0) {
			throw new IllegalArgumentException("CapMin should not be less than 0. Otherwise a negative amount of parcels could be ordered.");
		}
		
		if (capMax < capMin) {
			throw new IllegalArgumentException("CapMax should not be less than CapMin.");
		}
	}
	

	/**
	 * Selects the number of parcels, the given recipient orders
	 * from the normal distribution.
	 *
	 * @param recipient the recipient
	 * @param randomNumber a random number
	 * @return the selected number of parcels
	 */
	@Override
	public int select(R recipient, double randomNumber) {
		double standardGauss = new Random((long) (randomNumber * Long.MAX_VALUE)).nextGaussian();
		
		double scaledGauss = stdDev*standardGauss + mean;
		
		return (int) min(max(capMin, round(scaledGauss)), capMax);
	}

}
