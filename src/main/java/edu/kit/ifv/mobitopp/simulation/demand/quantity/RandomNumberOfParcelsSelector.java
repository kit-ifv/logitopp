package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import java.util.Random;

public class RandomNumberOfParcelsSelector<R> implements ParcelQuantityModel<R> {

	private final double percent;
	private final int min;
	private final int max;
	
	public RandomNumberOfParcelsSelector(int min, int max, double percent) {
		validate(min, max, percent);
		
		this.percent = percent;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public int select(R recipient, double randomNumber) {
		if (randomNumber > percent) {
			return 0;
		}
		
		Random random = new Random((long) (randomNumber * Long.MAX_VALUE));
		
		return min + random.nextInt(max - min);
		
	}
	
	/**
	 * Validates the given lower and upper bound.
	 *
	 * @param min the lower bound
	 * @param max the upper bound
	 * @param percent
	 */
	private void validate(int min, int max, double percent) {
		if (min < 0) {
			throw new IllegalArgumentException("Min should not be less than 0. Otherwise a negative amount of parcels could be ordered.");
		}
		
		if (max < min) {
			throw new IllegalArgumentException("Max should not be less than min.");
		}
		
		if (! (0 <= percent && percent <= 1)) {
			throw new IllegalArgumentException("Percent should be in [0,1] but is " + percent);
		}
	}

}
