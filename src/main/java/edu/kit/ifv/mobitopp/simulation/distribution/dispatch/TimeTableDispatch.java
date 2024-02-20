package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeTableDispatch implements DispatchStrategy {
	
	private final TimeTable timeTable;
	private final boolean isInputHub;	
	
	public TimeTableDispatch(TimeTable timeTable, boolean isInputHub) {
		this.timeTable = timeTable;
		this.isInputHub = isInputHub;
	}

	@Override
	public boolean canDispatch(PlannedTour tour, DistributionCenter origin, Time time) {
		
		if (useTimeTable(tour)) {
			DistributionCenter destination = tour.nextHub().get();
			
			Optional<Connection> nextConnection = timeTable.getNextConnection(origin, destination, time);
			
			return nextConnection.isPresent() && nextConnection.get().getDeparture().equals(time);
			
			
		} else {
			
			if (tour.isReturning()) {
				DistributionCenter destination = tour.nextHub().get();
				return destination.getFleet().hasAvailableVehicle();			
			}
			
			return origin.getFleet().hasAvailableVehicle();
			
		}

	}
	
	private boolean useTimeTable(PlannedTour tour) {
		return isInputHub != tour.isReturning();
	}
	
	

}
