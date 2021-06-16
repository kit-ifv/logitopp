package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import java.util.function.Predicate;

public class FilteredNumberOfParcelsSelector<R> implements NumberOfParcelsSelector<R> {

	private final Predicate<R> filter;
	private final NumberOfParcelsSelector<R> other;
	
	public FilteredNumberOfParcelsSelector(NumberOfParcelsSelector<R> other, Predicate<R> filter) {
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
