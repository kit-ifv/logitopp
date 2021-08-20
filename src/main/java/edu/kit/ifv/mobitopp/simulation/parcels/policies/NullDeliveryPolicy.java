package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType.PACKSTATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.policies.RecipientType.PERSONAL;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.time.Time;



/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class NullDeliveryPolicy implements ParcelDeliveryPolicy<PrivateParcel> {
	
	public NullDeliveryPolicy() {
	}
	
	/**
	 * Delivery is always successful:
	 * Returns {@link RecipientType#PACKSTATION} as recipient for {@link ParcelDestinationType#PACK_STATION} deliveries
	 * Returns {@link RecipientType#PERSONAL} otherwise
	 *
	 * @param parcel the parcel
	 * @return an optional {@link RecipientType} (non empty)
	 */
	@Override
	public Optional<RecipientType> canDeliver(PrivateParcel parcel, Time currentTime) {

		if (parcel.getDestinationType().equals(PACK_STATION)) {
			return Optional.of(PACKSTATION);
		}
		
		return Optional.of(PERSONAL);
	}
	
	/**
	 * No parcel updates
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel order was updated
	 */
	@Override
	public boolean updateParcelDelivery(PrivateParcel parcel, Time currentTime) {
		return false;
	}
	
}
