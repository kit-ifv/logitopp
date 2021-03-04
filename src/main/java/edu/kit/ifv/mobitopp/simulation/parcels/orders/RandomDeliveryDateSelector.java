package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static java.lang.Math.round;

import java.util.Collection;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class RandomDeliveryDateSelector implements DeliveryDateSelector {
	public static final Function<Time, Time> DAY_PRECISION = time -> Time.start.plusDays(time.getDay());
	public static final Function<Time, Time> HOUR_PRECISION = time -> Time.start.plusHours(time.getHour());
	
	
	private final Time from;
	private final Time until;
	private final Function<Time, Time> precisionFilter;
	
	public RandomDeliveryDateSelector() {
		this(Time.start, Time.start.plusDays(6), DAY_PRECISION);
	}
	

	public RandomDeliveryDateSelector(Time from, Time untilExclusive,
		Function<Time, Time> precision) {
		this.precisionFilter = precision;
		
		this.from = precisionFilter.apply(from);
		this.until = precisionFilter.apply(untilExclusive);
	}


	@Override
	public Time select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Collection<Parcel> otherParcels, double randomNumber) {
		int start = from.toSeconds();
		int end = until.toSeconds();
		
		int diff = end - start;

		
		Time t = new SimpleTime(start + (int)round(diff*randomNumber));
		
		return precisionFilter.apply(t);
	}

}
