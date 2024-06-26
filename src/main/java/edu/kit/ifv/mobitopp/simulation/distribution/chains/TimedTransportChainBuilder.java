package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.CostFunction;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.TransferTimeModel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;

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

	private Connection suggestedFirstConnection;


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

	public TimedTransportChainBuilder suggestFirstConnection(Connection connection) {
		this.suggestedFirstConnection = connection;
		return this;
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
		return useDurationsFromStatsNew(timeTable, impedance, currentTime);

//		DistributionCenter origin = chain.first();
//		int durationSum = 0;
//
//		for (DistributionCenter destination : chain.tail()) {
//			VehicleType vehicle = (chain.isDeliveryDirection()) ? origin.getVehicleType() : destination.getVehicleType();
//
//			double dist =  tripDistance(impedance, origin, destination);
//
//			Time time = (start == null) ? currentTime : start;
//			time = time.plusMinutes(durationSum);
//
//			if (time.isBefore(Time.start)) {
//				time = Time.start;
//			}
//
//			int transfer = getTransferTime(origin, destination, time);
//			int dur;
//
//			if (vehicle.equals(VehicleType.TRAM)) {
//				Optional<Connection> maybeConnection = timeTable.getFreeConnectionsOnDay(origin, destination, time).findFirst();
//
//				if (maybeConnection.isEmpty()) {
//					this.valid = false;
//					return this;
//				}
//
//				Connection connection = maybeConnection.get();
//				connections.put(origin, connection);
//
//				dur = connection.getDurationMinutes();
//				fixedDepartureAt(origin, durationSum, connection.getDeparture(), dur, transfer, dist);
//
//			} else {
//				dur = tripDuration(impedance, origin, destination, time, vehicle);
//				setDuration(origin, dur, transfer, dist);
//			}
//
//			durationSum += durations.get(origin) + transfers.get(origin);
//			origin = destination;
//
//		}//TODO for reverse order, sometimes box is picked up, should this be considered? effective travel time of box is accurate but not of vehicle
//
//		return this;
	}

	private TimedTransportChainBuilder useDurationsFromStatsNew(
			TimeTable timeTable,
			ImpedanceIfc impedance,
			Time currentTime
	) {
		Time departure = currentTime.plusMinutes(0);

		double durBeforeFirstTram = 0;

		DistributionCenter origin = chain.first();
		Optional<DistributionCenter> firstTram = Optional.empty();
		for (DistributionCenter destination : chain.tail()) {
			VehicleType vehicle = (chain.isDeliveryDirection()) ? origin.getVehicleType() : destination.getVehicleType();

			if (vehicle.equals(VehicleType.TRAM)) {
				firstTram = Optional.of(origin);
				break;
			}

			durBeforeFirstTram += tripDuration(
					impedance,
					origin,
					destination,
					departure.plusMinutes((int) Math.round(durBeforeFirstTram)),
					origin.getVehicleType()
			);

			durBeforeFirstTram += getTransferTime(origin, destination, departure.plusMinutes((int) Math.round(durBeforeFirstTram)));
			origin = destination;
		}

		if (durBeforeFirstTram < 0) {
			System.err.println("durBeforeFirstTram should not be < 0!!!");
			this.valid = false;
			return this;
		}

		Optional<DistributionCenter> hubAfterFirstTram = firstTram.flatMap(chain::nextHubAfter);

		if(firstTram.isPresent() && hubAfterFirstTram.isPresent()) {

			Time firstTramEarliestDeparture = departure.plusMinutes((int) Math.round(durBeforeFirstTram));

			Optional<Connection> maybeConnection;
			if (
				suggestedFirstConnection != null
				&& suggestedFirstConnection.getDeparture().isAfterOrEqualTo(firstTramEarliestDeparture)
				&& suggestedFirstConnection.hasFreeCapacity()
			) {
				maybeConnection = Optional.of(suggestedFirstConnection);

			} else {
				maybeConnection = timeTable.getFreeConnectionsOnDay(
						firstTram.get(),
						hubAfterFirstTram.get(),
						firstTramEarliestDeparture
				).findFirst();
			}



			if (maybeConnection.isEmpty()) {
				this.valid = false;
				return this;
			}

			Connection connection = maybeConnection.get();
			connections.put(origin, connection);
			if (connection.getDeparture().isBefore(firstTramEarliestDeparture)) {
				System.err.println("connection departure was before earliest departure: " + connection.getDeparture() + " < " + firstTramEarliestDeparture);
				this.valid = false;
				return this;
			}

			departure = connection.getDeparture().minusMinutes((int) Math.ceil(durBeforeFirstTram));
			start = departure;
		}

		DistributionCenter predecessor = null;

		Time time  = departure;
		origin = chain.first();
		for (DistributionCenter destination: chain.tail()) {
			VehicleType vehicle = (chain.isDeliveryDirection()) ? origin.getVehicleType() : destination.getVehicleType();

			this.departures.put(origin, time);

			double distance = tripDistance(impedance, origin, destination);
			this.distances.put(origin, distance);

			double duration;
			if (vehicle.equals(VehicleType.TRAM)) {
				Optional<Connection> maybeConnection = timeTable.getFreeConnectionsOnDay(
						origin,
						destination,
						time
				).findFirst();

				if (maybeConnection.isEmpty()) {
					this.valid = false;
					return this;
				}

				Connection connection = maybeConnection.get();
				connections.put(origin, connection);
				duration = connection.getDurationMinutes();

				int wait = Math.max(0, connection.getDeparture().differenceTo(time).toMinutes());
				time = connection.getDeparture();
				if (predecessor != null) {
					int transferUpdate = transfers.get(predecessor) + wait;
					transfers.put(predecessor, transferUpdate);
				}

			} else {
				duration = tripDuration(impedance, origin, destination, time, vehicle);
			}

			this.durations.put(origin, (int) Math.round(duration));

			time = time.plusMinutes((int) Math.round(duration));

			double transfer = getTransferTime(origin, destination, time);
			this.transfers.put(origin, (int) Math.round(transfer));

			time = time.plusMinutes((int) Math.round(transfer));
			predecessor = origin;
			origin = destination;

		}




		return this;
	}

	private int getTransferTime(DistributionCenter origin, DistributionCenter destination, Time time) {
		return transferTime.estimateTransferTimeMinutes(destination, origin.getVehicleType(),
				destination.getVehicleType(), time);
	}

	private int tripDuration(ImpedanceIfc impedance, DistributionCenter origin, DistributionCenter destination,
			Time time, VehicleType vehicle) {
		if (time.isBefore(Time.start)) {
			return Math.round(impedance.getTravelTime(origin.getZone().getId(), destination.getZone().getId(),
					vehicle.getMode(), Time.start));
		}
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

	public static ConnectionChainsFactory asConnectionsChainFactory(
			CostFunction costFunction,
			TransferTimeModel transferTime,
			TimeTable timeTable,
			ImpedanceIfc impedance
	) {

		return (chain, time) -> {


			Optional<Pair<DistributionCenter, DistributionCenter>> firstTramLeg =
					chain.legsOfType(VehicleType.TRAM).stream().findFirst();

			if (firstTramLeg.isPresent()) {
				DistributionCenter from = firstTramLeg.get().getFirst();
				DistributionCenter to = firstTramLeg.get().getSecond();

				return timeTable.getFreeConnectionsOnDay(from, to, time).map(c ->

					new TimedTransportChainBuilder(chain, costFunction, transferTime).suggestFirstConnection(c)
							.useDurationsFromStats(timeTable, impedance, time)
							.defaultDeparture(time)
							.build()

				).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
			}

			return List.of();
		};

	}

}
