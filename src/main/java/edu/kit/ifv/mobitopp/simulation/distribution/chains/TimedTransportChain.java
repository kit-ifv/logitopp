package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;

public class TimedTransportChain extends TransportChain {

	private final Map<DistributionCenter, Time> departures;
	
	public TimedTransportChain(TransportChain chain, Map<DistributionCenter, Time> departures) {
		super(chain.getHubs(), chain.isDeliveryDirection());
		this.departures = departures;
	}
	
	public Time getDeparture(DistributionCenter hub) {
		return departures.get(hub); //TODO check if hub exist in chain
	}

}
