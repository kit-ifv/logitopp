package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import static java.util.stream.Collectors.joining;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;

public class TimedTransportChain extends TransportChain {

	private final Map<DistributionCenter, Time> departures;
	private final Map<DistributionCenter, Integer> durations;
	
	public TimedTransportChain(TransportChain chain, Map<DistributionCenter, Time> departures, Map<DistributionCenter, Integer> durations) {
		super(chain.getHubs(), chain.isDeliveryDirection());
		this.departures = departures;
		this.durations = durations;
	}
	
	public Time getDeparture(DistributionCenter hub) {
		return departures.get(hub); //TODO check if hub exist in chain
	}
	
	public int getDuration(DistributionCenter hub) {
		return durations.get(hub); //TODO check if hub exist in chain
	}
	
	public Time getArrival(DistributionCenter hub) {
		int minutes = durations.get(hub);
		return departures.get(hub).plusMinutes(minutes); //TODO check if hub exist in chain
	}
	
	@Override
	public String toString() {
		return getHubs().stream().map(h -> h + " dep: " + departures.get(h) + " +(" + durations.get(h) +" min)\n").collect(joining());
	}

}
