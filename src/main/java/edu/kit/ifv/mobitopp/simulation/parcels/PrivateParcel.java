package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

/**
 * Parcel holds data about the current state of delivery of a parcel order.
 */
public class PrivateParcel extends BaseParcel {

	@Getter
	private PickUpParcelPerson person;
	@Getter
	@Setter
	private ParcelDestinationType destinationType;

	/**
	 * Instantiates a new parcel ordered by the given {@link PickUpParcelPerson}.
	 * The parcel will be delivered to the given {@link ParcelDestinationType}. The
	 * delivery is planned for the given {@link Time arrival date}. The parcel will
	 * be distributed by the given {@link DistributionCenter}. The given String
	 * delivery service can be used as a tag to assign a parcel to a specific
	 * delivery service company.
	 *
	 * @param person             the recipient
	 * @param destination        the destination type
	 * @param location           the location
	 * @param plannedArrival     the planned arrival date
	 * @param distributionCenter the distribution center
	 * @param shipmentSize 		 the shipment size
	 * @param results            the results to log state changes
	 */
	public PrivateParcel(PickUpParcelPerson person, ParcelDestinationType destination, ZoneAndLocation location,
			Time plannedArrival, DistributionCenter distributionCenter, ShipmentSize shipmentSize, DeliveryResults results) {
		super(location, plannedArrival, distributionCenter, results, shipmentSize);
		this.destinationType = destination;
		this.setPerson(person);

		this.logChange(Time.start, null, false);
		this.results.logPrivateOrder(this);
	}

	@Override
	protected void logChange(Time currentTime, DeliveryVehicle deliveryVehicle, boolean isAttempt) {
		this.results.logChange(this, deliveryVehicle, currentTime, isAttempt);
	}

	@Override
	protected Optional<RecipientType> canDeliver(Time currentTime, DeliveryVehicle deliveryVehicle) {
		return deliveryVehicle.getOwner().getPolicyProvider().forPrivate().canDeliver(this, currentTime);
	}

	@Override
	protected boolean updateParcelDelivery(Time currentTime, DeliveryVehicle deliveryVehicle) {
		return deliveryVehicle.getOwner().getPolicyProvider().forPrivate().updateParcelDelivery(this, currentTime);
	}

	/**
	 * Gets the delivery location of the parcel. Depends on the parcels
	 * {@link ParcelDestinationType}.
	 *
	 * @return the location
	 */
	public Location getLocation() {
		return this.destinationType.getLocation(this.person);
	}

	/**
	 * Gets the delivery zone of the parcel. Depends on the parcels
	 * {@link ParcelDestinationType}.
	 *
	 * @return the zone
	 */
	public Zone getZone() {
		return this.destinationType.getZone(this.person);
	}

	/**
	 * Gets the delivery zone and location of the parcel. Depends on the parcels
	 * {@link ParcelDestinationType}.
	 *
	 * @return the zone and location
	 */
	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(getZone(), getLocation());
	}


	@Override
	protected void onDeliverySuccess() {
		if (this.destinationType.equals(ParcelDestinationType.PACK_STATION)) {
			this.person.notifyParcelInPackStation(this);
		} else {
			this.person.receive(this);
		}
	}

	/**
	 * Sets the {@link PickUpParcelPerson recipient}. Adds the parcel to the given
	 * person's parcel orders. If the current recipient is not null, cancels the
	 * order of this parcel at the current recipient.
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
		return "Parcel(" + this.getOId() + ") for person " + this.getPerson().getOid() + " to "
				+ String.valueOf(this.getLocation()) + " (" + this.getDestinationType().toString() + ") at "
				+ this.getPlannedArrivalDate().toString();
	}

//	@Override
//	public boolean couldBeDeliveredWith(IParcel other) {
//
//		if (super.couldBeDeliveredWith(other)) {
//			
//			if (other instanceof PrivateParcel) {
//				PrivateParcel that = (PrivateParcel) other;
//				
//				if (that.getDestinationType().equals(this.getDestinationType())) {
//					
//					switch (this.getDestinationType()) {
//						case HOME: 
//							return this.getPerson().household().getOid() == that.getPerson().household().getOid();
//							
//						case PACK_STATION:
//							return true;
//						case WORK:
//							return true;
//							
//						default:
//							return false;
//					
//					}
//					
//				} else {
//					return false;
//				}
//				
//				
//			} else if (other instanceof BusinessParcel) {
//				return this.getDestinationType().equals(WORK);				
//				
//			} else {
//				return false;
//			}
//			
//		} else {
//			return false;
//		}
//
//	}

	@Override
	public ParcelAgent getConsumer() {
		return person;
	}

	@Override
	public void setConsumer(ParcelAgent producer) {
		throw new UnsupportedOperationException("Changing the recipient of a PrivateParcel is not supportet.");
	}

}
