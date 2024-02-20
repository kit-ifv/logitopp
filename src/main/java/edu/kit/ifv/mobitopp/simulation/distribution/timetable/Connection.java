package edu.kit.ifv.mobitopp.simulation.distribution.timetable;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
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
	
	public Connection(DistributionCenter from, DistributionCenter to, Time departure, int durationMinutes, int capacity) {
		assert from != null;
		assert to != null;
		
		this.from = from;
		this.to = to;
		this.departure = departure;
		this.durationMinutes = durationMinutes;
		this.capacity = capacity;
		
		this.bookings = new ArrayList<>();
	}
	
	public Time getArrival() {
		return this.departure.plusMinutes(durationMinutes);
	}
	
	public void book(TimedTransportChain chain) {
		if (!chainUsesThisConnection(chain)) {
			throw new IllegalArgumentException("The given transport chain " + chain + "does not seem to use this connection: " + this.toString());
		}
		
		if (this.bookings.size() >= capacity) {
			throw new IllegalStateException("This connections booking capacity  of " + capacity + "is already reached!");
		}
		
		bookings.add(chain);
	}
	
	public boolean hasFreeCapacity() {
		return this.bookings.size() < this.capacity;
	}
	
	public int freeCapacity() {
		return  this.capacity - this.bookings.size();
	}
	
	private boolean chainUsesThisConnection(TimedTransportChain chain) {
		return chain.contains(from) 
			&& chain.contains(to) 
			&& chain.nextHubAfter(from).map(to::equals).orElse(false)
			&& chain.getDeparture(from).equals(departure);
	}
}
