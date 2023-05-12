package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.time.Time;

public interface TourDepartureModel {
	
	public Map<Time, Integer> getLastMileDepartures(TransportChain chain, Time currentTime);

}
