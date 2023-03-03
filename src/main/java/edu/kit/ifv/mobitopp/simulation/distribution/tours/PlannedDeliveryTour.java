package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.ParcelArrivalScheduler;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.fleet.VehicleType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

public class PlannedDeliveryTour {
	
	@Getter private final List<ParcelActivityBuilder> stops;
	@Getter private final VehicleType vehicleType;
	@Getter private final RelativeTime plannedDuration;
	@Getter private final Time plannedAt;
	
	private final ImpedanceIfc impedance;
	
	public PlannedDeliveryTour(VehicleType vehicleType, RelativeTime plannedDuration, Time plannedAt, ImpedanceIfc impedance) {
		this.stops = new ArrayList<>();
		this.vehicleType = vehicleType;
		this.plannedDuration = plannedDuration;
		this.plannedAt = plannedAt;
		this.impedance = impedance;
	}
	
	public PlannedDeliveryTour(VehicleType vehicleType, List<ParcelActivityBuilder> plannedStops, RelativeTime plannedDuration, Time plannedAt, ImpedanceIfc impedance) {
		this(vehicleType, plannedDuration, plannedAt, impedance);
		addStops(plannedStops);
	}
	
	public void addStop(ParcelActivityBuilder plannedStop) {
		this.stops.add(plannedStop);
	}
	
	public void addStops(List<ParcelActivityBuilder> plannedStops) {
		this.stops.addAll(plannedStops);
	}
	
	public void dispatchTour(Time currentTime, DeliveryVehicle vehicle) {
		validate(vehicle);
		
		List<ParcelActivity> actualStops = new ArrayList<>();
		
		Time time = Time.start.plus(currentTime.fromStart());
		Zone position = depot(vehicle);
		
		int stopNo = 1;
		int totalTripTime = 0;
		int totalDeliveryTime = 0;
		int totalDistance = 0;
		int totalDeliveries = 0;
		int totalPickups = 0;
		
		for (ParcelActivityBuilder stop : stops) {
			Zone destination = stop.getZone();
			
			int tripDuration = travelTime(position, destination, time);
			double distance = distance(position, destination);
			int deliveryDuration = stop.estimateDuration();
			
			time = time.plusMinutes(tripDuration);
			stop.by(vehicle).plannedAt(time).asStopNo(stopNo++).afterTrip(distance, tripDuration);
			actualStops.add(stop.buildWorkerActivity());

			time = time.plusMinutes(deliveryDuration);
			position = destination;
			
			totalTripTime += tripDuration;
			totalDistance += distance;
			totalDeliveryTime += deliveryDuration;
			totalDeliveries += stop.getDeliveries().size();
			totalPickups += stop.getPickUps().size();
		}
		
		int returnDuration = travelTime(position, depot(vehicle), time);
		totalTripTime += returnDuration;
		totalDistance += distance(position, depot(vehicle));
		
		Time returnTime = time.plusMinutes(returnDuration); 
		
		ParcelArrivalScheduler scheduler = vehicle.getOwner().getScheduler();
		scheduler.dispatchVehicle(vehicle, returnTime);
		scheduler.dispatchParcelActivities(actualStops, currentTime);
		
		vehicle.getOwner().getResults().logLoadEvent(vehicle, currentTime, totalDeliveries, totalPickups, vehicle.getOwner().getZoneAndLocation(), totalDistance, totalTripTime, totalDeliveryTime);
	}

	private Zone depot(DeliveryVehicle vehicle) {
		return vehicle.getOwner().getZone();
	}

	private float distance(Zone position, Zone destination) {
		return impedance.getDistance(position.getId(), destination.getId());
	}

	private int travelTime(Zone origin, Zone destination, Time time) {
		return Math.round(impedance.getTravelTime(origin.getId(), destination.getId(), vehicleType.getMode(), time));
	}
	
	private void validate(DeliveryVehicle vehicle) {
		
		if (vehicle.getType() != this.vehicleType) {
			System.err.println("The planned vehicle type " + vehicleType.name() + " does not match the assigned vehicle type " + vehicle.getType().name());
		}
		
		// TODO validate capacity
	}
	
	

}
