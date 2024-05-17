package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeWindowDispatchStrategy implements DispatchStrategy {

	@Override
	public boolean canDispatch(PlannedTour tour, DistributionCenter origin, Time time) {

		boolean inTimeWindow;

		if (tour.usesTram()) {
			inTimeWindow = isInTramDispatchHours(time);
		} else {
			inTimeWindow = isInDispatchHours(time);
		}
		
		if (!inTimeWindow || isSunday(time) || isFleetAbsent(origin.getFleet()) ) {
			return false;
		}

		return endsBeforeEndOfDeliveryTime(time, tour);
	}
	

	private boolean isInDispatchHours(Time time) {
		int hour=time.getHour();
		return 7 <= hour && hour <= 18;
	}

	private boolean isInTramDispatchHours(Time time) {
		int hour=time.getHour();
		return 4 <= hour && hour <= 18;
	}

	private boolean isSunday(Time time) {
		return time.weekDay().equals(SUNDAY);
	}

	private boolean isFleetAbsent(Fleet fleet) {
		return fleet.getAvailableVehicles().isEmpty();
	}
		
	private boolean endsBeforeEndOfDeliveryTime(Time time, PlannedTour t) {
		return time.plus(t.getPlannedDuration()).isBefore(time.startOfDay().plusHours(21));
	}

}
