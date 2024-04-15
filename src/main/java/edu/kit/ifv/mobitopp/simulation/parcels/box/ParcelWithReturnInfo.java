package edu.kit.ifv.mobitopp.simulation.parcels.box;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelState;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;

public class ParcelWithReturnInfo implements IParcel {
	
	private final BoxOnBike returnTour;
	private final IParcel delegate;
	
	public ParcelWithReturnInfo(BoxOnBike returnTour, IParcel delegate) {
		this.returnTour = returnTour;
		this.delegate = delegate;
	}
	
	
	@Override
	public int getOId() {
		return delegate.getOId();
	}
	
	@Override
	public int getDeliveryAttempts() {
		return delegate.getDeliveryAttempts();
	}
	
	@Override
	public Time getDeliveryTime() {
		return delegate.getDeliveryTime();
	}
	
	@Override
	public boolean isPickUp() {
		return delegate.isPickUp();
	}
	
	@Override
	public ParcelState getState() {
		return delegate.getState();
	}
	
	@Override
	public Time getPlannedArrivalDate() {
		return delegate.getPlannedArrivalDate();
	}
	
	@Override
	public void setPlannedArrivalDate(Time plannedArrivalDate) {
		delegate.setPlannedArrivalDate(plannedArrivalDate);
	}
	
	@Override
	public ParcelAgent getProducer() {
		return delegate.getProducer();
	}
	
	@Override
	public void setProducer(ParcelAgent producer) {
		delegate.setProducer(producer);
	}
	
	@Override
	public ParcelAgent getConsumer() {
		return delegate.getConsumer();
	}
	
	@Override
	public void setConsumer(ParcelAgent producer) {
		delegate.setConsumer(producer);
	}
	
	@Override
	public ShipmentSize getShipmentSize() {
		return delegate.getShipmentSize();
	}
	
	@Override
	public Location getLocation() { //todo maybe use return tour
		return delegate.getLocation();
	}
	
	@Override
	public Zone getZone() {
		return delegate.getZone();
	}
	
	@Override
	public ZoneAndLocation getZoneAndLocation() {
		return delegate.getZoneAndLocation();
	}
	
	@Override
	public boolean tryDelivery(Time currentTime, DeliveryVehicle vehicle) {
		boolean success = delegate.tryDelivery(currentTime, vehicle);
		
		if (!success) {
			vehicle.getReturningParcels().remove(delegate);
			returnTour.addReturning(delegate);
		}
//		registerReturnTourBox(vehicle);
		
		return success;
	}

	@Override
	public boolean tryPickup(Time currentTime, DeliveryVehicle vehicle) {
		boolean success = delegate.tryPickup(currentTime, vehicle);
		
		if (success) {
			vehicle.getPickedUpParcels().remove(delegate);
			returnTour.addPickedUp(delegate);
		}
//		registerReturnTourBox(vehicle);
		
		return success;
	}
	
	private void registerReturnTourBox(DeliveryVehicle vehicle) {
		if (!vehicle.getPickedUpParcels().contains(returnTour)) {
			returnTour.load(getDeliveryTime(), vehicle);
			vehicle.addPickedUpParcel(returnTour);
		}
	}
	
	@Override
	public void load(Time time, DeliveryVehicle vehicle) {
		registerReturnTourBox(vehicle);
		delegate.load(time, vehicle);
	}
	
	@Override
	public void unload(Time time, DeliveryVehicle vehicle) {
		delegate.unload(time, vehicle);
	}
	
	@Override
	public RecipientType getRecipientType() {
		return delegate.getRecipientType();
	}
	
	@Override
	public String toString() {
		return "(Ret)" + delegate.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}
	

}
