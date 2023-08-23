package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public interface PreferredChainModel {
		
	public TransportPreference selectPreference(IParcel parcel, Collection<TimedTransportChain> choiceSet, double randomNumber);

	public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet);
	
}
