package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.time.Time;

public class AfternoonPickupDispatch implements DispatchStrategy {
	
	@Override
	public Optional<PlannedDeliveryTour> canDispatch(Collection<PlannedDeliveryTour> tours, Fleet fleet, Time time) {
		
		if ( !isInDispatchHours(time) || isSunday(time) || isFleetAbsent(fleet) || noTourPlanned(tours) ) {
			return Optional.empty();
		}
		
		return tours.stream()
					.filter(t -> noPickupBeforeAfternoon(t, time))
					.filter(t -> endsBeforeEndOfDeliveryTime(time, t))
					.findFirst();
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
	
	private boolean noTourPlanned(Collection<PlannedDeliveryTour> tours) {
		return tours.isEmpty();
	}
	
	private boolean endsBeforeEndOfDeliveryTime(Time time, PlannedDeliveryTour t) {
		return time.plus(t.getPlannedDuration()).isBefore(time.startOfDay().plusHours(21));
	}
	
	private boolean noPickupBeforeAfternoon(PlannedDeliveryTour tour, Time time) {
		return time.getHour() >= 14 || tour.getPickUpRequests().isEmpty();
	}

}
