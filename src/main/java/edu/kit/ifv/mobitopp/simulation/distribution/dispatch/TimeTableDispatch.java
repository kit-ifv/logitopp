package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeTableDispatch implements DispatchStrategy {
	
	private final TimeTable timeTable;
	private final DispatchStrategy defaultDeliveryDispatch;
	
	public TimeTableDispatch(TimeTable timeTable, DispatchStrategy defaultDeliveryDispatch) {
		this.timeTable = timeTable;
		this.defaultDeliveryDispatch = defaultDeliveryDispatch;
	}

	@Override
	public boolean canDispatch(PlannedTour tour, DistributionCenter origin, Time time) {
		
		if (useTimeTable(tour, origin)) {
			DistributionCenter destination = tour.nextHub().get();
			
			Optional<Connection> nextConnection = timeTable.getNextConnection(origin, destination, time);
			
			return nextConnection.isPresent() && nextConnection.get().getDeparture().equals(time);
			
			
		} else {
			
			if (tour.isReturning()) {
				DistributionCenter destination = tour.nextHub().get();
				return destination.getFleet().hasAvailableVehicle();			
			}
			
			return defaultDeliveryDispatch.canDispatch(tour, origin, time);
			
		}

	}
	
	private boolean useTimeTable(PlannedTour tour, DistributionCenter origin) {

		Optional<DistributionCenter> handler = tour.isReturning() ? tour.nextHub() : Optional.of(origin);

		return handler.filter(distributionCenter -> distributionCenter.getVehicleType() == VehicleType.TRAM).isPresent();

	}

}
