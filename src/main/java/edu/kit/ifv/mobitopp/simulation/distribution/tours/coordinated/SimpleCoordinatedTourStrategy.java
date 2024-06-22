package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import static java.lang.Math.ceil;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryDurationModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.TransferTimeModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference.TransportPreferences;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.planning.TourPlanningStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.box.BoxOnBike;
import edu.kit.ifv.mobitopp.simulation.parcels.box.ParcelBox;
import edu.kit.ifv.mobitopp.simulation.parcels.box.ParcelWithReturnInfo;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.ParcelCluster;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.routing.Tour;
import edu.kit.ifv.mobitopp.util.routing.tsp.TspSolver;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SimpleCoordinatedTourStrategy implements TourPlanningStrategy {
	
	private final ImpedanceIfc impedance;
	private final CapacityCoordinator coordinator;
	private final TspSolver<ParcelCluster> solver;
	private final DeliveryClusteringStrategy clustering;

	private final TourPlanningStrategy lastMilePlanning;
	private final DeliveryDurationModel durationModel;

	private final TransferTimeModel transferTime;

	private final int maxTruckParcels;
	private final int maxBikeParcels;
	
	public SimpleCoordinatedTourStrategy(
			CapacityCoordinator coordinator,
			DeliveryClusteringStrategy clustering,
			ImpedanceIfc impedance,
			DeliveryDurationModel durationModel,
			TspSolver<ParcelCluster> tspSolver,
			TourPlanningStrategy lastMilePlanning,
			TransferTimeModel transferTime,
			int maxTruckParcels, int maxBikeParcels) {
		this.impedance = impedance;
		this.coordinator = coordinator;
		this.solver = tspSolver;
		this.clustering = clustering;
		this.durationModel = durationModel;
		this.lastMilePlanning = lastMilePlanning;
		this.transferTime = transferTime;
		this.maxTruckParcels = maxTruckParcels;
		this.maxBikeParcels = maxBikeParcels;
	}
	
	@Override
	public boolean shouldReplanTours(DistributionCenter center, Time time) {
		return  !(time.weekDay().equals(DayOfWeek.SUNDAY)) && time.equals(time.startOfDay().plusHours(3).plusMinutes(45));
	}
	
	@Override
	public List<PlannedTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet,
			Time time) {
		System.gc();
		
		DistributionCenter dc = fleet.getDistributionCenter();
		ChainAssignment assignment = coordinator.getAssignment(time);
		List<TimedTransportChain> chains = assignment.getChains(dc);
		Map<TimedTransportChain, List<TimedTransportChain>> identicalChains =
				chains.stream().collect(groupingBy(identity()));

		Map<IParcel, TransportPreferences> preferences = getPreferencesByParcel(dc, assignment);
		Map<TimedTransportChain, List<IParcel>> deliveriesByChain = getParcelsByChain(preferences, deliveries);
		Map<TimedTransportChain, List<IParcel>> pickupsByChain = getParcelsByChain(preferences, pickUps);

		System.out.println("Plan tours for " + dc.getName() + "[" + dc.getId() + "]");
		System.out.println("    - chains: " + chains.size());
		System.out.println("    - unique chains: " + identicalChains.keySet().size());
		System.out.println("    - unique parcels: " + (deliveries.size() + pickUps.size()));
		System.out.println("    - unique parcels with preferences: " + preferences.keySet().size());
		System.out.println("    - unique deliveries: " + deliveries.size());
		System.out.println("    - unique deliveries with preferences: " + preferences.keySet().stream().filter(p -> !p.isPickUp()).count());
		System.out.println("    - unique pickups: " + pickUps.size());
		System.out.println("    - unique parcels with preferences: " + preferences.keySet().stream().filter(IParcel::isPickUp).count());


		Map<TimedTransportChain, List<LastMileTour>> validTours = new LinkedHashMap<>();

		List<IParcel> overflowParcelsForTruck = new ArrayList<>();

		List<TimedTransportChain> bikeChains = identicalChains.keySet()
				.stream().filter(c -> c.lastMileVehicle().equals(VehicleType.BIKE))
				.collect(toList());

		planLastMileToursAndHandelOverflow(
				dc,
				chains,
				identicalChains,
				preferences,
				deliveriesByChain,
				pickupsByChain,
				validTours,
				overflowParcelsForTruck,
				bikeChains,
				StandardMode.BIKE
		);

		List<TimedTransportChain> truckChains = identicalChains.keySet()
				.stream().filter(c -> c.lastMileVehicle().equals(VehicleType.TRUCK))
				.collect(toList());

		planLastMileToursAndHandelOverflow(
				dc,
				chains,
				identicalChains,
				preferences,
				deliveriesByChain,
				pickupsByChain,
				validTours,
				overflowParcelsForTruck,
				truckChains,
				StandardMode.TRUCK
		);

		System.out.println("    - planned parcels: " + validTours.values().stream().flatMapToInt(l -> l.stream().mapToInt(LastMileTour::parcelCount)).sum());
		System.out.println("    - unplanned parcels: " + overflowParcelsForTruck.size());
		System.out.println("    - bike tour parcel count: " +
				validTours.entrySet()
						  .stream()
						  .filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.BIKE))
						  .flatMap(e -> e.getValue().stream())
						  .map(LastMileTour::parcelCount).map(Object::toString).collect(joining(", "))
		);
		System.out.println("    - bike tour delivery count: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.BIKE))
						.flatMap(e -> e.getValue().stream())
						.mapToLong(lmt -> lmt.tour.getElements().stream().flatMap(c -> c.getParcels().stream()).filter(p -> !p.isPickUp()).count())
						.mapToObj(String::valueOf).collect(joining(", "))
		);
		System.out.println("    - bike tour pickup count: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.BIKE))
						.flatMap(e -> e.getValue().stream())
						.mapToLong(lmt -> lmt.tour.getElements().stream().flatMap(c -> c.getParcels().stream()).filter(IParcel::isPickUp).count())
						.mapToObj(String::valueOf).collect(joining(", "))
		);
		System.out.println("    - bike tour duration: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.BIKE))
						.flatMap(e -> e.getValue().stream())
						.map(t -> t.tour.getTravelTime()).map(Object::toString).collect(joining(", "))
		);
		System.out.println("    - bike tour stops: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.BIKE))
						.flatMap(e -> e.getValue().stream())
						.map(t -> t.tour.size()).map(Object::toString).collect(joining(", "))
		);
		System.out.println("    - bike tour departures: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.BIKE))
						.flatMap(e -> e.getValue().stream())
						.map(t -> t.chain.getFirstMileDeparture()).map(t -> t.getHour()+":"+t.getMinute()).collect(joining(", "))
		);
		System.out.println("    - truck tour parcel count: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.TRUCK))
						.flatMap(e -> e.getValue().stream())
						.map(LastMileTour::parcelCount).map(Object::toString).collect(joining(", "))
		);
		System.out.println("    - truck tour delivery count: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.TRUCK))
						.flatMap(e -> e.getValue().stream())
						.mapToLong(lmt -> lmt.tour.getElements().stream().flatMap(c -> c.getParcels().stream()).filter(p -> !p.isPickUp()).count())
						.mapToObj(String::valueOf).collect(joining(", "))
		);
		System.out.println("    - truck tour pickup count: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.TRUCK))
						.flatMap(e -> e.getValue().stream())
						.mapToLong(lmt -> lmt.tour.getElements().stream().flatMap(c -> c.getParcels().stream()).filter(IParcel::isPickUp).count())
						.mapToObj(String::valueOf).collect(joining(", "))
		);
		System.out.println("    - truck tour duration: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.TRUCK))
						.flatMap(e -> e.getValue().stream())
						.map(t -> t.tour.getTravelTime()).map(Object::toString).collect(joining(", "))
		);
		System.out.println("    - truck tour stops: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.TRUCK))
						.flatMap(e -> e.getValue().stream())
						.map(t -> t.tour.size()).map(Object::toString).collect(joining(", "))
		);
		System.out.println("    - truck tour departures: " +
				validTours.entrySet()
						.stream()
						.filter(e -> e.getKey().lastMileVehicle().equals(VehicleType.TRUCK))
						.flatMap(e -> e.getValue().stream())
						.map(t -> t.chain.getFirstMileDeparture()).map(t -> t.getHour()+":"+t.getMinute()).collect(joining(", "))
		);

		List<PlannedTour> plannedTours = createPlannedTours(time, dc, validTours);
		return plannedTours;
	}

	private void planLastMileToursAndHandelOverflow(
			DistributionCenter dc,
			List<TimedTransportChain> allChains,
			Map<TimedTransportChain, List<TimedTransportChain>> identicalChains,
			Map<IParcel, TransportPreferences> preferences,
			Map<TimedTransportChain, List<IParcel>> deliveriesByChain,
			Map<TimedTransportChain, List<IParcel>> pickupsByChain,
			Map<TimedTransportChain, List<LastMileTour>> validTours,
			List<IParcel> overflowParcelsForTruck,
			List<TimedTransportChain> modeChains,
			StandardMode mode
	) {
		boolean useTruckOverflow = (mode.equals(StandardMode.TRUCK));

		for (TimedTransportChain chain: modeChains) {
			if (!validTours.containsKey(chain)) {
				validTours.put(chain, new ArrayList<>());
			}

			List<IParcel> chainDeliveries = new ArrayList<>(deliveriesByChain.getOrDefault(chain, List.of()));
			List<IParcel> chainPickups = new ArrayList<>(pickupsByChain.getOrDefault(chain, List.of()));

			System.out.println("    - compute tours for " + chain.forLogging());
			System.out.println("        - preferred by deliveries " + chainDeliveries.size());
			System.out.println("        - preferred by pickups " + chainPickups.size());

			if (useTruckOverflow) {
				System.out.println("        - include overflow parcels " + overflowParcelsForTruck.size());


				chainDeliveries.addAll(
					overflowParcelsForTruck.stream().filter(p -> !p.isPickUp()).collect(toList())
				);
				chainPickups.addAll(
					overflowParcelsForTruck.stream().filter(IParcel::isPickUp).collect(toList())
				);
				overflowParcelsForTruck.clear();

				System.out.println("        - effective deliveries " + chainDeliveries.size());
				System.out.println("        - effective pickups " + chainPickups.size());
			}

			List<PlannedTour> chainTours = lastMilePlanning.planTours(
					chainDeliveries,
					chainPickups,
					chain.last().getFleet(),
					chain.getDeparture(chain.first())
			);
			System.out.println("        - tour sizes " + mode + ": " + chainTours.stream().map(p -> " " + p.getAllParcels().size()).collect(joining(",")) );

			List<TimedTransportChain> chainCopies = identicalChains.getOrDefault(chain, List.of());

			System.out.println("        - found tours: " + chainTours.size());
			System.out.println("        - number of chains: " + chainCopies.size());

			List<PlannedTour> unusedTours = new ArrayList<>(chainTours);
			for (TimedTransportChain copy: chainCopies) {
				if (unusedTours.isEmpty()) {break;}

				PlannedTour plan = unusedTours.get(0);
				unusedTours.remove(plan);

				float accessEgress = 0;
				if (plan.getPlannedStops().size() > 0) {
					accessEgress += impedance.getTravelTime(
							chain.last().getZone().getId(),
							plan.getPlannedStops().get(0).getZone().getId(),
							mode,
							chain.getDeparture(chain.last())
					);
					accessEgress += impedance.getTravelTime(
							plan.getPlannedStops().get(plan.getPlannedStops().size()-1).getZone().getId(),
							chain.last().getZone().getId(),
							mode,
							chain.getDeparture(chain.last()).plus(plan.getPlannedDuration())
					);
				}

				LastMileTour lmt = new LastMileTour(
						copy,
						tourFromPlan(plan, mode),
						accessEgress
				);

				validTours.get(chain).add(lmt);
			}

			System.out.println("        - remaining tours: " + unusedTours.size());

			int movedToNextPreference = 0;
			int movedToOverflow = 0;

			for (PlannedTour unused: unusedTours) {
				List<TimedTransportChain> unprocessedChains = allChains.stream().filter(c -> !validTours.containsKey(c)).collect(toList());
				Collection<IParcel> unusedParcels = unused.getAllParcels();

				for (IParcel parcel: unusedParcels) {
					TimedTransportChain newPreference = updatePreference(unprocessedChains, preferences, chain, parcel);

					if (unprocessedChains.contains(newPreference)) {
						movedToNextPreference++;
						if (parcel.isPickUp()) {
							if (!pickupsByChain.containsKey(newPreference)) {
								pickupsByChain.put(newPreference, new ArrayList<>());
							}

							pickupsByChain.get(newPreference).add(parcel);
						} else {

							if (!deliveriesByChain.containsKey(newPreference)) {
								deliveriesByChain.put(newPreference, new ArrayList<>());
							}

							deliveriesByChain.get(newPreference).add(parcel);
						}
					} else {
						movedToOverflow++;
						overflowParcelsForTruck.add(parcel);
					}
				}

			}

			System.out.println("        - remaining parcel with new preference: " + movedToNextPreference);
			System.out.println("        - remaining parcel without new preference (use for tours): " + movedToOverflow);

		}
	}

	private Tour<ParcelCluster> tourFromPlan(PlannedTour plan, StandardMode mode) {

		List<ParcelCluster> stops = plan.getPlannedStops()
										.stream()
										.map(s -> new ParcelCluster(s.getAllParcels(), clustering))
										.collect(toList());

		return new Tour<ParcelCluster>(
				stops,
				plan.getPlannedDuration().toMinutes(),
				solver.getTravelTimes(),
				mode
		);
	}

	private static Map<TimedTransportChain, List<IParcel>> getParcelsByChain(Map<IParcel, TransportPreferences> preferences, Collection<IParcel> combine) {
		return combine.stream()
				.filter(preferences::containsKey)
				.collect(groupingBy(p -> preferences.get(p).getSelected()))
				.entrySet().stream().collect(toMap(
						Map.Entry::getKey,
						e -> new ArrayList<>(e.getValue())
				));
	}

	private List<PlannedTour> createPlannedTours(Time time, DistributionCenter dc, Map<TimedTransportChain, List<LastMileTour>> validTours) {
		List<PlannedTour> plannedTours = new ArrayList<>();
		validTours.keySet().forEach(chainArchetype -> {

			for (LastMileTour tour: validTours.get(chainArchetype)) {
				TimedTransportChain chain = tour.chain;

				if (!tour.tour.isEmpty()) {
					float accessEgress = tour.tour.selectMinInsertionStart(chain.last().getZoneAndLocation());

					PlannedDeliveryTour plannedLastMile = new PlannedDeliveryTour(
							chain.lastMileVehicle(),
							RelativeTime.ofMinutes((int) ceil(tour.tour.getTravelTime() + accessEgress)),
							chain.getFirstMileDeparture(),
							false,
							impedance,
							chain.last()
					);

					PlannedTour planned;
					if (chain.uses(VehicleType.BIKE)) { //Encode return tour here
						boolean valid = fillTramTourPlan(chain, tour, accessEgress, plannedLastMile);

						if (!valid) {
							continue;
						}

						planned = ParcelBox.createDelivery(chain, plannedLastMile, impedance, transferTime);

					} else {
						fillDefaultTourPlan(tour, plannedLastMile);
						planned = plannedLastMile;
					}

					if (planned.getAllParcels().stream().distinct().count() < planned.getAllParcels().size()) {
						System.out.println("error duplicate");
					}

					plannedTours.add(planned);


				}

			}

		});

		System.out.println("    - planned " + plannedTours.size() + " tours for " + dc.getName() + "[" + dc.getId() + "]");
		System.out.println("      tour parcel cnt: " + plannedTours.stream().map(t -> t.getAllParcels().size() + ", ").collect(joining()));
		System.out.println("      tour durations: " + plannedTours.stream().map(t -> t.getPlannedDuration().toMinutes() + ", ").collect(joining()));
		return plannedTours;
	}

	private int maxNumParcels(VehicleType vehicle) {
		if (Objects.requireNonNull(vehicle) == VehicleType.BIKE) {
			return maxBikeParcels;
		}
		return maxTruckParcels;
	}

	private static void logValidation(String task, Map<TimedTransportChain, List<LastMileTour>> timeAndCapacityViolated, Map<TimedTransportChain, List<LastMileTour>> timeViolated, Map<TimedTransportChain, List<LastMileTour>> capacityViolated, Map<TimedTransportChain, List<LastMileTour>> validTours) {
		System.out.println(
				"    - " + task + ": valid=" + validTours.values().stream().mapToInt(List::size).sum() +
				" !time=" + timeViolated.values().stream().mapToInt(List::size).sum() +
				" !cap=" + capacityViolated.values().stream().mapToInt(List::size).sum() +
				" !cap&!time=" + timeAndCapacityViolated.values().stream().mapToInt(List::size).sum()
		);
	}

	private boolean fillTramTourPlan(
			TimedTransportChain chain,
			LastMileTour tour,
			float accessEgress,
			PlannedDeliveryTour planned
	) {
		int boxId = planned.getId();
		
		Time returnDeparture = chain.getArrival(chain.last()).plusMinutes((int) ceil(accessEgress + tour.tour.getTravelTime()));
		Optional<TimedTransportChain> maybeReturnChain = coordinator.createReturnChain(chain, returnDeparture);
		if (maybeReturnChain.isEmpty()) {
			System.out.println("Could not find reverse chain for " + chain + " returning at " + returnDeparture);
			return false;
		}


		TimedTransportChain returnChain = maybeReturnChain.get();
		BoxOnBike returningParcelBox =  ParcelBox.createBoxOnBike(returnChain, List.of(), List.of(), impedance, transferTime, boxId);
		
		tour.tour.iterator().forEachRemaining(stop -> {
			Collection<IParcel> wrappedParcels = stop.getParcels()
													 .stream()
													 .map(p -> 
													 		new ParcelWithReturnInfo(returningParcelBox, p)
													 ).collect(toList());
			
			ParcelActivityBuilder activity = new ParcelActivityBuilder(wrappedParcels, stop.getZoneAndLocation());						
			activity.withDuration(durationModel);
			planned.addStop(activity);
		});

		return true;
	}

	private void fillDefaultTourPlan(LastMileTour tour, PlannedDeliveryTour planned) {

		tour.tour.iterator().forEachRemaining(stop -> {
			ParcelActivityBuilder activity = new ParcelActivityBuilder(stop.getParcels(), stop.getZoneAndLocation());						
			activity.withDuration(durationModel);
			planned.addStop(activity);
		});
		
	}






	

	private Map<IParcel, TransportPreferences> getPreferencesByParcel(DistributionCenter dc,
			ChainAssignment assignment) {
		return assignment.getPreferences(dc).stream().collect(toMap(TransportPreferences::getParcel, identity()));
	}


	@Getter
	@AllArgsConstructor
	static class LastMileTour {
		private final TimedTransportChain chain;
		private Tour<ParcelCluster> tour;
		private float accessEgress;

		public double maxVolume() {
			return chain.last().getFleet().getVehicleVolume();
		}

		public int parcelCount() {
			return (int) tour.getElements().stream().mapToLong(c -> c.getParcels().size()).sum();
		}

		public double volume() {
			return tour.getElements().stream().mapToDouble(ParcelCluster::volume).sum();
		}

	}


	private TimedTransportChain updatePreference(
			List<TimedTransportChain> chains, 
			Map<IParcel, TransportPreferences> preferences,
			TimedTransportChain unavailable,
			IParcel parcel
	) {
		TimedTransportChain preference = unavailable;
		
		TransportPreferences prefs = preferences.get(parcel);
		while(prefs.getProbabilities().size() > 1 && !chains.contains(prefs.getSelected())) {
			prefs.removeOption(preference);
			preference = prefs.getSelected();
		}
		
		return prefs.getSelected();
	}
	
	private List<TimedTransportChain> findUnavailableChains(List<TimedTransportChain> chains,
			Map<TimedTransportChain, List<IParcel>> initialAssignment) {
		return initialAssignment.keySet().stream().filter(c -> !chains.contains(c)).collect(Collectors.toList());
	}

	private Collection<IParcel> combine(Collection<IParcel> deliveries, Collection<IParcel> pickUps) {
		Collection<IParcel> allParcel = new LinkedHashSet<>(deliveries);
		allParcel.addAll(pickUps);
		return allParcel;
	}

}
