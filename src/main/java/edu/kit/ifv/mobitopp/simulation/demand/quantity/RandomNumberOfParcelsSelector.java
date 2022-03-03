package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import java.util.Random;

public class RandomNumberOfParcelsSelector<R> implements ParcelQuantityModel<R> {

	private final double percent;
	private final int min;
	private final int max;
	
	public RandomNumberOfParcelsSelector(int min, int max, double percent) {
		this.percent = percent;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public int select(R recipient, double randomNumber) {
		if (randomNumber >= percent) {
			return 0;
		}
		
		Random random = new Random((long) (randomNumber * Long.MAX_VALUE));
		
		return min + random.nextInt(max - min);
		
	}

}
