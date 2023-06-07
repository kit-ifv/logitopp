package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChainBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChainStatistics;
import edu.kit.ifv.mobitopp.time.Time;

public class SimpleDepartureModel implements TourDepartureModel {
	
	private final TransportChainStatistics stats;
	
	public SimpleDepartureModel(TransportChainStatistics stats) {
		this.stats = stats;
	}

	@Override
	public Collection<TimedTransportChain> getDepartures(DistributionCenter center, Time currentTime) {
		Time start = earliestDeparture(currentTime);
		
		return center.getRegionalStructure()
					 .getDeliveryChains()
					 .stream()
					 .map(c -> inferTime(start, c))
					 .collect(toList());
	}

	private TimedTransportChain inferTime(Time currentTime, TransportChain chain) {
		return new TimedTransportChainBuilder(chain)
				.useDurationsFromStats(stats, currentTime)
				.defaultDeparture(currentTime)
				.build();
	}

	private static Time earliestDeparture(Time currentTime) {
		return currentTime.startOfDay().plusHours(7).plusMinutes(30);
	}

	

}
