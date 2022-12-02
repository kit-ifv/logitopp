package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;

public class OpeningHoursDeliveryPolicy implements ParcelDeliveryPolicy<BusinessParcel> {

	private final ParcelDeliveryPolicy<BusinessParcel> delegate;
	
	public OpeningHoursDeliveryPolicy(ParcelDeliveryPolicy<BusinessParcel> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Optional<RecipientType> canDeliver(BusinessParcel parcel, Time currentTime) {
		
		Pair<Time, Time> interval = parcel.getBusiness().getOpeningHours().get(currentTime.weekDay());
		
		if (interval.getFirst().isBeforeOrEqualTo(currentTime) && currentTime.isBeforeOrEqualTo(interval.getSecond())) {
			return delegate.canDeliver(parcel, currentTime);
		}

		return Optional.empty();
	}

	@Override
	public boolean updateParcelDelivery(BusinessParcel parcel, Time currentTime) {
		return delegate.updateParcelDelivery(parcel, currentTime);
	}


}
