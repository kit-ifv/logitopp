package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.time.Time;

public interface IParcel {

	public int getOId();
	
	public int getDeliveryAttempts();
	public Time getDeliveryTime();
	public boolean isPickUp();
	
	public ParcelState getState();
	
	public Time getPlannedArrivalDate();
	public void setPlannedArrivalDate(Time plannedArrivalDate);
		
	public ParcelAgent getProducer();
	public void setProducer(ParcelAgent producer);
	public ParcelAgent getConsumer();
	public void setConsumer(ParcelAgent producer);

	public ShipmentSize getShipmentSize();
	
	public Location getLocation();
	public Zone getZone();
	public ZoneAndLocation getZoneAndLocation();
	
//	public boolean couldBeDeliveredWith(IParcel other);
//	public boolean canBeDeliveredTogether(IParcel other);
	
	public boolean tryDelivery(Time currentTime, DeliveryVehicle vehicle);
	public boolean tryPickup(Time currentTime, DeliveryVehicle vehicle);
	public void load(Time time, DeliveryVehicle vehicle);
	public void unload(Time time, DeliveryVehicle vehicle);
	
//	public void returning(Time currentTime, DeliveryVehicle deliveryVehicle);
//	public void loaded(Time currentTime, DeliveryVehicle deliveryVehicle);
//	public void unloaded(Time currentTime, DeliveryVehicle deliveryVehicle);
	
	public RecipientType getRecipientType();

	
	default Collection<IParcel> getContainedParcels() {
		return List.of(this);
	}
}
