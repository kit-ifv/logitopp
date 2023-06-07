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

	private final Map<DistributionCenter, Time> departures;
	private final Map<DistributionCenter, Integer> durations;
	@Getter private final double distance;
	@Getter private final double cost;
	private final List<Connection> connections;

	public TimedTransportChain(TransportChain chain, Map<DistributionCenter, Time> departures,
			Map<DistributionCenter, Integer> durations, List<Connection> connections,
			double distance, double cost) {
		super(chain.getHubs(), chain.isDeliveryDirection());
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

}
