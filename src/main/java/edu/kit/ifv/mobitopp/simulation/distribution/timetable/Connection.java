package edu.kit.ifv.mobitopp.simulation.distribution.timetable;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Connection {

	private final DistributionCenter from;
	private final DistributionCenter to;
	private final Time departure;
	private final int durationMinutes;
	private final int capacity;
	
	private Collection<TimedTransportChain> bookings;

	/**
	 * Number of slots claimed by returning boxes. Unlike delivery chains, returning boxes do not
	 * book a connection in advance: they claim a free slot when they are dispatched, first come
	 * first served.
	 */
	private int returningClaims;

	public Connection(DistributionCenter from, DistributionCenter to, Time departure, int durationMinutes, int capacity) {
		assert from != null;
		assert to != null;

		this.from = from;
		this.to = to;
		this.departure = departure;
		this.durationMinutes = durationMinutes;
		this.capacity = capacity;

		this.bookings = new ArrayList<>();
		this.returningClaims = 0;
	}

	/**
	 * Claims one free slot of this connection for a returning box, if one is available.
	 *
	 * Returning boxes are handled first come first served: they are not booked in advance by the
	 * capacity coordination, but claim a remaining slot when they are actually dispatched.
	 *
	 * @return whether a slot was claimed
	 */
	public boolean claimForReturning() {
		if (!hasFreeCapacity()) {
			return false;
		}

		this.returningClaims++;
		return true;
	}
	
	public Time getArrival() {
		return this.departure.plusMinutes(durationMinutes);
	}
	
	public void book(TimedTransportChain chain) {
		if (!chainUsesThisConnection(chain)) {
			throw new IllegalArgumentException("The given transport chain " + chain + "does not seem to use this connection: " + this.toString());
		}

		if (!hasFreeCapacity()) {
			throw new IllegalStateException("This connections booking capacity  of " + capacity + "is already reached!");
		}

		bookings.add(chain);
	}

	public boolean hasFreeCapacity() {
		return usedCapacity() < this.capacity;
	}

	public int freeCapacity() {
		return  this.capacity - usedCapacity();
	}

	/**
	 * Gets the number of occupied slots: the connections booked in advance by delivery chains plus
	 * the slots claimed by returning boxes on dispatch.
	 *
	 * @return the number of occupied slots
	 */
	public int usedCapacity() {
		return this.bookings.size() + this.returningClaims;
	}
	
	private boolean chainUsesThisConnection(TimedTransportChain chain) {
		return chain.contains(from) 
			&& chain.contains(to) 
			&& chain.nextHubAfter(from).map(to::equals).orElse(false)
			&& chain.getDeparture(from).equals(departure);
	}

	public String getTag() {
		return VehicleType.TRAM.name() + "_" + from.getId() + "_" + to.getId() + "_"
				+ departure.getDay() + "_" + departure.getHour() + "_" + departure.getMinute()
				+ durationMinutes + "_" + capacity;
	}

}
