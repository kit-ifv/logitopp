package edu.kit.ifv.mobitopp.simulation.distribution.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class ParcelActivity {
	
	protected final int no;
	protected final int tourId;
	protected final Collection<IParcel> parcels;
	protected final Collection<IParcel> pickUps;
	protected final ZoneAndLocation stopLocation;
	protected final Time plannedTime;
	protected final double distance;
	protected final int tripDuration;
	protected final int deliveryDuration;
	protected final DeliveryVehicle vehicle;
	
	public ParcelActivity(int no, int tourId, ZoneAndLocation stopLocation, Collection<IParcel> parcels, Collection<IParcel> pickUps, DeliveryVehicle vehicle, Time plannedTime, double distance, int tripDuration, int deliveryDuration) {
		this.no = no;
		this.tourId = tourId;
		this.parcels = new ArrayList<>(parcels);
		this.pickUps = new ArrayList<>(pickUps);
		this.stopLocation = stopLocation;
		this.plannedTime = plannedTime;
		
		this.distance = distance;
		this.tripDuration = tripDuration;
		this.deliveryDuration = deliveryDuration;
		
		this.vehicle = vehicle;
	}
	
	public ZoneAndLocation getZoneAndLocation() {
		return this.stopLocation;
	}
	
	public Time startDate() {
		return this.plannedTime;
	}
	
	public void prepareAvtivity(Time currentTime) { //TODO in prepare -> remove parcels and requests from operator 
		DistributionCenter owner = vehicle.getOwner();
		
		parcels.forEach(owner::removeParcel);
		parcels.forEach(p -> p.load(currentTime, vehicle));
		pickUps.forEach(owner::removePickupRequest);
	};
	
	public void executeActivity(Time currentTime) {
		//todo executed flag for protection of reexecution
		//TODO why new arraylist? because they are removed from original list when successful?
		
		int successDelivery =  new ArrayList<>(parcels).stream().mapToInt(p -> p.tryDelivery(currentTime, vehicle) ? 1 : 0).sum();
		int successPickUp = new ArrayList<>(pickUps).stream().mapToInt(p -> p.tryPickup(currentTime, vehicle) ? 1 : 0).sum();
		
		vehicle.getOwner().getResults().logStopEvent(vehicle, currentTime, no, tourId, getParcels().size(), successDelivery, getPickUps().size(), successPickUp, stopLocation, distance, tripDuration, deliveryDuration);
	};
	
	public Collection<IParcel> getAllParcels() {
		List<IParcel> list = new ArrayList<>(parcels);
		list.addAll(pickUps);
		return list;
	}
	
	@Override
	public String toString() {
		return "Delivery " + no + " by " + vehicle + " at " + plannedTime + ":" + getAllParcels().stream().map(IParcel::getOId).collect(Collectors.toList());
	}


}
