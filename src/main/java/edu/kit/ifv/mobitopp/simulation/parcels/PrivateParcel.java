package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;


/**
 * Parcel holds data about the current state of delivery of a parcel order.
 */
public class PrivateParcel extends BaseParcel {

	@Getter private PickUpParcelPerson person;
	@Getter @Setter private ParcelDestinationType destinationType;

	/**
	 * Instantiates a new parcel ordered by the given {@link PickUpParcelPerson}.
	 * The parcel will be delivered to the given {@link ParcelDestinationType}.
	 * The delivery is planned for the given {@link Time arrival date}.
	 * The parcel will be distributed by the given {@link DistributionCenter}.
	 * The given String delivery service can be used as a tag to assign a parcel to a specific delivery service company.
	 *
	 * @param person the recipient
	 * @param destination the destination type
	 * @param plannedArrival the planned arrival date
	 * @param distributionCenter the distribution center
	 * @param deliveryService the delivery service
	 * @param results the results to log state changes
	 */
	public PrivateParcel(PickUpParcelPerson person, ParcelDestinationType destination, ZoneAndLocation location, Time plannedArrival, DistributionCenter distributionCenter,
			String deliveryService, DeliveryResults results) {
		super(location, plannedArrival, distributionCenter, deliveryService, results);
		this.destinationType = destination;
		this.setPerson(person);

		this.logChange(Time.start, null, false);
	}
	
	@Override
	protected void logChange(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt) {
		this.results.logChange(this, deliveryGuy, currentTime, isAttempt);
	}
	

	@Override
	protected Optional<RecipientType> canDeliver(Time currentTime) {
		return this.distributionCenter.getPolicyProvider().forPrivate().canDeliver(this, currentTime);
	}

	@Override
	protected boolean updateParcelDelivery(Time currentTime) {
		return this.distributionCenter.getPolicyProvider().forPrivate().updateParcelDelivery(this, currentTime);
	}

	/**
	 * Gets the delivery location of the parcel.
	 * Depends on the parcels {@link ParcelDestinationType}.
	 *
	 * @return the location
	 */
	public Location getLocation() {
		return this.destinationType.getLocation(this.person);
	}

	/**
	 * Gets the delivery zone of the parcel.
	 * Depends on the parcels {@link ParcelDestinationType}.
	 *
	 * @return the zone
	 */
	public Zone getZone() {
		return this.destinationType.getZone(this.person);
	}

	/**
	 * Gets the delivery zone and location of the parcel.
	 * Depends on the parcels {@link ParcelDestinationType}.
	 *
	 * @return the zone and location
	 */
	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(getZone(), getLocation());
	}



	/**
	 * Deliver.
	 *
	 * @param currentTime the current time
	 * @param deliveryGuy the delivery guy
	 */
	@Override
	protected void deliver(Time currentTime, DeliveryPerson deliveryGuy) {
		this.deliveryTime = currentTime;
		deliveryGuy.delivered(this);

		this.state = ParcelState.DELIVERED;

		if (this.destinationType.equals(ParcelDestinationType.PACK_STATION)) {
			this.person.notifyParcelInPackStation(this);
		} else {
			this.person.receive(this);
		}
	}
	
	
	/**
	 * Sets the {@link PickUpParcelPerson recipient}.
	 * Adds the parcel to the given person's parcel orders.
	 * If the current recipient is not null, cancels the order of this parcel at the current recipient.
	 *
	 * @param person the new person
	 */
	private void setPerson(PickUpParcelPerson person) {
		if (this.person != null) {
			this.person.cancelOrder(this);
		}

		this.person = person;
		this.person.order(this);
	}


	/**
	 * Returns a String representation of the parcel.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Parcel(" + this.getOId() + ") for person " + this.getPerson().getOid()
			+ " to " + String.valueOf(this.getLocation()) + " (" + this.getDestinationType().toString() + ") at "
			+ this.getPlannedArrivalDate().toString();
	}


	@Override
	public boolean canBeDeliveredTogether(IParcel other) {
		if (other == this) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (other.getLocation().equals(this.getLocation())) {
			return true; //TODO
		} else {
			return false;
		}
		
	}

}
