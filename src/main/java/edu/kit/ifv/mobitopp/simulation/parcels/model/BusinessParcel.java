package edu.kit.ifv.mobitopp.simulation.parcels.model;

import static edu.kit.ifv.mobitopp.simulation.parcels.model.ParcelDestinationType.WORK;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.business.Business;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

public class BusinessParcel extends BaseParcel {
	
	@Getter
	private final Business business;

	public BusinessParcel(ZoneAndLocation location, Business business, Time plannedArrival, DistributionCenter distributionCenter,
			String deliveryService, ShipmentSize shipmentSize, DeliveryResults results) {
		super(location, plannedArrival, distributionCenter, results, shipmentSize);
		this.business = business;
		this.results.logBusinessOrder(this);
		this.results.logChange(this, null, Time.start, false);
	}

	@Override
	protected void logChange(Time currentTime, DeliveryPerson deliveryGuy, boolean isAttempt) {
		this.results.logChange(this, deliveryGuy, currentTime, isAttempt);
	}

	@Override
	protected Optional<RecipientType> canDeliver(Time currentTime) {
		return distributionCenter.getPolicyProvider().forBusiness().canDeliver(this, currentTime);
	}

	@Override
	protected boolean updateParcelDelivery(Time currentTime) {
		return distributionCenter.getPolicyProvider().forBusiness().updateParcelDelivery(this, currentTime);
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
