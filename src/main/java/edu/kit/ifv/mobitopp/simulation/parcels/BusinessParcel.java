package edu.kit.ifv.mobitopp.simulation.parcels;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class BusinessParcel extends BaseParcel {

	public BusinessParcel(ZoneAndLocation location, Time plannedArrival, DistributionCenter distributionCenter,
			String deliveryService, DeliveryResults results) {
		super(location, plannedArrival, distributionCenter, deliveryService, results);
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
