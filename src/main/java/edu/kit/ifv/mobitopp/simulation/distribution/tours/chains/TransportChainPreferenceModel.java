package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public interface TransportChainPreferenceModel {
	
	public Map<TransportChain,Double> computePreferences(IParcel parcel, Collection<TransportChain> choiceSet, Time currentTime);

	public TransportChain selectPreference(IParcel parcel, Collection<TransportChain> choiceSet, Time currentTime, double randomNumber);
	
	public Collection<TransportChain> filterChoiceSet(IParcel parcel, Collection<TransportChain> choiceSet, Time time);
}
