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
	
	public PlannedDeliveryTour(VehicleType vehicleType, RelativeTime plannedDuration, Time plannedAt) {
		this.stops = new ArrayList<>();
		this.vehicleType = vehicleType;
		this.plannedDuration = plannedDuration;
		this.plannedAt = plannedAt;
	}
	
	public PlannedDeliveryTour(VehicleType vehicleType, List<ParcelActivityBuilder> plannedStops, RelativeTime plannedDuration, Time plannedAt) {
		this(vehicleType, plannedDuration, plannedAt);
		addStops(plannedStops);
	}
	
	public void addStop(ParcelActivityBuilder plannedStop) {
		this.stops.add(plannedStop);
	}
	
	public void addStops(List<ParcelActivityBuilder> plannedStops) {
		this.stops.addAll(plannedStops);
	}
	
	public void dispatchTour(Time currentTime, DeliveryVehicle vehicle, ImpedanceIfc impedance) {
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
			time = time.plusMinutes(stop.estimateDuration());
			
			actualStops.add(stop.buildWorkerActivity());
			position = destination;
		}
		
		Time returnTime = time.plusMinutes(Math.round(impedance.getTravelTime(position.getId(), vehicle.getOwner().getZone().getId(), vehicle.getType().getMode(), time))); 
		
		ParcelArrivalScheduler scheduler = vehicle.getOwner().getScheduler();
		scheduler.dispatchVehicle(vehicle, returnTime);
		scheduler.dispatchParcelActivities(actualStops, currentTime);	
		
	}
	
	private void validate(DeliveryVehicle vehicle) {
		
		if (vehicle.getType() != this.vehicleType) {
			System.err.println("The planned vehicle type " + vehicleType.name() + " does not match the assigned vehicle type " + vehicle.getType().name());
		}
		
		// TODO validate capacity
	}
	
	

}
