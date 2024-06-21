package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import edu.kit.ifv.mobitopp.time.Time;

import java.util.List;

public interface ConnectionChainsFactory {
    public List<TimedTransportChain> create(TransportChain chain, Time time);

}
