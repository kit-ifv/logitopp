package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

public interface IParcel {

	public int getOId();
	
	public int getDeliveryAttempts();
	public Time getDeliveryTime();
	
	public ParcelState getState();
	
	public Time getPlannedArrivalDate();
	public void setPlannedArrivalDate(Time plannedArrivalDate);
		
	public DistributionCenter getDistributionCenter();
	public void setDistributionCenter(DistributionCenter distributionCenter);

	public ShipmentSize getShipmentSize();
	
	public Location getLocation();
	public Zone getZone();
	public ZoneAndLocation getZoneAndLocation();
	
	public boolean couldBeDeliveredWith(IParcel other);
	public boolean canBeDeliveredTogether(IParcel other);
	
	public boolean tryDelivery(Time currentTime, DeliveryPerson deliveryGuy);	
	public void returning(Time currentTime, DeliveryPerson deliveryGuy);
	public void loaded(Time currentTime, DeliveryPerson deliveryGuy);
	public void unloaded(Time currentTime, DeliveryPerson deliveryGuy);
	
	public RecipientType getRecipientType();
	
}
