package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public interface PreferedChainModel {
		
	public TransportPreference selectPreference(IParcel parcel, Collection<TransportChain> choiceSet, Time currentTime, double randomNumber);
	
	public Collection<TimedTransportChain> filterChoiceSet(IParcel parcel, Collection<TimedTransportChain> choiceSet, Time time);
	
}
