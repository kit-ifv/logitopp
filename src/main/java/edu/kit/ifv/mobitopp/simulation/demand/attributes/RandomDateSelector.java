package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static java.lang.Math.round;

import java.util.Collection;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class RandomDeliveryDateSelector is a {@link ParcelDemandModelStep}.
 * A date is selected randomly from a time interval and then cast to a certain precision (e.g. hour or day).
 */
public class RandomDateSelector<A extends ParcelAgent, P extends ParcelBuilder<A>> implements ParcelDemandModelStep<A, P, Time> {
	
	public static final Function<Time, Time> DAY_PRECISION = time -> Time.start.plusDays(time.getDay());
	public static final Function<Time, Time> HOUR_PRECISION = time -> DAY_PRECISION.apply(time).plusHours(time.getHour());
	public static final Function<Time, Time> MINUTE_PRECISION = time -> HOUR_PRECISION.apply(time).plusMinutes(time.getMinute());
	public static final Function<Time, Time> SECOND_PRECISION = time -> MINUTE_PRECISION.apply(time).plusSeconds(time.getSecond());
	
	
	private final Time from;
	private final Time until;
	private final Function<Time, Time> precisionFilter;
	
	/**
	 * Instantiates a new {@link RandomDateSelector}
	 * with the given time interval [from, untilExcusive)
	 * and the given date precision.
	 *
	 * @param from the from
	 * @param untilExclusive the until exclusive
	 * @param precision the precision
	 */
	public RandomDateSelector(Time from, Time untilExclusive,
		Function<Time, Time> precision) {
		this.precisionFilter = precision;
	
		this.from = precisionFilter.apply(from);
		this.until = precisionFilter.apply(untilExclusive);
	}
	
	/**
	 * Instantiates a new {@link RandomDateSelector}
	 * with the given time interval [from, untilExcusive)
	 * and day precision.
	 *
	 * @param from the from
	 * @param untilExclusive the until exclusive
	 */
	public RandomDateSelector(Time from, Time untilExclusive) {
		this(from, untilExclusive, DAY_PRECISION);
	}
	
	/**
	 * Instantiates a new {@link RandomDateSelector}
	 * with the interval [Monday,Sunday) = [Monday,Saturday]
	 * and the given precision.
	 *
	 * @param precision the precision
	 */
	public RandomDateSelector(Function<Time, Time> precision) {
		this(Time.start, Time.start.plusDays(6), precision);
	}
	
	/**
	 * Instantiates a new {@link RandomDateSelector}
	 * with the interval [Monday,Sunday) = [Monday,Saturday]
	 * and day precision.
	 */
	public RandomDateSelector() {
		this(DAY_PRECISION);
	}

	/**
	 * Selects a planned arrival date uniformly distributed
	 * from the interval [from,until].
	 * The selected date is cast to the models precision.
	 *
	 * @param parcel the {@link ParcelBuilders} for which an arrival date is selected
	 * @param otherParcels the other {@link ParcelBuilders}s the recipient already ordered
	 * @param numOfParcels the number of parcels the recipient will order
	 * @param randomNumber a random number
	 * @return the planned arrival date
	 */
	@Override
	public  Time select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
		int start = from.toSeconds();
		int end = until.toSeconds();
		
		int diff = end - start;

		
		Time t = new SimpleTime(start + (int)round(diff*randomNumber));
		
		return precisionFilter.apply(t);
	}

}
