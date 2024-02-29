package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.Optional;

import edu.kit.ifv.mobitopp.time.Time;

public interface TimedTransportChainFactory {
	
	public Optional<TimedTransportChain> create(TransportChain chain, Time time);

}
