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
	private final Map<DistributionCenter, Integer> durations;
	private final Map<DistributionCenter, Integer> transfers;
	private final Map<DistributionCenter, Time> departures;
	private Time start;

	public TimedTransportChainBuilder(TransportChain chain) {
		this.chain = chain;
		this.durations = new LinkedHashMap<>();
		this.transfers = new LinkedHashMap<>();
		this.departures = new LinkedHashMap<>();
		durations.put(chain.last(), 0);//TODO default value
		transfers.put(chain.last(), 0);//TODO default value
	}
	
	public TimedTransportChainBuilder setDuration(DistributionCenter hub, int durMinutes, int transferMinutes) {
		this.durations.put(hub, durMinutes);
		this.transfers.put(hub, transferMinutes);
		return this;
	}
	
	public TimedTransportChainBuilder fixedDepartureAt(DistributionCenter hub, Time departure, int durMinutes, int transferMinutes) {
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
			transfers.compute(predecessor, (key, val) -> val + waitMin);
		}
		
		this.setDuration(hub, durMinutes, transferMinutes);
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
			
			int transfer = stats.getTransferTime(origin, destination);
			int dur;
			if (origin.getVehicleType().equals(VehicleType.TRAM)) {
				Connection connection = stats.nextReachableConnection(destination, origin, time).get(); //TODO what if no connection is found, chain should not exist in that case
				dur = connection.getDurationMinutes();
				fixedDepartureAt(origin, connection.getDeparture(), dur, transfer);
				
			} else {
				dur = Math.round(stats.getTravelTime(origin, destination, time));
				setDuration(origin, dur, transfer);
			}
			
			durationSum += dur + transfer;
		}
		
		
		return this;
	}
	
	public int getPrefixDuration(DistributionCenter hub) {
		List<DistributionCenter> prefix = getPrefixHubs(hub);
		return getPrefixDuration(prefix, hub);
	}
	
	private int getPrefixDuration(List<DistributionCenter> prefix, DistributionCenter hub) {		
		if (!allHubsHaveDuration(prefix)) {
			String msg = "Could not set fixed departure at hub '" + hub + "', since not all prefix hubs have a duration:\n";
			msg += prefix.stream().map(h -> "  " + h + " = " + durations.getOrDefault(h, -1) + " / " + transfers.getOrDefault(h, -1)).collect(Collectors.joining(", "));
			throw new IllegalStateException(msg);
		}
		
		int prefixDur = prefix.stream().mapToInt(durations::get).sum()
					  + prefix.stream().mapToInt(transfers::get).sum();
		
		return prefixDur;
	}

	private boolean allHubsHaveDuration(List<DistributionCenter> prefix) {
		return prefix.stream().allMatch(h -> durations.containsKey(h) && transfers.containsKey(h));
	}

	private List<DistributionCenter> getPrefixHubs(DistributionCenter hub) {
		List<DistributionCenter> prefix = chain.getHubs().stream().takeWhile(h -> !h.equals(hub)).collect(Collectors.toList());
		return prefix;
	}
	
	
	
	public TimedTransportChain build() {
		if (start== null) {
			throw new IllegalStateException("Cannot build TimedTransportChain since start departure is not set.");
		}
		
//		Map<DistributionCenter, Time> arrivals = new LinkedHashMap<>();
		
		Time time = start.plusMinutes(0);
		for (DistributionCenter hub: chain.getHubs()) {
			departures.put(hub, time);
			time = time.plusMinutes(durations.get(hub));
			
//			arrivals.put(hub, time);
			time = time.plusMinutes(transfers.get(hub));		   
		}
		
		return new TimedTransportChain(chain, departures, durations);
	}

}
