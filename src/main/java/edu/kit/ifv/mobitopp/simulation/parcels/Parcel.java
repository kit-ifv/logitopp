package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parcel {

	private static int OID_CNT = 0;
	private final int oId = OID_CNT++;

	private PickUpParcelPerson person;
	private ParcelDestinationType destinationType;
	private Time plannedArrivalDate;
	private DistributionCenter distributionCenter;
	private String deliveryService;
	
	@Setter(value = lombok.AccessLevel.NONE)
	private ParcelState state = ParcelState.UNDEFINED;
	@Getter(value = lombok.AccessLevel.NONE)
	@Setter(value = lombok.AccessLevel.NONE)
	private DeliveryResults results;
	@Setter(value = lombok.AccessLevel.NONE)
	private int deliveryAttempts = 0;
	private Time deliveryTime = Time.future;

	public Parcel(PickUpParcelPerson person, ParcelDestinationType destination, Time plannedArrival,
		DistributionCenter distributionCenter, String deliveryService, DeliveryResults results) {
		
		this.destinationType = destination;
		this.plannedArrivalDate = plannedArrival;
		this.setDistributionCenter(distributionCenter);
		this.deliveryService = deliveryService;
		this.results = results;
		this.setPerson(person);
		
		this.results.logOrder(this);
	}

	public Location getLocation() {
		return this.destinationType.getLocation(this);
	}

	public Zone getZone() {
		return this.destinationType.getZone(this);
	}

	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(getZone(), getLocation());
	}

	
	public boolean isDelivered() {
		return this.state.equals(ParcelState.DELIVERED);
	}

	public boolean isOnDelivery() {
		return this.state.equals(ParcelState.ONDELIVERY);
	}

	public boolean isReturning() {
		return this.state.equals(ParcelState.RETURNING);
	}

	public boolean isUndefined() {
		return this.state.equals(ParcelState.UNDEFINED);
	}



	public ParcelState updateState(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt) {
		if (isAttempt) {
			this.deliveryAttempts++;
		}
		String before = this.state.name(); 
		this.state = this.state.nextState();
		System.out.println(before + " -> " + this.state.name());
		results.logChange(this, deliveryGuy, currentTime, isAttempt);
		return this.state;
	}

		
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
	
	
	private void setPerson(PickUpParcelPerson person) {
		if (this.person != null) {
			this.person.cancelOrder(this);
		}

		this.person = person;
		this.person.order(this);
	}

	private void setDistributionCenter(DistributionCenter distributionCenter) {
		if (this.distributionCenter != null) {
			this.distributionCenter.removeParcelOrder(this);
		}

		this.distributionCenter = distributionCenter;
		this.distributionCenter.addParcelOrder(this);
	}
	
	@Override
	public String toString() {
		return "Parcel(" + this.getOId() + ") for person " + this.getPerson().getOid()
			+ " to " + String.valueOf(this.getLocation()) + " (" + this.getDestinationType().toString() + ") at "
			+ this.getPlannedArrivalDate().toString();
	}

}
