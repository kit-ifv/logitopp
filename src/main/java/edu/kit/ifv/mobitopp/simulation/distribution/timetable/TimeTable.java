package edu.kit.ifv.mobitopp.simulation.distribution.timetable;

import static java.util.Comparator.comparing;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
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
					   .sorted(comparing(Connection::getDeparture))
					   .findFirst();
		
		return connection;
	}

	public Stream<Connection> getConnections(DistributionCenter origin, DistributionCenter destination) {
		return connections.stream()
						  .filter(c -> c.getFrom().equals(origin))
						  .filter(c -> c.getTo().equals(destination))
						  .sorted(comparing(Connection::getDeparture));
	}
	
	public Stream<Connection> getConnectionsOnDay(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getConnections(origin, destination)
				.filter(c -> c.getDeparture().getDay() == currentTime.getDay())
				.filter(c -> c.getDeparture().isAfterOrEqualTo(currentTime));
	}

	public Stream<Connection> getFreeConnections(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getConnections(origin, destination)
				.filter(c -> c.getDeparture().isAfterOrEqualTo(currentTime))
				.filter(Connection::hasFreeCapacity)
				.sorted(Comparator.comparing(Connection::getDeparture));
	}
	
	public Stream<Connection> getFreeConnectionsOnDay(DistributionCenter origin, DistributionCenter destination, Time currentTime) {
		return getConnectionsOnDay(origin, destination, currentTime)
				.filter(Connection::hasFreeCapacity);
	}
	
	@Override
	public String toString() {
		return "{" + connections.stream().map(Object::toString).collect(Collectors.joining("|\n")) + "}";
	}
}
