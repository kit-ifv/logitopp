package edu.kit.ifv.mobitopp.simulation.parcels;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

public class BusinessParcel extends BaseParcel {
	
	@Getter
	private final Business business;
	@Getter private final ParcelAgent consumer;

	public BusinessParcel(ZoneAndLocation location, Business business, ParcelAgent consumer, Time plannedArrival,
			ParcelAgent producer, ShipmentSize shipmentSize, DeliveryResults results) {
		super(location, plannedArrival, producer, results, shipmentSize);
		this.business = business;
		this.results.logBusinessOrder(this);
		this.results.logChange(this, null, Time.start, false);
		this.consumer = consumer;
	}

	@Override
	protected void logChange(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt) {
		this.results.logChange(this, deliveryGuy, currentTime, isAttempt);
	}

	@Override
	protected Optional<RecipientType> canDeliver(Time currentTime) {
		return this.producer.getPolicyProvider().forBusiness().canDeliver(this, currentTime);
	}

	@Override
	protected boolean updateParcelDelivery(Time currentTime) {
		return this.producer.getPolicyProvider().forBusiness().updateParcelDelivery(this, currentTime);
	}

	
	@Override
	public boolean couldBeDeliveredWith(IParcel other) {
		boolean sameLocation = super.couldBeDeliveredWith(other);
		
		if (!sameLocation) {
			return false;
		}
		
		if (other instanceof BusinessParcel) {
			return true;
			
		} else if (other instanceof PrivateParcel) {
			PrivateParcel that = (PrivateParcel) other;
			
			if (that.getDestinationType().equals(WORK)) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}
}
