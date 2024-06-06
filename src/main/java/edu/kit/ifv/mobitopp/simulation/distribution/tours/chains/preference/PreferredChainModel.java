package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public interface PreferredChainModel {
		
	public TransportPreferences selectPreference(IParcel parcel, Collection<TimedTransportChain> choiceSet, double randomNumber, Time time);

	public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet);
	
}
