package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

public class TimedTransportChain extends TransportChain {
	private static int idCount = 0;
	
	@Getter
	private final int id;
	private final Map<DistributionCenter, Time> departures;
	private final Map<DistributionCenter, Integer> durations;
	@Getter private final double distance;
	@Getter private final double cost;
	@Getter private final List<Connection> connections;

	public TimedTransportChain(TransportChain chain, Map<DistributionCenter, Time> departures,
			Map<DistributionCenter, Integer> durations, List<Connection> connections,
			double distance, double cost) {
		this(idCount++, chain.getHubs(), chain.isDeliveryDirection(), departures, durations, connections, distance, cost);
	}
	
	private TimedTransportChain(int id, List<DistributionCenter> hubs, boolean isDeliveryDirection, Map<DistributionCenter, Time> departures,
			Map<DistributionCenter, Integer> durations, List<Connection> connections,
			double distance, double cost) {
		super(hubs, isDeliveryDirection);
		this.id = id;
		this.departures = departures;
		this.durations = durations;
		this.distance = distance;
		this.cost = cost;
		this.connections = new ArrayList<>(connections);
	}

	public Time getDeparture(DistributionCenter hub) {
		return departures.get(hub); // TODO check if hub exist in chain
	}

	public int getDuration(DistributionCenter hub) {
		return durations.get(hub); // TODO check if hub exist in chain
	}

	public Time getArrival(DistributionCenter hub) {
		int minutes = durations.get(hub);
		return departures.get(hub).plusMinutes(minutes); // TODO check if hub exist in chain
	}

	public Time getLastMileDeparture() {
		return departures.get(last()); // TODO check if hub exist in chain
	}

	public Time getFirstMileDeparture() {
		return departures.get(first()); // TODO check if hub exist in chain
	}
	
	public int getTotalDuration() {
		return getLastMileDeparture().differenceTo(getFirstMileDeparture()).toMinutes();
	}

	@Override
	public String toString() {
		return getHubs().stream().map(h -> h + " dep: " + departures.get(h) + " +(" + durations.get(h) + " min)\n")
				.collect(joining());
	}

	public void bookConnections() {
		this.connections.forEach(c -> c.book(this));
	}
	
	public boolean canBookConnections() {
		return this.connections.stream().allMatch(Connection::hasFreeCapacity);
	}
	
	public TimedTransportChain copy() {
		return new TimedTransportChain(id, hubs, deliveryDirection, departures, durations, connections, distance, cost);
	}
	
	public TimedTransportChain getTimedTail() {
		if (hubs.size() <= 1) { throw new IllegalArgumentException("Cannot create timed tail of chain with size <= 1"); }
		
		TimedTransportChain copy = new TimedTransportChain(
				-Math.abs(id), 
				hubs, 
				deliveryDirection, 
				departures, 
				durations, 
				connections, 
				-1.0, //TODO cannot compute cost/distance when dropping first hub
				-1.0
			);
		
		copy.hubs.remove(first());
		copy.departures.remove(first());
		copy.durations.remove(first());
		
		for (Connection c : copy.connections) {
			if (c.getFrom().equals(first())) {
				copy.connections.remove(c);
			}
		}
		
		return copy;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {return true;}
		if (obj == null) {return false;}
	    if (!(obj instanceof TimedTransportChain)) {return false;}
	    
	    TimedTransportChain other = (TimedTransportChain) obj;
	    return this.id == other.id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

}
