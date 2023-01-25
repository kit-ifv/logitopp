package edu.kit.ifv.mobitopp.simulation.distribution.tours.precalculated;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryTourAssignmentStrategy;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryAgent;
import edu.kit.ifv.mobitopp.simulation.fleet.VehicleType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class PrecalculatedTourAssignment implements DeliveryTourAssignmentStrategy {
	
	private final Map<DayOfWeek, Collection<Route>> routes;
	private DayOfWeek lastUpdate = null;
	private final ImpedanceIfc impedance;
	
	public PrecalculatedTourAssignment(Map<DayOfWeek, CsvFile> routeFiles, String algorithm, String name, ImpedanceIfc impedance) {
		this.routes = new HashMap<>();
		routeFiles.forEach((day,file) -> this.routes.put(day, Route.parseRoutes(file, algorithm, name+"_"+day)));
		this.impedance = impedance;
	}
	

	@Override
	public List<ParcelActivityBuilder> assignParcels(Collection<ParcelActivityBuilder> deliveries,
			DeliveryAgent agent, Time currentTime, RelativeTime remainingWorkTime, VehicleType vehicle) {
		
		DayOfWeek day = currentTime.weekDay();
		
		if (isSunday(currentTime) || startsAfter1800(currentTime)) {
			return List.of();
		}

		Collection<Route> routes = this.routes.getOrDefault(day, List.of());
		if (routes.isEmpty()) {
			return List.of();
		}
		
		
		if (!day.equals(lastUpdate)) {
			fillRoutes(deliveries, routes);
			lastUpdate = day;
		}

		Route route = Collections.max(routes, Comparator.comparingInt(Route::size));
		this.routes.get(day).remove(route);

		ArrayList<ParcelActivityBuilder> assigned = new ArrayList<>();		
		Zone lastZone = agent.getDistributionCenter().getZone();
		Time time = currentTime;

		for (ParcelActivityBuilder delivery : route.getDeliveries()) {			
			float tripDuration = travelTime(lastZone, delivery.getZone(), time, vehicle.getMode());			
			delivery.withTripDuration(round(tripDuration));
			delivery.plannedAt(time.plusMinutes(round(tripDuration)));
			
			float deliveryDuration = delivery.estimateDuration();
			time = time.plusMinutes(round(tripDuration + deliveryDuration));
			
			assigned.add(delivery);
			lastZone = delivery.getZone();
		}
		
		
		return assigned;
	}

	private boolean startsAfter1800(Time currentTime) {
		return currentTime.isAfter(currentTime.startOfDay().plusHours(18));
	}

	private boolean isSunday(Time currentTime) {
		return currentTime.weekDay().equals(DayOfWeek.SUNDAY);
	}
	
	private float travelTime(Zone origin, Zone destination, Time time, Mode mode) {
		return impedance.getTravelTime(origin.getId(), destination.getId(), mode, time);
	}

	private void fillRoutes(Collection<ParcelActivityBuilder> deliveries, Collection<Route> routes) {
		Collection<ParcelActivityBuilder> remainingDeliveries = new ArrayList<>(deliveries);
		for (Route route : routes) {
			List<ParcelActivityBuilder> stops = remainingDeliveries.stream().filter(route::contains).collect(toList());
			stops.forEach(route::addParcels);
			remainingDeliveries.removeAll(stops);
		}
		if (!remainingDeliveries.isEmpty()) {
			System.out.println("Could not assign all parcels to routes: " + remainingDeliveries.size() + " remaining!");
		}
	}

}
