package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper=true)
public class BusinessParcel extends BaseParcel {
	
	@Getter
	private final Business business;
	@Getter private final ParcelAgent consumer;

	public BusinessParcel(
			int bundleId,
			ZoneAndLocation location,
			Business business,
			ParcelAgent consumer,
			Time plannedArrival,
			ParcelAgent producer,
			ParcelSize parcelSize,
			double volume,
			boolean isPickUp,
			DeliveryResults results
	) {
		super(bundleId, location, plannedArrival, producer, results, parcelSize, volume, isPickUp);
		this.business = business;
		this.consumer = consumer;
		this.results.logBusinessOrder(this);
		this.results.logChange(this, null, Time.start, false);
	}

	@Override
	protected void logChange(Time currentTime, DeliveryVehicle deliveryVehicle, boolean isAttempt) {
		this.results.logChange(this, deliveryVehicle, currentTime, isAttempt);
	}

	@Override
	protected Optional<RecipientType> canDeliver(Time currentTime, DeliveryVehicle deliveryVehicle) {
		return deliveryVehicle.getOwner().getPolicyProvider().forBusiness().canDeliver(this, currentTime);
	}

	@Override
	protected boolean updateParcelDelivery(Time currentTime, DeliveryVehicle deliveryVehicle) {
		return deliveryVehicle.getOwner().getPolicyProvider().forBusiness().updateParcelDelivery(this, currentTime);
	}

	
//	@Override
//	public boolean couldBeDeliveredWith(IParcel other) {
//		boolean sameLocation = super.couldBeDeliveredWith(other);
//		
//		if (!sameLocation) {
//			return false;
//		}
//		
//		if (other instanceof BusinessParcel) {
//			return true;
//			
//		} else if (other instanceof PrivateParcel) {
//			PrivateParcel that = (PrivateParcel) other;
//			
//			if (that.getDestinationType().equals(WORK)) {
//				return true;
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
	public void setConsumer(ParcelAgent producer) {
		throw new UnsupportedOperationException("Setting the consumer is not supported for BusinessParcels.");
	}
}
