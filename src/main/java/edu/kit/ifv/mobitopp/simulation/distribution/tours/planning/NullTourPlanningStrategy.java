package edu.kit.ifv.mobitopp.simulation.distribution.tours.planning;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class NullTourPlanningStrategy implements TourPlanningStrategy {

	@Override
	public List<PlannedTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet,
			Time time) {
		throw new UnsupportedOperationException("NullTourPlanningStrategy.planTours " + fleet.getDistributionCenter() + " should never be called!");
	}

	@Override
	public boolean shouldReplanTours(DistributionCenter center, Time time) {
		return false;
	}

}
