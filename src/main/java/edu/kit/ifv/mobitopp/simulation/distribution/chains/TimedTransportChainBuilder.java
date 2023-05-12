package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class TimedTransportChainBuilder {
	
	private final TransportChain chain;
	private final Map<DistributionCenter, Integer> duration;
	private final Map<DistributionCenter, Time> departures;
	private Time start;
	
	public TimedTransportChainBuilder(TransportChain chain) {
		this.chain = chain;
		this.duration = new LinkedHashMap<>();
		this.departures = new LinkedHashMap<>();
		duration.put(chain.last(), 0);//TODO default value
	}
	
	public TimedTransportChainBuilder setDuration(DistributionCenter hub, int durMinutes) {
		this.duration.put(hub, durMinutes);
		return this;
	}
	
	public TimedTransportChainBuilder fixedDepartureAt(DistributionCenter hub, Time departure, int durMinutes) {
		List<DistributionCenter> prefix = getPrefixHubs(hub);		
		int prefixDur = getPrefixDuration(prefix, hub);
		
		if (start == null) {
			// set start time with gievn departure as fixed pole
			start = departure.minusMinutes(prefixDur);
			
		} else {
			// add waiting time between arrival from last leg and given departure
			int waitMin = departure.differenceTo(start).toMinutes() - prefixDur;
			
			if (waitMin < 0) {
				throw new IllegalArgumentException("The given departure" + departure +" is before the first possible start " 
						+ start.plusMinutes(prefixDur) + " = " + start + " + " + prefixDur + " min (delta=" + waitMin + ").");
			}
			
			DistributionCenter predecessor = prefix.get(prefix.size()-1);
			duration.compute(predecessor, (key, val) -> val + waitMin);
		}
		
		this.setDuration(hub, durMinutes);
		return this;
	}
	
	public TimedTransportChainBuilder defaultDeparture(Time defaultDeparture) {
		if (this.start == null) {
			this.start = defaultDeparture;
		}
		return this;
	}
	
	public TimedTransportChainBuilder useDurationsFromStats(TransportChainStatistics stats, Time currentTime) {//TODO error on legs before first tram leg with fixed departure as start dep is approximated via current time
		DistributionCenter origin = chain.first();
		int durationSum = 0;
		for (DistributionCenter destination : chain.tail()) {
			
			Time time = (start == null) ? currentTime : start;
			time = time.plusMinutes(durationSum);	
			int dur = stats.getTransferTime(origin);
			
			if (origin.getFleet().getVehicleType().equals(VehicleType.TRAM)) {
				Connection connection = stats.nextReachableConnection(destination, origin, time).get(); //TODO what if no connection is found, chain should not exist in that case
				dur += connection.getDurationMinutes();
				fixedDepartureAt(origin, connection.getDeparture(), dur);
				
			} else {
				dur += Math.round(stats.getTravelTime(origin, destination, time));
				setDuration(origin, dur);
			}
			
			durationSum += dur;
		}
		
		
		return this;
	}
	
	public int getPrefixDuration(DistributionCenter hub) {
		List<DistributionCenter> prefix = getPrefixHubs(hub);
		return getPrefixDuration(prefix, hub);
	}
	
	private int getPrefixDuration(List<DistributionCenter> prefix, DistributionCenter hub) {		
		if (!prefix.stream().allMatch(duration::containsKey)) {
			String msg = "Could not set fixed departure at hub '" + hub + "', since not all prefix hubs have a duration:\n";
			msg += prefix.stream().map(h -> "  " + h + " = " + duration.getOrDefault(h, -1)).collect(Collectors.joining(", "));
			throw new IllegalStateException(msg);
		}
		
		int prefixDur = prefix.stream().mapToInt(duration::get).sum(); //TODO transfer time?
		
		return prefixDur;
	}

	private List<DistributionCenter> getPrefixHubs(DistributionCenter hub) {
		List<DistributionCenter> prefix = chain.getHubs().stream().takeWhile(h -> !h.equals(hub)).collect(Collectors.toList());
		return prefix;
	}
	
	
	
	public TimedTransportChain build() {
		if (start== null) {
			throw new IllegalStateException("Cannot build TimedTransportChain since start departure is not set.");
		}
		
		Time time = start.plusMinutes(0);
		for (DistributionCenter hub: chain.getHubs()) {
			departures.put(hub, time);
			time = time.plusMinutes(duration.get(hub));
		}
		
		return new TimedTransportChain(chain, departures);
	}

}
