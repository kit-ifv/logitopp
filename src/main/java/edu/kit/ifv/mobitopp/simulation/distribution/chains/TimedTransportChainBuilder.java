package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.CostFunction;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.TransferTimeModel;
import edu.kit.ifv.mobitopp.time.Time;

public class TimedTransportChainBuilder {
	private final CostFunction costFunction;
	private final TransferTimeModel transferTime;

	private final TransportChain chain;
	private final Map<DistributionCenter, Integer> durations;
	private final Map<DistributionCenter, Integer> transfers;
	private final Map<DistributionCenter, Time> departures;
	private final Map<DistributionCenter, Double> distances;

	private final Map<DistributionCenter, Connection> connections;

	private Time start;
	
	private boolean valid = true;


	public TimedTransportChainBuilder(TransportChain chain, CostFunction costFunction, TransferTimeModel transferTime) {
		this.costFunction = costFunction;
		this.transferTime = transferTime;

		this.chain = chain;
		this.durations = new LinkedHashMap<>();
		this.transfers = new LinkedHashMap<>();
		this.departures = new LinkedHashMap<>();
		this.distances = new LinkedHashMap<>();
		this.connections = new LinkedHashMap<>();
		durations.put(chain.last(), 0);// TODO default value
		transfers.put(chain.last(), 0);// TODO default value

	}

	public TimedTransportChainBuilder setDuration(DistributionCenter hub, int durMinutes, int transferMinutes, double distance) {
		this.durations.put(hub, Math.max(1, durMinutes));
		this.transfers.put(hub, Math.max(1, transferMinutes));
		this.distances.put(hub, Math.max(0.001, distance));
		return this;
	}

	public TimedTransportChainBuilder fixedDepartureAt(
			DistributionCenter hub,
			int prefixDur,
			Time departure,
			int durMinutes,
			int transferMinutes,
			double distance
	) {
		List<DistributionCenter> prefix = getPrefixHubs(hub);
		//int prefixDur = getPrefixDuration(prefix, hub);

		if (start == null) {
			// set start time with given departure as fixed pole
			start = departure.minusMinutes(prefixDur);

		} else {
			// add waiting time between arrival from last leg and given departure
			int waitMin = departure.differenceTo(start).toMinutes() - prefixDur;

			if (waitMin < 0) {
				throw new IllegalArgumentException("The given departure " + departure
						+ " is before the first possible start " + start.plusMinutes(prefixDur) + " = " + start + " + "
						+ prefixDur + " min (delta=" + waitMin + ").");
			}

			DistributionCenter predecessor = prefix.get(prefix.size() - 1);
			transfers.compute(predecessor, (key, val) -> val + waitMin);
		}

		this.setDuration(hub, durMinutes, transferMinutes, distance);
		return this;
	}

	public TimedTransportChainBuilder defaultDeparture(Time defaultDeparture) {
		if (this.start == null) {
			this.start = defaultDeparture;
		}
		return this;
	}

	public TimedTransportChainBuilder useDurationsFromStats(
			TimeTable timeTable,
			ImpedanceIfc impedance,
			Time currentTime
	) {// TODO error on legs before first tram leg with fixed departure as start dep is
								// approximated via current time

		DistributionCenter origin = chain.first();
		int durationSum = 0;

		for (DistributionCenter destination : chain.tail()) {
			VehicleType vehicle = (chain.isDeliveryDirection()) ? origin.getVehicleType() : destination.getVehicleType();

			double dist =  tripDistance(impedance, origin, destination);

			Time time = (start == null) ? currentTime : start;
			time = time.plusMinutes(durationSum);

			if (time.isBefore(Time.start)) {
				time = Time.start;
			}

			int transfer = getTransferTime(origin, destination, time);
			int dur;

			if (vehicle.equals(VehicleType.TRAM)) {
				Optional<Connection> maybeConnection = timeTable.getFreeConnectionsOnDay(origin, destination, time).findFirst();
				
				if (maybeConnection.isEmpty()) {
					this.valid = false;
					return this;
				}
				
				Connection connection = maybeConnection.get();
				connections.put(origin, connection);

				dur = connection.getDurationMinutes();
				fixedDepartureAt(origin, durationSum, connection.getDeparture(), dur, transfer, dist);

			} else {
				dur = tripDuration(impedance, origin, destination, time, vehicle);
				setDuration(origin, dur, transfer, dist);
			}

			durationSum += durations.get(origin) + transfers.get(origin);
			origin = destination;
			
		}//TODO for reverse order, sometimes box is picked up, should this be considered? effective travel time of box is accurate but not of vehicle

		return this;
	}

	private int getTransferTime(DistributionCenter origin, DistributionCenter destination, Time time) {
		return transferTime.estimateTransferTimeMinutes(destination, origin.getVehicleType(),
				destination.getVehicleType(), time);
	}

	private int tripDuration(ImpedanceIfc impedance, DistributionCenter origin, DistributionCenter destination,
			Time time, VehicleType vehicle) {
		return Math.round(impedance.getTravelTime(origin.getZone().getId(), destination.getZone().getId(),
				vehicle.getMode(), time));
	}

	private double tripDistance(ImpedanceIfc impedance, DistributionCenter origin, DistributionCenter destination) {
		return impedance.getDistance(origin.getZone().getId(), destination.getZone().getId());
	}

	public int getPrefixDuration(DistributionCenter hub) {
		List<DistributionCenter> prefix = getPrefixHubs(hub);
		return getPrefixDuration(prefix, hub);
	}

	private int getPrefixDuration(List<DistributionCenter> prefix, DistributionCenter hub) {
		if (!allHubsHaveDuration(prefix)) {
			String msg = "Could not set fixed departure at hub '" + hub
					+ "', since not all prefix hubs have a duration:\n";
			msg += prefix.stream()
					.map(h -> "  " + h + " = " + durations.getOrDefault(h, -1) + " / " + transfers.getOrDefault(h, -1))
					.collect(Collectors.joining(", "));
			throw new IllegalStateException(msg);
		}

		return prefix.stream().mapToInt(durations::get).sum()
						+ prefix.stream().mapToInt(transfers::get).sum();
	}

	private boolean allHubsHaveDuration(List<DistributionCenter> prefix) {
		return prefix.stream().allMatch(h -> durations.containsKey(h) && transfers.containsKey(h));
	}

	private List<DistributionCenter> getPrefixHubs(DistributionCenter hub) {
		List<DistributionCenter> prefix = chain.getHubs().stream().takeWhile(h -> !h.equals(hub))
				.collect(Collectors.toList());
		return prefix;
	}

	public Optional<TimedTransportChain> build() {
		if (!valid) {
			return Optional.empty();
		}
		
		if (start== null) {
			throw new IllegalStateException("Cannot build TimedTransportChain since start departure is not set.");
		}
		
		Time time = start.plusMinutes(0);
		for (DistributionCenter hub: chain.getHubs()) {

			if (connections.containsKey(hub)) {
				Connection con = connections.get(hub);
				departures.put(hub, con.getDeparture());
				time = time.plusMinutes(con.getDurationMinutes());

			} else {
				departures.put(hub, time);
				time = time.plusMinutes(durations.get(hub));
			}

			time = time.plusMinutes(transfers.get(hub));
		}
		
		double cost = 0.0;
		double distance = 0.0;
		DistributionCenter origin = chain.first();
		for (DistributionCenter destination : chain.tail()) {
			cost += costFunction.estimateCost(
					origin,
					destination,
					departures.get(origin),
					origin.getVehicleType(),
					durations.get(origin),
					transfers.get(origin),
					distances.get(origin)
			);
			
			distance += distances.get(origin);
			
			origin = destination;
		}
		
		return Optional.of(new TimedTransportChain(chain, departures, durations, new ArrayList<>(connections.values()), distance, cost));
	}
	
	
	public static TimedTransportChainFactory asFactory(CostFunction costFunction, TransferTimeModel transferTime, TimeTable timeTable, ImpedanceIfc impedance) {
		
		return (chain, time) 
			-> new TimedTransportChainBuilder(chain, costFunction, transferTime)
					.useDurationsFromStats(timeTable, impedance, time)
					.defaultDeparture(time)
					.build();
	}

}
