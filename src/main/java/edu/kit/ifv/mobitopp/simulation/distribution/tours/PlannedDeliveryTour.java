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
	
	 
	public PlannedDeliveryTour(VehicleType vehicleType, RelativeTime plannedDuration, Time plannedAt, boolean replan) {
		this.stops = new ArrayList<>();
		this.preparedStops = new  ArrayList<>();
		this.vehicleType = vehicleType;
		this.plannedDuration = plannedDuration;
		this.plannedAt = plannedAt;
		this.replan = replan;
	}
	
	public PlannedDeliveryTour(VehicleType vehicleType, List<ParcelActivityBuilder> plannedStops, RelativeTime plannedDuration, Time plannedAt, boolean replan) {
		this(vehicleType, plannedDuration, plannedAt, replan);
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
		Zone position = vehicle.getOwner().getZone();
		int stopNo = 1;
		
		for (ParcelActivityBuilder stop : stops) {
			Zone destination = stop.getZone();
			
			float tripDuration = impedance.getTravelTime(position.getId(), destination.getId(), vehicle.getType().getMode(), time);
			time = time.plusMinutes(Math.round(tripDuration));
			
			stop.by(vehicle).plannedAt(time).asStopNo(stopNo++);
			time = time.plusMinutes(stop.getDeliveryMinutes());
			
			actualStops.add(stop.buildWorkerActivity());
			position = destination;
		}
		
		this.preparedStops.addAll(actualStops);
		
		Time returnTime = time.plusMinutes(Math.round(impedance.getTravelTime(position.getId(), vehicle.getOwner().getZone().getId(), vehicle.getType().getMode(), time))); 
		return returnTime;
		
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
	
	

}
