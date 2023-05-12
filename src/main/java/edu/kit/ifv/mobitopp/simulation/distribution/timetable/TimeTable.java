package edu.kit.ifv.mobitopp.simulation.distribution.timetable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeTable {
	
	private final List<Connection> connections;
	
	public TimeTable(Collection<Connection> connections) {
		this.connections = new ArrayList<>(connections);
	}

	public int getNextDuration(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getNextConnection(origin, destination, currentTime).get().getDurationMinutes();
	}
	
	public Time getNextDeparture(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getNextConnection(origin, destination, currentTime).get().getDeparture();
	}
	
	public boolean hasNextConnection(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getNextConnection(origin, destination, currentTime).isPresent();
	}
	
	public Optional<Connection> getNextConnection(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		
		Optional<Connection> connection = 
			connections.stream()
					   .filter(c -> c.getFrom().equals(origin))
					   .filter(c -> c.getTo().equals(destination))
					   .filter(c -> c.getDeparture().isAfterOrEqualTo(currentTime))
					   .sorted(Comparator.comparing(Connection::getDeparture))
					   .findFirst();
		
		return connection;
	}
}
