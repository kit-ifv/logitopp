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
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryTourAssignmentStrategy;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class PrecalculatedTourAssignment implements DeliveryTourAssignmentStrategy {
	
	private final Map<DayOfWeek, Collection<Route>> routes;
	private DayOfWeek lastUpdate = null;
	private final Mode mode;
	
	public PrecalculatedTourAssignment(Map<DayOfWeek, CsvFile> routeFiles, String algorithm, Mode mode, String name) {
		this.routes = new HashMap<>();
		routeFiles.forEach((day,file) -> this.routes.put(day, Route.parseRoutes(file, algorithm, name+"_"+day)));
		this.mode = mode;
	}
	

	@Override
	public List<DeliveryActivityBuilder> assignParcels(Collection<DeliveryActivityBuilder> deliveries,
			DeliveryPerson person, Time currentTime, RelativeTime remainingWorkTime) {
		
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

		ArrayList<DeliveryActivityBuilder> assigned = new ArrayList<>();		
		Zone lastZone = person.getDistributionCenter().getZone();
		Time time = currentTime;

		for (DeliveryActivityBuilder delivery : route.getDeliveries()) {			
			float tripDuration = travelTime(person, lastZone, delivery.getZone(), time);			
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
	
	private float travelTime(DeliveryPerson person, Zone origin, Zone destination, Time time) {
		return person.options().impedance().getTravelTime(origin.getId(), destination.getId(), mode, time);
	}

	private void fillRoutes(Collection<DeliveryActivityBuilder> deliveries, Collection<Route> routes) {
		Collection<DeliveryActivityBuilder> remainingDeliveries = new ArrayList<>(deliveries);
		for (Route route : routes) {
			List<DeliveryActivityBuilder> stops = remainingDeliveries.stream().filter(route::contains).collect(toList());
			stops.forEach(route::addParcels);
			remainingDeliveries.removeAll(stops);
		}
		if (!remainingDeliveries.isEmpty()) {
			System.out.println("Could not assign all parcels to routes: " + remainingDeliveries.size() + " remaining!");
		}
	}

}
