package edu.kit.ifv.mobitopp.simulation.distribution.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class ParcelActivityBuilder {
	protected DeliveryVehicle deliveryVehicle;
	protected Time plannedArrivalTime;
	protected int no;
	protected int tripDuration;
	protected double distance;
	
	protected final List<IParcel> allParcels;
	protected final List<IParcel> deliveries;
	protected final List<IParcel> pickUps;
	protected final ZoneAndLocation stopLocation;
	
	public ParcelActivityBuilder(Collection<IParcel> parcels, ZoneAndLocation stopLocation) {
		this.allParcels = new ArrayList<>(parcels);
		this.deliveries = new ArrayList<>();
		this.pickUps = new ArrayList<>();
		this.stopLocation = stopLocation;

		parcels.stream().filter(IParcel::isPickUp).forEach(pickUps::add);
		parcels.stream().filter(p -> !p.isPickUp()).forEach(deliveries::add);
	}
	
	public int estimateDuration() {
		return (int) this.deliveryVehicle.getOwner().getDurationModel().estimateDuration(deliveryVehicle, getAllParcels()); //TODO distinguish pickups and deliveries in duration model?
	}

	public ParcelActivityBuilder plannedAt(Time time) {
		this.plannedArrivalTime = time;
		return this;
	}
	
	public ParcelActivityBuilder by(DeliveryVehicle vehicle) {
		this.deliveryVehicle = vehicle;
		return this;
	}
	
	public ParcelActivityBuilder asStopNo(int no) {
		this.no = no;
		return this;
	}
	
	public ParcelActivityBuilder afterTrip(double distance, int duration) {
		this.distance = distance;
		this.tripDuration = duration;
		return this;
	}
	
	public ParcelActivity buildWorkerActivity() {
		return new ParcelActivity(no, stopLocation, deliveries, pickUps, deliveryVehicle, plannedArrivalTime, distance, tripDuration, estimateDuration());		
	}	
		

	public Zone getZone() {
		return this.getStopLocation().zone();
	}

	public Location getLocation() {
		return this.getStopLocation().location();
	}

	public int size() {
		return this.getAllParcels().size();
	}
	
	public int volume() {
		return getAllParcels().stream().mapToInt(p -> p.getShipmentSize().getVolume(p)).sum();
	}
	
}
