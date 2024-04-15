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

/**
 *  This is a wrapper of a return tour to represent the box while it is on the bike during the delivery tour.
 *  Once it is delivered to the bike depot, the wrapped return tour is added to the depots planned tours.
 **/

public class BoxOnBike implements IParcel {

    private final ParcelBox returnTour;

    public BoxOnBike(ParcelBox returnTour) {
        this.returnTour = returnTour;
    }

    @Override
    public int getOId() {
        return returnTour.getOId();
    }

    @Override
    public int getDeliveryAttempts() {
        return 1;
    }

    @Override
    public Time getDeliveryTime() {
        return null;
    }

    @Override
    public boolean isPickUp() {
        return false;
    }

    @Override
    public ParcelState getState() {
        return ParcelState.ONDELIVERY;
    }

    @Override
    public Time getPlannedArrivalDate() {
        throw new UnsupportedOperationException("BoxOnBike planned date should not be accessed!");
    }

    @Override
    public void setPlannedArrivalDate(Time plannedArrivalDate) {
        throw new UnsupportedOperationException("BoxOnBike planned date is not be settable!");
    }

    @Override
    public ParcelAgent getProducer() {
        return returnTour.depot();
    }

    @Override
    public void setProducer(ParcelAgent producer) {
        throw new UnsupportedOperationException("BoxOnBike producer is not be settable!");
    }

    @Override
    public ParcelAgent getConsumer() {
        return returnTour.depot();
    }

    @Override
    public void setConsumer(ParcelAgent producer) {
        throw new UnsupportedOperationException("BoxOnBike consumer is not be settable!");
    }

    @Override
    public ShipmentSize getShipmentSize() {
        return ShipmentSize.CONTAINER;
    }

    @Override
    public Location getLocation() {
        return returnTour.depot().getLocation();
    }

    @Override
    public Zone getZone() {
        return returnTour.depot().getZone();
    }

    @Override
    public ZoneAndLocation getZoneAndLocation() {
        return returnTour.depot().getZoneAndLocation();
    }

    @Override
    public boolean tryDelivery(Time currentTime, DeliveryVehicle vehicle) {

        returnTour.depot().getStorage().addPlannedTour(returnTour);

        System.out.println("BoxOnBike was delivered to " + returnTour.depot() + ": " + this);

        return true;
    }

    @Override
    public boolean tryPickup(Time currentTime, DeliveryVehicle vehicle) {
        throw new UnsupportedOperationException("BoxOnBike should not be picked up!");
    }

    @Override
    public void load(Time time, DeliveryVehicle vehicle) {  }

    @Override
    public void unload(Time time, DeliveryVehicle vehicle) {
        throw new UnsupportedOperationException("BoxOnBike should not be unloaded up!");
    }

    @Override
    public RecipientType getRecipientType() {
        return RecipientType.DISTRIBUTION_CENTER;
    }

    public void addReturning(IParcel parcel) {
        returnTour.addReturning(parcel);
    }

    public void addPickedUp(IParcel parcel) {
        returnTour.addPickedUp(parcel);
    }
}
