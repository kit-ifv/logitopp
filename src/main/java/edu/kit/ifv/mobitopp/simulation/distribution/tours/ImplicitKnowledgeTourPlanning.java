package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
		
		List<ParcelActivityBuilder> delActivities = getDeliveryActivities(deliveries, List.of());
		tours.addAll(
				this.planTours(delActivities, vehicle, time, RelativeTime.ofHours(6))
		);
		
		List<ParcelActivityBuilder> pickActivities = getDeliveryActivities(List.of(), pickUps);
		tours.addAll(
				this.planTours(pickActivities, vehicle, time.startOfDay().plusHours(14), RelativeTime.ofHours(6))
		);
		
		return tours;
	}

	@Override
	protected List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle,
			Time time, RelativeTime duration) {
		List<PlannedDeliveryTour> tours = new ArrayList<>();
		
		Map<Zone, List<ParcelActivityBuilder>> activitiesPerZone = 
				activities.stream().collect(Collectors.groupingBy(ParcelActivityBuilder::getZone));
		
		
		PriorityQueue<Zone> zonePriority = priorityByDemand(activitiesPerZone);
		
		
		while(!zonePriority.isEmpty()) {
			if (zonePriority.minPriority().equals(0.0f)) {
				zonePriority.deleteMin();
				continue;
			}
			List<ParcelActivityBuilder> stopsOfTour = new ArrayList<>();
			
			RelativeTime remainingDur = duration.plusSeconds(0);
			Zone currentZone = vehicle.getOwner().getZone();
			Time currentTime = time.plusSeconds(0);
			int capacity = 150;
			
			Zone zone = zonePriority.minElement();
			List<Zone> zonestToTest = new ArrayList<>(activitiesPerZone.keySet());
			do {
				
				int accessDur = round(travelTime(currentZone, zone, currentTime));
				Pair<List<ParcelActivityBuilder>, RelativeTime> stopsInZone = 
					pickStopsWithWorktime(vehicle, zone, currentTime, stopsOfTour, remainingDur.minusMinutes(accessDur), capacity);
				
				if (!stopsInZone.getFirst().isEmpty()) {
					RelativeTime durAfterZone = stopsInZone.getSecond();
					currentTime = currentTime.plus(remainingDur).minus(durAfterZone);
					remainingDur = stopsInZone.getSecond();
					
					currentZone = zone;
					capacity -= countParcels(stopsInZone.getFirst());
					
					updateRemainingStops(activitiesPerZone, zonePriority, stopsInZone.getFirst(), zone);
				}
				
				zonestToTest.remove(zone);
				zone = nextZoneByDist(currentTime, zone, zonestToTest);
				
			} while(!zonestToTest.isEmpty() && capacity > 0 && enoughTimeForReturn(vehicle, remainingDur, currentTime, zone) );
			
			tours.add(new PlannedDeliveryTour(vehicle.getType(), stopsOfTour, duration.minus(remainingDur), currentTime, true, impedance));
		}
		
		return tours;
	}

	private Pair<List<ParcelActivityBuilder>, RelativeTime> pickStopsWithWorktime(DeliveryVehicle vehicle, Zone zone, Time time, List<ParcelActivityBuilder> stops, RelativeTime workTime, int capacity) {
		List<ParcelActivityBuilder> selectedStops = new ArrayList<>();
		RelativeTime remainingTime = workTime.plusSeconds(0);
		Time currentTime = time.plusSeconds(0);
		int remainingCapacity = capacity;
				
		for (ParcelActivityBuilder stop : stops) {
			float dur = travelTime(zone, currentTime);
			
			stop.plannedAt(currentTime.plusMinutes(round(dur)));
			
			dur += stop.withDuration(durationModel).getDeliveryMinutes();
		
			int duration = round(dur);
			int returnTime = returnTime(vehicle, currentTime, zone);
			
			if (timeIsSufficient(remainingTime, duration+returnTime) && remainingCapacity >= stop.size()) {
				selectedStops.add(stop);
			} else {
				break;
			}
			
			currentTime = currentTime.plusMinutes(duration);
			remainingTime = remainingTime.minusMinutes(duration);
			remainingCapacity -= stop.size();
		}
		
		return Pair.of(selectedStops, remainingTime);
	}

	private void updateRemainingStops(Map<Zone, List<ParcelActivityBuilder>> zones, PriorityQueue<Zone> zonePriority, List<ParcelActivityBuilder> stops, Zone zone) {
		List<ParcelActivityBuilder> remainingStops = zones.get(zone);
		remainingStops.removeAll(stops);
		
		zonePriority.decreaseKey(zone, (float) remainingStops.size());
		
		if (remainingStops.isEmpty()) {
			zones.remove(zone);
			
		}
	}

	private Zone nextZoneByDist(Time currentTime, Zone zone, List<Zone> zonestToTest) {
		Time timeOfTransfer = currentTime;
		return zonestToTest.stream().sorted(Comparator.comparingDouble(z -> travelTime(zone, z, timeOfTransfer))).findFirst().get();
	}
	
	private PriorityQueue<Zone> priorityByDemand(Map<Zone, List<ParcelActivityBuilder>> activitiesPerZone) {
		PriorityQueue<Zone> zones = new SimplePQ<>();
		activitiesPerZone.entrySet()
						 .stream()
						 .forEach(e -> {
							 zones.add(e.getKey(), (float) countParcels(e.getValue()));
						 });
		return zones;
	}

	private int countParcels(List<ParcelActivityBuilder> list) {
		return list.stream().mapToInt(ParcelActivityBuilder::size).sum();
	}
	
	private boolean timeIsSufficient(RelativeTime remainingTime, int dur) {
		return remainingTime.minusMinutes(dur).isNegative();
	}

	private boolean enoughTimeForReturn(DeliveryVehicle vehicle, RelativeTime remainingDur, Time currentTime,
			Zone zone) {
		return !remainingDur.minusMinutes(returnTime(vehicle, currentTime, zone)).isNegative();
	}

	private int returnTime(DeliveryVehicle vehicle, Time currentTime, Zone zone) {
		return round(travelTime(zone, vehicle.getOwner().getZone(), currentTime));
	}

	private float travelTime(Zone zone, Time currentTime) {
		return travelTime(zone, zone, currentTime);
	}
	private float travelTime(Zone from, Zone to, Time currentTime) {
		return impedance.getTravelTime(from.getId(), to.getId(), StandardMode.TRUCK, currentTime);
	}

}
