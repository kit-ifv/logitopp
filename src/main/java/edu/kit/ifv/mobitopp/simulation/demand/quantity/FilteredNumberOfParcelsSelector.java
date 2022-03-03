package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import java.util.function.Predicate;

public class FilteredNumberOfParcelsSelector<R> implements ParcelQuantityModel<R> {

	private final Predicate<R> filter;
	private final ParcelQuantityModel<R> other;
	
	public FilteredNumberOfParcelsSelector(ParcelQuantityModel<R> other, Predicate<R> filter) {
		this.filter = filter;
		this.other = other;
	}
	
	@Override
	public int select(R recipient, double randomNumber) {
		if (filter.test(recipient)) {
			return other.select(recipient, randomNumber);
		} else {
			return 0;
		}
	}

}
