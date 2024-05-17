package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import static java.lang.Math.ceil;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryDurationModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.preference.TransportPreferences;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.planning.TourPlanningStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.box.BoxOnBike;
import edu.kit.ifv.mobitopp.simulation.parcels.box.ParcelBox;
import edu.kit.ifv.mobitopp.simulation.parcels.box.ParcelWithReturnInfo;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.ParcelCluster;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.routing.Tour;
import edu.kit.ifv.mobitopp.util.routing.TspSolver;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CoordinatedChainTourStrategy implements TourPlanningStrategy {
	
	private final ImpedanceIfc impedance;
	private final CapacityCoordinator coordinator;
	private final TspSolver<ParcelCluster> solver;
	private final DeliveryClusteringStrategy clustering;
	private final DeliveryDurationModel durationModel;
	
	public CoordinatedChainTourStrategy(CapacityCoordinator coordinator, DeliveryClusteringStrategy clustering, ImpedanceIfc impedance, DeliveryDurationModel durationModel, TspSolver<ParcelCluster> tspSolver) {
		this.impedance = impedance;
		this.coordinator = coordinator;
		this.solver = tspSolver;
		this.clustering = clustering;
		this.durationModel = durationModel;
	}
	
	@Override
	public boolean shouldReplanTours(DistributionCenter center, Time time) {
		return time.equals(time.startOfDay().plusHours(3));
	}
	
	@Override
	public List<PlannedTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet,
			Time time) {
		
		DistributionCenter dc = fleet.getDistributionCenter();
		
		ChainAssignment assignment = coordinator.getAssignment(time);
		List<TimedTransportChain> chains = assignment.getChains(dc);
		
		Map<IParcel, TransportPreferences> preferences = getPreferencesByParcel(dc, assignment);
		
		//plan giant tours for distinct chains (duplicates are here ignored)
		Map<TimedTransportChain, Tour<ParcelCluster>> initialTours = createInitialTours(deliveries, pickUps, chains, preferences);
		
//		initialTours.values().forEach(t -> System.out.println(
//				t.getElements().stream().flatMap(c -> c.getParcels().stream().map(IParcel::getOId)).sorted().map(Object::toString).collect(Collectors.joining(" "))
//		));
		
		//Some chains have multiple copies (e.g. if multiple truck vehicle are available):split demand to form smaller subtours
		Map<TimedTransportChain, List<LastMileTour>> lastMileTours = splitToursForChainCopies(chains, initialTours);
		
		
		//TODO fix tours violating tour constraints then build planned tours
		
		Map<TimedTransportChain, List<LastMileTour>> timeAndCapacityViolated = new LinkedHashMap<>();
		Map<TimedTransportChain, List<LastMileTour>> timeViolated = new LinkedHashMap<>();
		Map<TimedTransportChain, List<LastMileTour>> capacityViolated = new LinkedHashMap<>();
		Map<TimedTransportChain, List<LastMileTour>> validTours = new LinkedHashMap<>();
		
		sortTours(lastMileTours, timeAndCapacityViolated, capacityViolated, timeViolated, validTours);
		
		List<IParcel> removedParcels = new ArrayList<>();
		resolveTimeViolation(timeAndCapacityViolated, capacityViolated, validTours, removedParcels);
		resolveTimeViolation(timeViolated, capacityViolated, validTours, removedParcels);
		resolveCapacityViolation(capacityViolated, validTours, removedParcels);
		
		List<IParcel> remaining = addRemovedParcelsToOtherExistingStops(preferences, validTours, removedParcels);
		if (!remaining.isEmpty()) {
			System.out.println("Not all parcels (" + remaining.size() + ") could be included in any of their preferred/allowed tours: " + remaining.stream().map(p -> ""+p.getOId()).collect(joining(",")));
		}
		
		
		
		
		
		List<PlannedTour> plannedTours = new ArrayList<>();
		validTours.keySet().forEach(chain -> {

			for (LastMileTour tour: validTours.get(chain)) {

				if (!tour.tour.isEmpty()) {
					float accessEgress = tour.tour.selectMinInsertionStart(chain.last().getZoneAndLocation());
					
					PlannedDeliveryTour plannedLastMile = new PlannedDeliveryTour(chain.lastMileVehicle(), RelativeTime.ofMinutes((int) ceil(tour.tour.getTravelTime() + accessEgress)), time, false, impedance, chain.last());

					PlannedTour planned;
					if (chain.uses(VehicleType.TRAM)) { //Encode return tour here
						boolean valid = fillTramTourPlan(chain, tour, accessEgress, plannedLastMile);

						if (!valid) {
							continue;
						}
						
						planned = ParcelBox.createDelivery(chain, plannedLastMile, impedance);
						
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
		
		
		return plannedTours;
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
		BoxOnBike returningParcelBox =  ParcelBox.createBoxOnBike(returnChain, List.of(), List.of(), impedance, boxId);
		
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
	
	private DeliveryClusteringStrategy preferenceAwareClustering(Map<IParcel, TransportPreferences> preferences) {
		return new DeliveryClusteringStrategy() {

			@Override
			public boolean canBeGrouped(IParcel a, IParcel b) {
				List<TimedTransportChain> optsA = preferences.get(a).options();
				List<TimedTransportChain> optsB = preferences.get(b).options();
				return optsA.stream().anyMatch(optsB::contains) && clustering.canBeGrouped(a, b);
			}

			@Override
			public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster) {
				return clustering.getStopLocation(deliveryCluster);
			}
			
		};
	}
	

	private List<IParcel> addRemovedParcelsToOtherExistingStops(Map<IParcel, TransportPreferences> preferences,
			Map<TimedTransportChain, List<LastMileTour>> validTours, List<IParcel> removedParcels) {
		
		List<IParcel> remaining = new ArrayList<>();

		removedParcels.forEach(parcel -> {
			
			TransportPreferences preferredChains = preferences.get(parcel);
			
			List<LastMileTour> nonFullTours = 
				preferredChains.options()
							   .stream()
							   .flatMap(c -> validTours.getOrDefault(c, List.of()).stream())
							   .filter(lmt -> lmt.volume() < lmt.maxVolume())
							   .collect(toList());
			
			if (nonFullTours.isEmpty()) {
				//TODO just drop parcel and try again next day?
				remaining.add(parcel);
			
			} else {
				boolean handeled = false;
				for (LastMileTour lmt: nonFullTours) {
					
					Optional<ParcelCluster> matchingCluster = 
						lmt.tour.getElements()
								.stream()
								.filter(cluster -> clustering.canBeGrouped(parcel, cluster.getParcels().get(0)))
								.findFirst();
					
					if (matchingCluster.isPresent()) {
						matchingCluster.get().getParcels().add(parcel);
						handeled=true;
						break;
					}
				}
				
				if (!handeled) {
					addParcelAsNewStopAtMinPosition(validTours, remaining, parcel, preferredChains);
				}
			}
			
		});
		
		return remaining;
	}

	private void addParcelAsNewStopAtMinPosition(Map<TimedTransportChain, List<LastMileTour>> validTours,
			List<IParcel> remaining, IParcel parcel, TransportPreferences preferredChains) {
		ParcelCluster newStop = new ParcelCluster(new ArrayList<>(List.of(parcel)), clustering);
		
		Optional<LastMileTour> minInsertionTour = 
				preferredChains.options()
							   .stream()
							   .filter(validTours::containsKey)
							   .flatMap(c -> validTours.get(c).stream())
							   .filter(t -> t.tour.getTravelTime() + t.tour.minInsertionCost(newStop) + t.accessEgress < 8*60) //TODO time
							   .min(Comparator.comparing(t -> t.tour.minInsertionCost(newStop)));

		if (minInsertionTour.isPresent()) {
			minInsertionTour.get().tour.insertAtMinPosition(newStop);
		} else {
			remaining.add(parcel);
		}
	}

	private void resolveCapacityViolation(Map<TimedTransportChain, List<LastMileTour>> capacityViolated,
			Map<TimedTransportChain, List<LastMileTour>> validTours, List<IParcel> removedParcels) {
		capacityViolated.keySet().forEach(chain -> {
			capacityViolated.get(chain).forEach(lmt -> {
				
				removeStopsOfMinCost(removedParcels, lmt, true);
				
				if (lmt.volume() > lmt.maxVolume()) {
					removeStopsOfMinCost(removedParcels, lmt, false);
				}
				
				//here each stops in tour should have more parcels than overflow!
				if (lmt.volume() > lmt.maxVolume()) {
					takeParcelsFromFirstStop(removedParcels, lmt);
				}
				
				validTours.get(chain).add(lmt);
				
			});
		});
	}

	private void takeParcelsFromFirstStop(List<IParcel> removedParcels, LastMileTour lmt) {
		List<IParcel> parcels = lmt.tour.getModuloFromStart(0).getParcels();
		double overflowCapacity = lmt.volume() - lmt.maxVolume();
		
		while (overflowCapacity > 0 && parcels.size() > 1) {
			IParcel removedParcel = parcels.remove(0);
			removedParcels.add(removedParcel);
			overflowCapacity -= removedParcel.getVolume();
		}
	}

	private void removeStopsOfMinCost(List<IParcel> removedParcels, LastMileTour lmt, boolean checkCostNegative) {
		TimedTransportChain chain = lmt.chain;
		
		double overflowCapacity = lmt.volume() - lmt.maxVolume();
		List<ParcelCluster> sortedStops = lmt.tour.getElements().stream().sorted(Comparator.comparing(lmt.tour::getRemovalCost)).collect(toList());
		
		int i = 0;
		while (overflowCapacity > 0 && i < sortedStops.size()) {
			ParcelCluster stop = sortedStops.get(i);
			
			if (	stop.volume() <= overflowCapacity
				&& (!checkCostNegative || lmt.tour.getRemovalCost(stop) < 0)
			) {
				removedParcels.addAll(stop.getParcels());
				lmt.tour.remove(stop);
				lmt.accessEgress = lmt.tour.selectMinInsertionStart(chain.last().getZoneAndLocation());
				overflowCapacity -= stop.volume();
			}
			
			i++;
		}
	}

	private void resolveTimeViolation(
			Map<TimedTransportChain, List<LastMileTour>> timeAndCapacityViolated,
			Map<TimedTransportChain, List<LastMileTour>> capacityViolated,
			Map<TimedTransportChain, List<LastMileTour>> validTours, List<IParcel> removedParcels
	) {
		
		timeAndCapacityViolated.keySet().forEach(chain -> {
			timeAndCapacityViolated.get(chain).forEach(lmt -> {
				
				while(lmt.tour.getTravelTime()+lmt.accessEgress > 8*60) { // TODO work time
					int toRemove = lmt.tour.findMinRemovalIndex();
					
					ParcelCluster cluster = lmt.tour.getModulo(toRemove);
					removedParcels.addAll(cluster.getParcels());
					
					float cost = lmt.tour.removeAtPosition(toRemove);
					lmt.accessEgress = lmt.tour.selectMinInsertionStart(chain.last().getZoneAndLocation());
				}
				
				if (lmt.volume() > lmt.maxVolume()) {
					capacityViolated.get(chain).add(lmt);
				
				} else {
					validTours.get(chain).add(lmt);
				}
				
				
			});
		});
	}

	private void sortTours(Map<TimedTransportChain, List<LastMileTour>> lastMileTours,
			Map<TimedTransportChain, List<LastMileTour>> timeAndCapacityViolated, Map<TimedTransportChain, List<LastMileTour>> capacityViolated,
			Map<TimedTransportChain, List<LastMileTour>> timeViolated, Map<TimedTransportChain, List<LastMileTour>> validTours) {
		for (TimedTransportChain chain: lastMileTours.keySet()) {
			
			timeAndCapacityViolated.put(chain, new ArrayList<>());
			timeViolated.put(chain, new ArrayList<>());
			capacityViolated.put(chain, new ArrayList<>());
			validTours.put(chain, new ArrayList<>());
			
			for (LastMileTour tour: lastMileTours.get(chain)) {
				
				boolean timeValid = (tour.tour.getTravelTime() + tour.accessEgress) < 8*60; 
				boolean capacityValid = tour.volume() <= tour.maxVolume();
				
				if (!capacityValid && !timeValid) {
					timeAndCapacityViolated.get(chain).add(tour);
				
				} else if (!timeValid) {
					timeViolated.get(chain).add(tour);
				
				} else if (!capacityValid) {
					capacityViolated.get(chain).add(tour);
				
				} else {
					validTours.get(chain).add(tour);
				}
	
			}
		}
	}
		
	

	private Map<IParcel, TransportPreferences> getPreferencesByParcel(DistributionCenter dc,
			ChainAssignment assignment) {
		return assignment.getPreferences(dc).stream().collect(toMap(TransportPreferences::getParcel, identity()));
	}

	private Map<TimedTransportChain, List<LastMileTour>> splitToursForChainCopies(
			List<TimedTransportChain> chains,
			Map<TimedTransportChain, Tour<ParcelCluster>> initialTours
	) {
		Map<TimedTransportChain, List<LastMileTour>> lastMileTours = new LinkedHashMap<>();
		
		Map<TimedTransportChain, List<TimedTransportChain>> identicalChains = chains.stream().collect(groupingBy(identity()));
		for (TimedTransportChain chain: identicalChains.keySet()) {
			
			if (identicalChains.get(chain).size() == 1) {
				Tour<ParcelCluster> tour = initialTours.get(chain);
				float accessEgress = tour.selectMinInsertionStart(chain.last().getZoneAndLocation());
				lastMileTours.put(chain, List.of(new LastMileTour(chain, tour, accessEgress)));
			
			} else {//route first cluster second: distribute parcels with same preference among copies of identical chains
				//TODO consider delivery time here?
				List<TimedTransportChain> remainingChainCopies = new ArrayList<>(identicalChains.get(chain));
				List<LastMileTour> subTours = new ArrayList<>();
				
				Tour<ParcelCluster> tour = initialTours.get(chain);
				tour.selectMinInsertionStart(chain.last().getZoneAndLocation());
				
				double volume = chain.last().getFleet().getVehicleVolume();
				StandardMode mode = chain.lastMileVehicle().getMode().mainMode();
				Tour<ParcelCluster> subTour = new Tour<>(solver.getTravelTimes(), mode);
				
				//iterate all parcels preferring this kind of delivery chain
				for (Iterator<ParcelCluster> iter = tour.iterator(); iter.hasNext(); ) {
					subTour.insertAtMinPosition(iter.next());
					
					float tourTime = subTour.getTravelTime() + subTour.selectMinInsertionStart(chain.last().getZoneAndLocation());
					
					if (violatesTourConstraints(volume, tourTime, subTour) && remainingChainCopies.size() > 1) {
						TimedTransportChain copy = remainingChainCopies.remove(0);
						float accessEgress = tour.selectMinInsertionStart(copy.last().getZoneAndLocation());
						subTours.add(new LastMileTour(copy, subTour, accessEgress));
						subTour = new Tour<>(solver.getTravelTimes(), mode);
					}
					
				}
				
				//assign remaining tour to first remaining chain copy
				TimedTransportChain copy = remainingChainCopies.remove(0);
				float accessEgress = tour.selectMinInsertionStart(copy.last().getZoneAndLocation());
				subTours.add(new LastMileTour(copy, subTour, accessEgress));
				
				//if not all chain copies were needed, create empty tours for remaining
				remainingChainCopies.forEach(c -> {
					subTours.add(new LastMileTour(c, new Tour<>(solver.getTravelTimes(), mode), 0));
				});
				
				
				lastMileTours.put(chain, subTours);
				
			}
			
		}
		return lastMileTours;
	}
	
	private boolean violatesTourConstraints(double maxVolume, float tourTime, Tour<ParcelCluster> tour) {
		return tour.getElements().stream().mapToDouble(ParcelCluster::volume).sum() >= maxVolume || tourTime > 8*0.8;
		//TODO factor of travel time vs total tour time incl delivery time
	}

	@Getter
	@AllArgsConstructor
	static class LastMileTour {
		private final TimedTransportChain chain;
		private final Tour<ParcelCluster> tour;
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
	
	

	// here duplicates of chains are removed to gather all preferences in single giant tour, later divide if constraints are violated
	private Map<TimedTransportChain, Tour<ParcelCluster>> createInitialTours(Collection<IParcel> deliveries,
			Collection<IParcel> pickUps, List<TimedTransportChain> chains,
			Map<IParcel, TransportPreferences> preferences) {
		
		List<TimedTransportChain> distinctChains = chains.stream().distinct().collect(toList());
		
		Collection<IParcel> allParcel = combine(deliveries, pickUps);
		Map<TimedTransportChain, List<IParcel>> initialAssignment = resolveInitialAssignment(distinctChains, preferences, allParcel);
		
		Map<TimedTransportChain, Collection<ParcelCluster>> clusters = computeClusters(initialAssignment);		
		
//		clusters.values().forEach(t -> System.out.println(
//				t.stream().flatMap(c -> c.getParcels().stream().map(IParcel::getOId)).sorted().map(Object::toString).collect(Collectors.joining(" "))
//		));
		
		return distinctChains.stream()
					 .collect(Collectors.toMap(
							 Function.identity(), 
							 c -> solver.findTour(clusters.get(c), c.lastMileVehicle().getMode().mainMode()
					 )));
	}

	private Map<TimedTransportChain, Collection<ParcelCluster>> computeClusters(
			Map<TimedTransportChain, List<IParcel>> initialAssignment) {
		return initialAssignment
				 .entrySet()
				 .stream()
				 .collect(Collectors.toMap(
						 Map.Entry::getKey,
						 e -> clustering.cluster(e.getValue(), e.getKey().last().getFleet().getVehicleVolume())
				 ));
	}

	private Collection<IParcel> combine(Collection<IParcel> deliveries, Collection<IParcel> pickUps) {
		Collection<IParcel> allParcel = new LinkedHashSet<>(deliveries);
		allParcel.addAll(pickUps);
		return allParcel;
	}

	private Map<TimedTransportChain, List<IParcel>> resolveInitialAssignment(List<TimedTransportChain> chains,
			Map<IParcel, TransportPreferences> preferences, Collection<IParcel> allParcel) {
		
		Map<TimedTransportChain, List<IParcel>> initialAssignment = 
			allParcel.stream().collect(groupingBy(p -> preferences.get(p).getSelected()));
		
		chains.forEach(c -> {
			initialAssignment.putIfAbsent(c, List.of());
		});	
		
		List<TimedTransportChain> unavailableChains = findUnavailableChains(chains, initialAssignment);
		unavailableChains.forEach(u -> {
			
			List<IParcel> unassignedParcels = initialAssignment.get(u);
			initialAssignment.remove(u);
			
			unassignedParcels.forEach(p -> {
				
				initialAssignment.get(
					updatePreference(chains, preferences, initialAssignment, u, p)
				).add(p);
				
			});		
		});
		
		return initialAssignment;
	}

	private TimedTransportChain updatePreference(
			List<TimedTransportChain> chains, 
			Map<IParcel, TransportPreferences> preferences,
			Map<TimedTransportChain, List<IParcel>> initialAssignment, TimedTransportChain unavailable, IParcel parcel) {
		
		TransportPreferences pref = preferences.get(parcel);
		do {
			pref.removeOption(unavailable);
			unavailable = pref.getSelected();
		} while(!chains.contains(pref.getSelected()));
		
		return pref.getSelected();
	}
	
	private List<TimedTransportChain> findUnavailableChains(List<TimedTransportChain> chains,
			Map<TimedTransportChain, List<IParcel>> initialAssignment) {
		return initialAssignment.keySet().stream().filter(c -> !chains.contains(c)).collect(Collectors.toList());
	}

}
