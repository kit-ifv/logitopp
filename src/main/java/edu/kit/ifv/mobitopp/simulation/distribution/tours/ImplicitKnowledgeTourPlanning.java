package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.lang.Math.round;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.util.PriorityQueue;
import edu.kit.ifv.mobitopp.routing.util.SimplePQ;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;

public class ImplicitKnowledgeTourPlanning extends ClusterTourPlanningStrategy {

	private final ImpedanceIfc impedance;
	
	public ImplicitKnowledgeTourPlanning(DeliveryClusteringStrategy clusteringStrategy,
			DeliveryDurationModel durationModel, ImpedanceIfc impedance) {
		super(clusteringStrategy, durationModel);
		this.impedance = impedance;
	}
	
	@Override
	public List<PlannedDeliveryTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet,
			Time time) {
		
		DeliveryVehicle vehicle = fleet.getVehicles().iterator().next();
		List<PlannedDeliveryTour> tours = new ArrayList<>();
		
		System.out.print(vehicle.getOwner().getName() + " plans delivery: ");
		List<ParcelActivityBuilder> delActivities = getDeliveryActivities(deliveries, List.of());
		tours.addAll(
				this.planTours(delActivities, vehicle, time, RelativeTime.ofHours(6))
		);
		System.out.println(" -> " + tours.size());
		
		System.out.print(vehicle.getOwner().getName() + " plans delivery: ");
		List<ParcelActivityBuilder> pickActivities = getDeliveryActivities(List.of(), pickUps);
		tours.addAll(
				this.planTours(pickActivities, vehicle, time.startOfDay().plusHours(14), RelativeTime.ofHours(6))
		);
		System.out.println(" -> " + tours.size());
		
		return tours;
	}

	@Override
	protected List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle,
			Time time, RelativeTime duration) {
		List<PlannedDeliveryTour> tours = new ArrayList<>();
		
		
		Map<Zone, List<ParcelActivityBuilder>> activitiesPerZone = new LinkedHashMap<>(
				activities.stream().collect(groupingBy(ParcelActivityBuilder::getZone))
		);
		
		//iterate start zone
		while(!activitiesPerZone.isEmpty()) { //each iteration at least one zone is removed
			Zone origin = vehicle.getOwner().getZone();
			Zone zone = selectMinBySize(activitiesPerZone);
			Zone startZone = zone;
					
			List<ParcelActivityBuilder> stops = new ArrayList<>(activitiesPerZone.get(zone));
			activitiesPerZone.remove(zone);
			
			Time arrivalInZone = time.plusMinutes(round(travelTime(origin, zone, time)));
			do {// if zone has more than 150 parcels, create tours until less than 150 left
				
				Pair<List<ParcelActivityBuilder>, RelativeTime> selected = selectStops(stops, arrivalInZone, duration, zone, 150, true); //list contains at least one element
				
				RelativeTime tourDur = selected.getSecond();
				List<ParcelActivityBuilder> selectedStops = selected.getFirst();
				stops.removeAll(selectedStops);
				
				PlannedDeliveryTour newTour = createTour(vehicle, time, origin, startZone, zone, tourDur, selectedStops);
				tours.add(newTour);
				log(zone, newTour, "!,");
				
			} while (countParcels(stops) >= 150); // removes at least one stop per iteration
			
			
			// process remaining parcels of start zone
			List<ParcelActivityBuilder> stopsForTour = new ArrayList<>();
			stopsForTour.addAll(stops);
			stops.clear();

			if (stopsForTour.isEmpty()) { continue; } // if none are left, continue with next start zone
			
			int capacity = 150;
			Time currentTime = arrivalInZone;
			RelativeTime remainingTime = duration;
			
			int i = 0;
			for (ParcelActivityBuilder stop : stopsForTour) { // add remaining parcels to tour
				stop.plannedAt(currentTime);
				float dur = stop.withDuration(durationModel).getTripDuration();
				
				capacity -= stop.size();
				
				currentTime = currentTime.plusMinutes(round(dur));
				remainingTime = remainingTime.minusMinutes(round(dur));
				
				i++;
				if (!(i == stopsForTour.size())) {
					float travelTime = travelTime(zone, currentTime);
					currentTime = currentTime.plusMinutes(round(travelTime));
					remainingTime = remainingTime.minusMinutes(round(travelTime));
				}
			}
			
			// package to tour if constraints reached or no other zones available
			if (capacity <= 0 || remainingTime.isNegative() || activitiesPerZone.keySet().isEmpty()) {
				RelativeTime tourDur = duration.minus(remainingTime);
				PlannedDeliveryTour newTour = createTour(vehicle, time, origin, startZone, zone, tourDur, stopsForTour);
				tours.add(newTour);
				
				log(zone, newTour, "!");
				continue;
			}
			
			System.out.print(zone.getId().getExternalId() + "!,");
			
			// if capacity not reached check neighboring zones
			List<Zone> zonesToTest = new ArrayList<>(activitiesPerZone.keySet());
			do {
				
				Zone nextZone = nextZoneByDist(currentTime, zone, new ArrayList<>(activitiesPerZone.keySet()));
				List<ParcelActivityBuilder> newStops = new ArrayList<>(activitiesPerZone.get(nextZone));
				zonesToTest.remove(nextZone);
				
				int accessDur = round(travelTime(zone, nextZone, currentTime));
				
				if (accessDur >= 60) {
					break;
				}

				currentTime = currentTime.plusMinutes(accessDur);
				remainingTime = remainingTime.minusMinutes(accessDur);
				
				if (remainingTime.isNegative()) {
					remainingTime.plusMinutes(accessDur);
					break;
				}
				
				zone = nextZone;
				if (countParcels(newStops) <= capacity) { // take all parcels of next zone
					for (ParcelActivityBuilder stop: newStops) {
						stop.plannedAt(currentTime);
						int dur = stop.withDuration(durationModel).getDeliveryMinutes();
						
						stopsForTour.add(stop);
						
						currentTime = currentTime.plusMinutes(dur);
						remainingTime = remainingTime.minusMinutes(dur);
						capacity -= stop.size();
						
						System.out.print(nextZone.getId().getExternalId() + "!,");
					}
					
				} else { // take only a few parcels, put rest back in map
					Pair<List<ParcelActivityBuilder>, RelativeTime> selected = selectStops(newStops, currentTime, remainingTime, nextZone, capacity, false);
					
					RelativeTime tourDur = selected.getSecond();
					remainingTime = remainingTime.minus(tourDur);
					currentTime = currentTime.plus(tourDur);
					
					List<ParcelActivityBuilder> selectedStops = selected.getFirst();
					stopsForTour.addAll(selectedStops);
					newStops.removeAll(selectedStops);
					
					if (!newStops.isEmpty()) {
						activitiesPerZone.put(nextZone, newStops);
					}
					
					break;
				}
			
				if (capacity <= 0 || remainingTime.isNegative()) {// stop is constraints are reached					
					break;
				}

			} while (!zonesToTest.isEmpty());
			
			RelativeTime tourDur = duration.minus(remainingTime);
			PlannedDeliveryTour newTour = createTour(vehicle, currentTime, origin, startZone, zone, tourDur, stopsForTour);
			tours.add(newTour);
			
			if (activitiesPerZone.containsKey(zone)) {
				log(zone, newTour, "?;");
			} else {
				log(zone, newTour, "!;");
			}
			
		}
		
		
		return tours;
	}

	private void log(Zone zone, PlannedDeliveryTour newTour, String tag) {
		System.out.print(zone.getId().getExternalId() + "(" + newTour.getStops().size() + "/" + countParcels(newTour.getStops()) + ")" + tag);
	}

	private PlannedDeliveryTour createTour(DeliveryVehicle vehicle, Time time, Zone origin, Zone startZone, Zone lastZone,
			RelativeTime tourDur, List<ParcelActivityBuilder> selectedStops) {
		
		int accessDur = round(travelTime(origin, startZone, time));
		int returnDur = round(travelTime(lastZone, origin, time));
		RelativeTime totalDur = tourDur.plusMinutes(accessDur + returnDur);
		
		PlannedDeliveryTour newTour = newTour(vehicle, time, totalDur);
		newTour.addStops(selectedStops);
		return newTour;
	}

	private Zone selectMinBySize(Map<Zone, List<ParcelActivityBuilder>> activitiesPerZone) {
		return activitiesPerZone.entrySet()
									.stream()
									.min(
											comparing(e -> countParcels(e.getValue()))
									)
									.get()
									.getKey();
	}

	//assumes stops from same zone! hence uses intrazonal traveltime
	private Pair<List<ParcelActivityBuilder>, RelativeTime> selectStops(List<ParcelActivityBuilder> stops, Time arrivalInZone,
			RelativeTime duration, Zone zone, int capacity, boolean largeFirst) {
		List<ParcelActivityBuilder> result = new ArrayList<>();
		
		RelativeTime remainingTime = duration.plusSeconds(0);
		Time currentTime = arrivalInZone.plusSeconds(0);
		
		List<ParcelActivityBuilder> sorted = sortBySize(stops, largeFirst);
		for (ParcelActivityBuilder stop : sorted) {

			if ((capacity >= stop.size() && !remainingTime.isNegative()) || result.isEmpty()) {
				result.add(stop);
				capacity -= stop.size();
				
				stop.plannedAt(currentTime);
				
				float dur = stop.withDuration(durationModel).getDeliveryMinutes();
				dur += travelTime(zone, currentTime);
				
				currentTime = currentTime.plusMinutes(round(dur));
				remainingTime = remainingTime.minusMinutes(round(dur));
			}

		}
		
		RelativeTime tourDur = duration.minus(remainingTime);
		return Pair.of(result, tourDur);
	}

	private List<ParcelActivityBuilder> sortBySize(List<ParcelActivityBuilder> stops, boolean largeFirst) {
		Comparator<ParcelActivityBuilder> comparing = Comparator.comparing(s -> s.size());
		
		if (largeFirst) {
			comparing = comparing.reversed();
		}
		
		List<ParcelActivityBuilder> sorted = 
				stops.stream()
					 .sorted(comparing)
					 .sequential()
					 .collect(toList());
		return sorted;
	}

	private PlannedDeliveryTour newTour(DeliveryVehicle vehicle, Time time, RelativeTime duration) {
		return new PlannedDeliveryTour(vehicle.getType(), duration, time, true, impedance);
	}
	
//	@Override
//	protected List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle,
//			Time time, RelativeTime duration) {
//		System.out.println(vehicle.getOwner().getName() + " plans delivery areas + tours for " + activities.size() + "stops.");
//		List<PlannedDeliveryTour> tours = new ArrayList<>();
//		
//		Map<Zone, List<ParcelActivityBuilder>> activitiesPerZone = new LinkedHashMap<>(
//				activities.stream().collect(groupingBy(ParcelActivityBuilder::getZone))
//		);
//		
//		
//		PriorityQueue<Zone> zonePriority = priorityByDemand(activitiesPerZone);
//		
//		
//		while(!zonePriority.isEmpty()) {
//			if (zonePriority.minPriority().equals(0.0f)) {
//				zonePriority.deleteMin();
//				continue;
//			}
//			List<ParcelActivityBuilder> stopsOfTour = new ArrayList<>();
//			
//			RelativeTime remainingDur = duration.plusSeconds(0);
//			Zone currentZone = vehicle.getOwner().getZone();
//			Time currentTime = time.plusSeconds(0);
//			int capacity = 150;
//			
//			Zone zone = zonePriority.minElement();
//			List<Zone> zonestToTest = new ArrayList<>(activitiesPerZone.keySet());
//			System.out.print("Processing zones: ");
//			do {
//				
//				int accessDur = round(travelTime(currentZone, zone, currentTime));
//				List<ParcelActivityBuilder> possibleStops = activitiesPerZone.get(zone);
//				
//				System.out.print(zone.getId().getExternalId() + "(" + possibleStops.size() +"), ");
//				
//				Pair<List<ParcelActivityBuilder>, RelativeTime> stopsInZone = 
//					pickStopsWithWorktime(vehicle, zone, currentTime, possibleStops, remainingDur.minusMinutes(accessDur), capacity);
//				
//				if (!stopsInZone.getFirst().isEmpty()) {
//					RelativeTime durAfterZone = stopsInZone.getSecond();
//					currentTime = currentTime.plus(remainingDur).minus(durAfterZone);
//					remainingDur = stopsInZone.getSecond();
//					
//					currentZone = zone;
//					capacity -= countParcels(stopsInZone.getFirst());
//					
//					updateRemainingStops(activitiesPerZone, zonePriority, stopsInZone.getFirst(), zone);
//				} else {
//					System.out.print("(no stops selected!)");
//				}
//				
//				zonestToTest.remove(zone);
//				if (!zonestToTest.isEmpty()) {
//					zone = nextZoneByDist(currentTime, zone, zonestToTest);
//				}
//				
//			} while(!zonestToTest.isEmpty() && capacity > 0 && enoughTimeForReturn(vehicle, remainingDur, currentTime, zone) );
//			
//			System.out.println();
//			tours.add(new PlannedDeliveryTour(vehicle.getType(), stopsOfTour, duration.minus(remainingDur), currentTime, true, impedance));
//		}
//		
//		System.out.println("Generated " + tours.size() + "tours!");
//		
//		return tours;
//	}
//
//	private Pair<List<ParcelActivityBuilder>, RelativeTime> pickStopsWithWorktime(DeliveryVehicle vehicle, Zone zone, Time time, List<ParcelActivityBuilder> stops, RelativeTime workTime, int capacity) {
//		List<ParcelActivityBuilder> selectedStops = new ArrayList<>();
//		RelativeTime remainingTime = workTime.plusSeconds(0);
//		Time currentTime = time.plusSeconds(0);
//		int remainingCapacity = capacity;
//				
//		for (ParcelActivityBuilder stop : stops) {
//			float dur = travelTime(zone, currentTime);
//			
//			stop.plannedAt(currentTime.plusMinutes(round(dur)));
//			
//			dur += stop.withDuration(durationModel).getDeliveryMinutes();
//		
//			int duration = round(dur);
//			int returnTime = returnTime(vehicle, currentTime, zone);
//			
//			if (timeIsSufficient(remainingTime, duration+returnTime) && remainingCapacity >= stop.size()) {
//				selectedStops.add(stop);
//			} else {
//				if (!timeIsSufficient(remainingTime, duration+returnTime)) { System.out.print("(insufficient time: " + remainingTime.toMinutes() + " < " + (duration+returnTime) + " dur+ret)");}
//				if (!(remainingCapacity >= stop.size())) { System.out.print("insufficient capacity: " + remainingCapacity + " < " + stop.size()); }
//				break;
//			}
//			
//			currentTime = currentTime.plusMinutes(duration);
//			remainingTime = remainingTime.minusMinutes(duration);
//			remainingCapacity -= stop.size();
//		}
//		
//		return Pair.of(selectedStops, remainingTime);
//	}
//
//	private void updateRemainingStops(Map<Zone, List<ParcelActivityBuilder>> zones, PriorityQueue<Zone> zonePriority, List<ParcelActivityBuilder> stops, Zone zone) {
//		List<ParcelActivityBuilder> remainingStops = zones.get(zone);
//		remainingStops.removeAll(stops);
//		
//		zonePriority.decreaseKey(zone, (float) remainingStops.size());
//		
//		if (remainingStops.isEmpty()) {
//			zones.remove(zone);
//			
//		}
//	}

	private Zone nextZoneByDist(Time currentTime, Zone zone, List<Zone> zonestToTest) {
		Time timeOfTransfer = currentTime;
		return zonestToTest.stream().sorted(Comparator.comparingDouble(z -> travelTime(zone, z, timeOfTransfer))).findFirst().get();
	}
	
//	private PriorityQueue<Zone> priorityByDemand(Map<Zone, List<ParcelActivityBuilder>> activitiesPerZone) {
//		PriorityQueue<Zone> zones = new SimplePQ<>();
//		activitiesPerZone.entrySet()
//						 .stream()
//						 .forEach(e -> {
//							 zones.add(e.getKey(), (float) countParcels(e.getValue()));
//						 });
//		return zones;
//	}

	private int countParcels(List<ParcelActivityBuilder> list) {
		return list.stream().mapToInt(ParcelActivityBuilder::size).sum();
	}
	
//	private boolean timeIsSufficient(RelativeTime remainingTime, int dur) {
//		return !remainingTime.minusMinutes(dur).isNegative();
//	}
//
//	private boolean enoughTimeForReturn(DeliveryVehicle vehicle, RelativeTime remainingDur, Time currentTime,
//			Zone zone) {
//		return !remainingDur.minusMinutes(returnTime(vehicle, currentTime, zone)).isNegative();
//	}

//	private int returnTime(DeliveryVehicle vehicle, Time currentTime, Zone zone) {
//		return round(travelTime(zone, vehicle.getOwner().getZone(), currentTime));
//	}

	private float travelTime(Zone zone, Time currentTime) {
		return travelTime(zone, zone, currentTime);
	}
	private float travelTime(Zone from, Zone to, Time currentTime) {
		return impedance.getTravelTime(from.getId(), to.getId(), StandardMode.TRUCK, currentTime);
	}

}
