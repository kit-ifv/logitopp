package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class PlannedDeliveryTour {
	
	private final List<ParcelActivityBuilder> stops;
	private final List<ParcelActivity> preparedStops;
	private final VehicleType vehicleType;
	private final RelativeTime plannedDuration;
	private final Time plannedAt;
	private final boolean replan;
	private final ImpedanceIfc impedance;
	
	public PlannedDeliveryTour(VehicleType vehicleType, RelativeTime plannedDuration, Time plannedAt, boolean replan, ImpedanceIfc impedance) {
		this.stops = new ArrayList<>();
		this.preparedStops = new  ArrayList<>();
		this.vehicleType = vehicleType;
		this.plannedDuration = plannedDuration;
		this.plannedAt = plannedAt;
		this.replan = replan;
		this.impedance = impedance;
	}
	
	public PlannedDeliveryTour(VehicleType vehicleType, List<ParcelActivityBuilder> plannedStops, RelativeTime plannedDuration, Time plannedAt, boolean replan, ImpedanceIfc impedance) {
		this(vehicleType, plannedDuration, plannedAt, replan, impedance);
		addStops(plannedStops);
	}
	
	public void addStop(ParcelActivityBuilder plannedStop) {
		this.stops.add(plannedStop);
	}
	
	public void addStops(List<ParcelActivityBuilder> plannedStops) {
		this.stops.addAll(plannedStops);
	}
	
	public Time prepare(Time currentTime, DeliveryVehicle vehicle, ImpedanceIfc impedance) {
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
			int deliveryDuration = stop.getDeliveryMinutes();
			
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
		
		this.preparedStops.addAll(actualStops);
		
		int returnDuration = travelTime(position, depot(vehicle), time);
		totalTripTime += returnDuration;
		totalDistance += distance(position, depot(vehicle));
		
		Time returnTime = time.plusMinutes(returnDuration); 
		
		vehicle.getOwner().getResults().logLoadEvent(vehicle, currentTime, stops.size(), totalDeliveries, totalPickups, vehicle.getOwner().getZoneAndLocation(), totalDistance, totalTripTime, totalDeliveryTime);
	
		return returnTime;
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
	
	public Collection<IParcel> getDeliveryParcels() {
		return this.stops.stream().flatMap(stop -> stop.getDeliveries().stream()).collect(toList());
	}
	
	public Collection<IParcel> getPickUpRequests() {
		return this.stops.stream().flatMap(stop -> stop.getPickUps().stream()).collect(toList());
	}
	
	@Override
	public String toString() {
		return "Planned tour: " + this.vehicleType.name() + ", " + this.stops.size() + " stops";
	}

}
