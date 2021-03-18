package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.simulation.ActivityType;


/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class DummyDeliveryPolicy implements ParcelDeliveryPolicy {

	/**
	 * Checks whether the given parcel can be delivered.
	 * Home delivery: check if the recipient (or another household member) is at home.
	 * Work delivery: check if the recipient is working.
	 * Pack-station delivery: true
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel can be delivered
	 */
	@Override
	public boolean canDeliver(Parcel parcel) {

		if (parcel.getDestinationType() == ParcelDestinationType.HOME) {
			

			boolean isHome = parcel.getPerson()
								   .household()
								   .persons()
								   .anyMatch(p -> p.currentActivity().activityType().equals(ActivityType.HOME));

			return isHome;						
								
		} else if (parcel.getDestinationType() == ParcelDestinationType.WORK) {
			
			if (parcel.getPerson().currentActivity().activityType() == ActivityType.WORK) {
				return true;
				
			} else {
				return false;
			}
			
			
		} else if (parcel.getDestinationType() == ParcelDestinationType.PACK_STATION) {
			
			return true;
		}
		
		return false;
	}

	
	/**
	 * Update the parcel delivery.
	 * After three delivery attempts: send the parcel to a pack station.
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel order was updated
	 */
	@Override
	public boolean updateParcelDelivery(Parcel parcel) {
		if (parcel.getDeliveryAttempts() >= 2) {
			parcel.setDestinationType(ParcelDestinationType.PACK_STATION);
			return true;
		}
		
		return false;
	}

}
