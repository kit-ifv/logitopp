package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;


/**
 * Parcel holds data about the current state of delivery of a parcel order.
 */
public class Parcel {

	private static int OID_CNT = 0;
	@Getter private final int oId = OID_CNT++;

	@Getter private PickUpParcelPerson person;
	@Getter @Setter private ParcelDestinationType destinationType;
	@Getter @Setter private Time plannedArrivalDate;
	@Getter private DistributionCenter distributionCenter;
	@Getter @Setter private String deliveryService;

	@Getter private ParcelState state = ParcelState.UNDEFINED;
	private DeliveryResults results;
	@Getter private int deliveryAttempts = 0;
	@Getter private Time deliveryTime = Time.future;

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
	public Parcel(PickUpParcelPerson person, ParcelDestinationType destination, Time plannedArrival,
		DistributionCenter distributionCenter, String deliveryService, DeliveryResults results) {
		
		this.destinationType = destination;
		this.plannedArrivalDate = plannedArrival;
		this.setDistributionCenter(distributionCenter);
		this.deliveryService = deliveryService;
		this.results = results;
		this.setPerson(person);
		
		this.results.logChange(this, null, Time.start, false);
		this.results.logOrder(this);
	}

	/**
	 * Gets the delivery location of the parcel.
	 * Depends on the parcels {@link ParcelDestinationType}.
	 *
	 * @return the location
	 */
	public Location getLocation() {
		return this.destinationType.getLocation(this);
	}

	/**
	 * Gets the delivery zone of the parcel.
	 * Depends on the parcels {@link ParcelDestinationType}.
	 *
	 * @return the zone
	 */
	public Zone getZone() {
		return this.destinationType.getZone(this);
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
	 * Checks if the parcel is delivered.
	 *
	 * @return true, if is delivered
	 */
	public boolean isDelivered() {
		return this.state.equals(ParcelState.DELIVERED);
	}

	/**
	 * Checks if the parcel is on delivery.
	 *
	 * @return true, if is on delivery
	 */
	public boolean isOnDelivery() {
		return this.state.equals(ParcelState.ONDELIVERY);
	}

	/**
	 * Checks if the parcel is returning.
	 *
	 * @return true, if is returning
	 */
	public boolean isReturning() {
		return this.state.equals(ParcelState.RETURNING);
	}

	/**
	 * Checks if the parcel state is undefined
	 * (in distribution center or not yet arrived).
	 *
	 * @return true, if is undefined
	 */
	public boolean isUndefined() {
		return this.state.equals(ParcelState.UNDEFINED);
	}



	/**
	 * Updates the parcel state.
	 * If it is an delivery attempt, the number of attempts is increased.
	 * Logs the state change at the given {@link Time}.
	 *
	 * @param currentTime the current time
	 * @param deliveryGuy the delivery guy
	 * @param isAttempt the is attempt
	 * @return the parcel state
	 */
	public ParcelState updateState(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt) {
		if (isAttempt) {
			this.deliveryAttempts++;
		}
		this.state = this.state.nextState();
		results.logChange(this, deliveryGuy, currentTime, isAttempt);
		return this.state;
	}

		
	/**
	 * Deliver.
	 *
	 * @param currentTime the current time
	 * @param deliveryGuy the delivery guy
	 */
	public void deliver(Time currentTime, DeliveryPerson deliveryGuy) {
		this.deliveryTime = currentTime;
		deliveryGuy.delivered(this);
		
		String before = this.state.name(); 
		this.state = ParcelState.DELIVERED;
		System.out.println(before + " -> " + this.state.name());
				
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
	 * Sets the {@link DistributionCenter} from where the parcel will be delivered.
	 * Adds the parcel to the given {@link DistributionCenter}'s parcels.
	 * If the current distribution center is not null, remove the parcel from the current distribution center.
	 *
	 * @param distributionCenter the new distribution center
	 */
	private void setDistributionCenter(DistributionCenter distributionCenter) {
		if (this.distributionCenter != null) {
			this.distributionCenter.removeParcelOrder(this);
		}

		this.distributionCenter = distributionCenter;
		this.distributionCenter.addParcelOrder(this);
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

}
