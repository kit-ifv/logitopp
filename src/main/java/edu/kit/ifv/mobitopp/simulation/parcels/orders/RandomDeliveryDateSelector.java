package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static java.lang.Math.round;

import java.util.Collection;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class RandomDeliveryDateSelector is a {@link DeliveryDateSelector}.
 * A date is selected randomly from a time interval and then cast to a certain precision (e.g. hour or day).
 */
public class RandomDeliveryDateSelector implements DeliveryDateSelector {
	public static final Function<Time, Time> DAY_PRECISION = time -> Time.start.plusDays(time.getDay());
	public static final Function<Time, Time> HOUR_PRECISION = time -> Time.start.plusHours(time.getHour());
	public static final Function<Time, Time> MINUTE_PRECISION = time -> Time.start.plusHours(time.getMinute());
	public static final Function<Time, Time> SECOND_PRECISION = time -> Time.start.plusHours(time.getSecond());
	
	
	private final Time from;
	private final Time until;
	private final Function<Time, Time> precisionFilter;
	
	
	/**
	 * Instantiates a new {@link RandomDeliveryDateSelector}
	 * with the given time interval [from, untilExcusive)
	 * and the given date precision.
	 *
	 * @param from the from
	 * @param untilExclusive the until exclusive
	 * @param precision the precision
	 */
	public RandomDeliveryDateSelector(Time from, Time untilExclusive,
		Function<Time, Time> precision) {
		this.precisionFilter = precision;
		
		this.from = precisionFilter.apply(from);
		this.until = precisionFilter.apply(untilExclusive);
	}
	
	/**
	 * Instantiates a new {@link RandomDeliveryDateSelector}
	 * with the interval [Monday,Sunday) = [Monday,Saturday]
	 * and day precision.
	 */
	public RandomDeliveryDateSelector() {
		this(Time.start, Time.start.plusDays(6), DAY_PRECISION);
	}

	/**
	 * Selects a planned arrival date uniformly distributed
	 * from the interval [from,until].
	 * The selected date is cast to the models precision.
	 *
	 * @param recipient the recipient
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param destination the parcel's {@link ParcelDestinationType}
	 * @param otherParcels the other {@link Parcel}s the recipient already ordered
	 * @param randomNumber a random number
	 * @return the planned arrival date
	 */
	@Override
	public Time select(PickUpParcelPerson recipient, int numOfParcels, ParcelDestinationType destination, Collection<Parcel> otherParcels, double randomNumber) {
		int start = from.toSeconds();
		int end = until.toSeconds();
		
		int diff = end - start;

		
		Time t = new SimpleTime(start + (int)round(diff*randomNumber));
		
		return precisionFilter.apply(t);
	}

}
