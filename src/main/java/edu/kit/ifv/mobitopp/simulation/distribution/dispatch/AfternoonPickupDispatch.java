package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

public class AfternoonPickupDispatch implements DispatchStrategy {
	
	@Override
	public boolean canDispatch(PlannedTour tour, DistributionCenter origin, Time time) {
		
		if ( !isInDispatchHours(time) || isSunday(time) || isFleetAbsent(origin.getFleet())  ) {
			return false;
		}
		
		return noPickupBeforeAfternoon(tour, time);
	}
	
	private boolean isInDispatchHours(Time time) {
		int hour=time.getHour();
		return 8 <= hour && hour <= 18;
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
	
	private boolean noPickupBeforeAfternoon(PlannedTour tour, Time time) {
		return time.getHour() >= 14 || tour.getPickUpRequests().isEmpty();
	}

}
