package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class CapacityCoordinator implements Hook {
	
	private final Collection<DistributionCenter> distributionCenters;
	
	public CapacityCoordinator(Collection<DistributionCenter> distributionCenters) {
		this.distributionCenters = distributionCenters;
	}

	@Override
	public void process(Time date) {
		if (date.equals(date.startOfDay().plusHours(6))) {
			coordinateCapacityAssignment(date);
		}
	}

	private void coordinateCapacityAssignment(Time date) {
		Time earliestDeparture = date.startOfDay().plusHours(7).plusMinutes(30);

		Map<Connection, Collection<TimedTransportChain>> capacityRequests;
		for (DistributionCenter dc : distributionCenters) {
			Map<TimedTransportChain, Integer> demand = getRequests(dc, earliestDeparture);
		}
	}

	private Map<TimedTransportChain, Integer> getRequests(DistributionCenter dc, Time earliestDeparture) {
		// TODO Auto-generated method stub
		return null;
	}

}
