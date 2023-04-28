package edu.kit.ifv.mobitopp.simulation.distribution.timetable;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class Connection {

	private final DistributionCenter from;
	private final DistributionCenter to;
	private final Time departure;
	private final int durationMinutes;
	
	public Connection(DistributionCenter from, DistributionCenter to, Time departure, int durationMinutes) {
		this.from = from;
		this.to = to;
		this.departure = departure;
		this.durationMinutes = durationMinutes;
	}
	
	public Time getArrival() {
		return this.departure.plusMinutes(durationMinutes);
	}
}
