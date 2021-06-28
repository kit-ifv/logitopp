package edu.kit.ifv.mobitopp.simulation.parcels;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.DELIVERED;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.ONDELIVERY;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelState.RETURNING;

import java.util.Arrays;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseParcel implements IParcel {

	private static int OID_CNT = 0;
	@Getter
	protected final int oId = OID_CNT++;

	@Getter	@Setter	protected Time plannedArrivalDate;
	@Getter	protected DistributionCenter distributionCenter;
	@Getter	@Setter	protected String deliveryService;
	@Getter	protected int deliveryAttempts = 0;
	@Getter	protected Time deliveryTime = Time.future;
	@Getter	protected ZoneAndLocation zoneAndLocation;
	@Getter protected RecipientType recipientType;

	protected final DeliveryResults results;
	@Getter protected ParcelState state = ParcelState.UNDEFINED;

	public BaseParcel(ZoneAndLocation location, Time plannedArrival, DistributionCenter distributionCenter,
			String deliveryService, DeliveryResults results) {
		this.results = results;
		this.plannedArrivalDate = plannedArrival;
		this.setDistributionCenter(distributionCenter);
		this.deliveryService = deliveryService;
		this.zoneAndLocation = location;

	}
	
	protected abstract void logChange(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt);
	protected abstract Optional<RecipientType> canDeliver(Time currentTime);
	protected abstract boolean updateParcelDelivery(Time currentTime);
	

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
	 * @param deliveryGuy the delivery guy
	 * @param isAttempt   the is attempt
	 * @return the parcel state
	 */
	private void updateState(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt) {
		this.state = this.state.nextState();
		this.logChange(currentTime, deliveryGuy, isAttempt);
	}

	/**
	 * Deliver.
	 *
	 * @param currentTime the current time
	 * @param deliveryGuy the delivery guy
	 */
	protected void deliver(Time currentTime, DeliveryPerson deliveryGuy) {
		this.deliveryTime = currentTime;
		deliveryGuy.delivered(this);

		this.state = ParcelState.DELIVERED;
	}

	/**
	 * Sets the {@link DistributionCenter} from where the parcel will be delivered.
	 * Adds the parcel to the given {@link DistributionCenter}'s parcels. If the
	 * current distribution center is not null, remove the parcel from the current
	 * distribution center.
	 *
	 * @param distributionCenter the new distribution center
	 */
	@Override
	public void setDistributionCenter(DistributionCenter distributionCenter) {
		if (this.distributionCenter != null) {
			this.distributionCenter.removeParcelOrder(this);
		}

		this.distributionCenter = distributionCenter;
		this.distributionCenter.addParcelOrder(this);
	}

	@Override
	public boolean tryDelivery(Time currentTime, DeliveryPerson deliveryGuy) {
		verifyState("tryDelivery", ONDELIVERY);
		this.deliveryAttempts++;
		Optional<RecipientType> recipient = this.canDeliver(currentTime);

		boolean success = recipient.isPresent();
		
		if (success) {
			this.recipientType = recipient.get();
			System.out.println(this.deliveryService + " successfully delivered " + this.oId + "(" + this.recipientType.name() + ", attempt " + this.deliveryAttempts + ")");
			this.deliver(currentTime, deliveryGuy);
		} else {
			System.out.println(this.deliveryService + " failed to deliver " + this.oId + "(attempt " + this.deliveryAttempts + ")");
			this.updateParcelDelivery(currentTime);
		}

		this.updateState(currentTime, deliveryGuy, true);

		if (success) {
			verifyState("tryDelivery result", DELIVERED);
		} else {
			verifyState("tryDelivery result", RETURNING);
		}

		return success;
	}

	@Override
	public void returning(Time currentTime, DeliveryPerson deliveryGuy) {
		verifyState("returning", ONDELIVERY);
		this.updateState(currentTime, deliveryGuy, false);
		verifyState("returning result", RETURNING);
	}

	@Override
	public void loaded(Time currentTime, DeliveryPerson deliveryGuy) {
		verifyState("loaded", ParcelState.UNDEFINED);
		this.updateState(currentTime, deliveryGuy, false);
		verifyState("loaded result", ONDELIVERY);
	}

	@Override
	public void unloaded(Time currentTime, DeliveryPerson deliveryGuy) {
		verifyState("unloaded", ONDELIVERY, RETURNING);
		this.updateState(currentTime, deliveryGuy, false);
		verifyState("unloaded result", ParcelState.UNDEFINED);
	}

	protected void verifyState(String operation, ParcelState ... states ) {
		if (!Arrays.asList(states).contains(this.state)) {
			throw new IllegalStateException(operation + " expects the parcel state " + state.name()
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
	

	@Override
	public boolean canBeDeliveredTogether(IParcel other) {
		if (other == null) {
			return this.couldBeDeliveredWith(other);
		} else {
			return this.couldBeDeliveredWith(other) && other.couldBeDeliveredWith(this);
		}
	}
	
	@Override
	public boolean couldBeDeliveredWith(IParcel other) {
		if (other == this) {
			return true;
		}

		if (other == null) {
			return false;
		}

		return other.getLocation().equals(this.getLocation());
	}
	
}
