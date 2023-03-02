package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeWindowDispatchStrategy implements DispatchStrategy {
	
	@Override
	public boolean canDispatch(DistributionCenter center, Time time) {
		if (time.weekDay().equals(SUNDAY)) {return false;}
		
		int hour=time.getHour();
		Collection<PlannedDeliveryTour> plannedTours = center.getStorage().getPlannedTours();
		Collection<DeliveryVehicle> vehicles = center.getFleet().getVehicles();
		
		return !plannedTours.isEmpty() 
			&& !vehicles.isEmpty() 
			&& 8 <= hour && hour <= 18 
			&& plannedTours.stream().anyMatch(t -> endsBeforeEndOfDeliveryTime(time, t));
	}

	private boolean endsBeforeEndOfDeliveryTime(Time time, PlannedDeliveryTour t) {
		return time.plus(t.getPlannedDuration()).getHour() <= 21;
	}

}
