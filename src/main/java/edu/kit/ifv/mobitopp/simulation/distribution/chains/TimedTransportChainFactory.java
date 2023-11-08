package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import edu.kit.ifv.mobitopp.time.Time;

public interface TimedTransportChainFactory {
	
	public TimedTransportChain create(TransportChain chain, Time time);

}
