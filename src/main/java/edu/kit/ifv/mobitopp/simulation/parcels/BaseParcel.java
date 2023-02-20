package edu.kit.ifv.mobitopp.simulation.parcels;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.DELIVERED;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.ONDELIVERY;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.RETURNING;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.UNDEFINED;

import java.util.Arrays;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseParcel implements IParcel {

	private static int OID_CNT = 0;
	@Getter
	protected final int oId = OID_CNT++;

	@Getter	@Setter	protected Time plannedArrivalDate;
	@Getter	protected ParcelAgent producer;
	@Getter	protected int deliveryAttempts = 0;
	@Getter	protected Time deliveryTime = Time.future;
	@Getter	protected ZoneAndLocation zoneAndLocation;
	@Getter protected RecipientType recipientType;
	@Getter protected ShipmentSize shipmentSize;
	@Getter protected boolean isPickUp;

	protected final DeliveryResults results;
	@Getter protected ParcelState state = ParcelState.UNDEFINED;

	public BaseParcel(ZoneAndLocation location, Time plannedArrival, ParcelAgent producer,
			DeliveryResults results, ShipmentSize shipmentSize, boolean isPickUp) {
		this.results = results;
		this.plannedArrivalDate = plannedArrival;
		this.setProducer(producer);
		this.zoneAndLocation = location;
		this.shipmentSize = shipmentSize;
		this.isPickUp = isPickUp;
	}
	
	protected abstract void logChange(Time currentTime, DeliveryVehicle deliveryVehicle, boolean isAttempt);
	protected abstract Optional<RecipientType> canDeliver(Time currentTime, DeliveryVehicle deliveryVehicle);
	protected abstract boolean updateParcelDelivery(Time currentTime, DeliveryVehicle deliveryVehicle);
	

	public Zone getZone() {
		return this.zoneAndLocation.zone();
	}
	
	public Location getLocation() {
		return this.zoneAndLocation.location();
	}
	
	/**
	 * Updates the parcel state. If it is an delivery attempt, the number of
	 * attempts is increased. Logs the state change at the given {@link Time}.
	 *
	 * @param currentTime the current time
	 * @param deliveryVehicle the delivery vehicle
	 * @param isAttempt   the is attempt
	 * @return the parcel state
	 */
	private void updateState(Time currentTime, DeliveryVehicle deliveryVehicle, boolean isAttempt) {
		this.state = this.state.nextState();
		this.logChange(currentTime, deliveryVehicle, isAttempt);
	}

	/**
	 * Deliver.
	 *
	 * @param currentTime the current time
	 * @param deliveryVehicle the delivery vehicle
	 */
	protected void setDelivered(Time currentTime, DeliveryVehicle deliveryVehicle) {
		this.deliveryTime = currentTime;

		this.state = ParcelState.DELIVERED;
	}

	/**
	 * Sets the {@link ParcelAgent producer} from where the parcel will be delivered.
	 * Adds the parcel to the given {@link ParcelAgent producer}'s parcels. If the
	 * current producer is not null, remove the parcel from the current
	 * producer.
	 *
	 * @param producer the new producer
	 */
	@Override
	public void setProducer(ParcelAgent producer) {
		this.producer = producer;
	}

	@Override
	public boolean tryDelivery(Time currentTime, DeliveryVehicle deliveryVehicle) {
		verifyState("tryDelivery", ONDELIVERY);
		this.deliveryAttempts++;
		Optional<RecipientType> recipient = this.canDeliver(currentTime, deliveryVehicle);

		boolean success = recipient.isPresent();
		
		if (success) {
			this.recipientType = recipient.get();
//			System.out.println(this.producer.toString() + " successfully delivered " + this.oId + "(" + this.recipientType.name() + ", attempt " + this.deliveryAttempts + ")");
			
			setDelivered(currentTime, deliveryVehicle);
			onDeliverySuccess();
			
		} else {
//			System.out.println(this.producer.toString() + " failed to deliver " + this.oId + "(attempt " + this.deliveryAttempts + ")");
			
			onDeliveryFailure(currentTime, deliveryVehicle);
		}

		this.updateState(currentTime, deliveryVehicle, true);

		if (success) {
			verifyState("tryDelivery result", DELIVERED);
		} else {
			verifyState("tryDelivery result", RETURNING);
		}

		return success;
	}
	
	@Override
	public boolean tryPickup(Time currentTime, DeliveryVehicle vehicle) {
		
		//TODO introduce canPickUp in policy
		Optional<RecipientType> recipient = this.canDeliver(currentTime, vehicle);

		boolean success = recipient.isPresent();
		if (success) {
			this.recipientType = recipient.get();
//			System.out.println(vehicle.toString() + " successfully picked up " + this.oId + "(" + this.recipientType.name() + ", attempt " + this.deliveryAttempts + ") at " + producer.toString());
			
			setOnDelivery(currentTime, vehicle);
			onPickUpSuccess(currentTime, vehicle);
			
		} else {
//			System.out.println(vehicle.toString() + " failed to pick up " + this.oId + "(attempt " + this.deliveryAttempts + ") at " + producer.toString());
			onPickUpFailure(vehicle);
		}
		
		if (success) {
			verifyState("tryPickup result", ONDELIVERY);
		} else {
			verifyState("tryPickup result", UNDEFINED);
		}
		
		return success;
	}
	
	@Override
	public void load(Time time, DeliveryVehicle vehicle) {
		setOnDelivery(time, vehicle);
	}
	
	@Override
	public void unload(Time time, DeliveryVehicle vehicle) {
		setUndefined(time, vehicle);
	}
	
	
	
	protected void onDeliverySuccess() {
		this.getConsumer().addDelivered(this); //TODO override in wrapping parcel shipment
	}
	
	protected void onDeliveryFailure(Time currentTime, DeliveryVehicle vehicle) {
		updateParcelDelivery(currentTime, vehicle);
		vehicle.addReturningParcel(this);
	}
	
	protected void onPickUpSuccess(Time currentTime, DeliveryVehicle vehicle) {
		vehicle.addPickedUpParcel(this);
		this.getProducer().removeParcel(this);
	}
	
	protected void onPickUpFailure(DeliveryVehicle vehicle) {
		vehicle.getOwner().requestPickup(this);
	}

	
	

	protected void setReturning(Time currentTime, DeliveryVehicle deliveryVehicle) {
		verifyState("returning", ONDELIVERY);
		this.updateState(currentTime, deliveryVehicle, false);
		verifyState("returning result", RETURNING);
	}

	protected void setOnDelivery(Time currentTime, DeliveryVehicle deliveryVehicle) {
		verifyState("loaded", UNDEFINED);
		this.updateState(currentTime, deliveryVehicle, false);		
		verifyState("loaded result", ONDELIVERY);
	}

	protected void setUndefined(Time currentTime, DeliveryVehicle deliveryVehicle) {
		verifyState("unloaded", ONDELIVERY, RETURNING);
		this.updateState(currentTime, deliveryVehicle, false);
		verifyState("unloaded result", UNDEFINED);
	}

	protected void verifyState(String operation, ParcelState ... states ) {
		if (!Arrays.asList(states).contains(this.state)) {
			throw new IllegalStateException(operation + " expects one of the parcel states: " + Arrays.toString(states)
					+ " (but current state is " + this.state.name() + ")");
		}
	}

	/**
	 * Returns a String representation of the parcel.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Parcel(" + this.getOId() + ")  to " + String.valueOf(this.getLocation()) + " at "
				+ this.getPlannedArrivalDate().toString();
	}
	

//	@Override
//	public boolean canBeDeliveredTogether(IParcel other) {
//		if (other == null) {
//			return this.couldBeDeliveredWith(other);
//		} else {
//			return this.couldBeDeliveredWith(other) && other.couldBeDeliveredWith(this);
//		}
//	}
//	
//	@Override
//	public boolean couldBeDeliveredWith(IParcel other) {
//		if (other == this) {
//			return true;
//		}
//
//		if (other == null) {
//			return false;
//		}
//
//		return other.getLocation().equals(this.getLocation());
//	}
	
}
