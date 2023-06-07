package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.collections.Pair;

public interface TransportChainDemandModel {
	
	public List<Pair<TimedTransportChain, Double>> select(Collection<TimedTransportChain> choiceSet, IParcel parcel);

}
