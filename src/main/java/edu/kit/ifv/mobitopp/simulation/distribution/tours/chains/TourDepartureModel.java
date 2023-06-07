package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.time.Time;

public interface TourDepartureModel {
	
	public Collection<TimedTransportChain> getDepartures(DistributionCenter center, Time currentTime);

}
