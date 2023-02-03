package edu.kit.ifv.mobitopp.simulation.distribution.delivery;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class ParcelActivity {
	
	protected final Collection<IParcel> parcels;
	protected final Collection<IParcel> pickUps;
	protected final ZoneAndLocation stopLocation;
	protected final Time plannedTime;
	protected final DeliveryVehicle vehicle;
	
	public ParcelActivity(ZoneAndLocation stopLocation, Collection<IParcel> parcels, Collection<IParcel> pickUps, DeliveryVehicle vehicle, Time plannedTime) {
		this.parcels = new ArrayList<>(parcels);
		this.pickUps = new ArrayList<>(pickUps);
		this.stopLocation = stopLocation;
		this.plannedTime = plannedTime;
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
		new ArrayList<>(parcels).forEach(p -> p.tryDelivery(currentTime, vehicle));
		new ArrayList<>(pickUps).forEach(p -> p.tryPickup(currentTime, vehicle));
	};


}
