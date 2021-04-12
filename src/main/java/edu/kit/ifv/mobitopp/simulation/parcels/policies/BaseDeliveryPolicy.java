package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;


/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class BaseDeliveryPolicy implements ParcelDeliveryPolicy {
	
	public BaseDeliveryPolicy() {
		// TODO Auto-generated constructor stub
	}
	
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

		if (parcel.getDestinationType().equals(HOME)) {

			return isHome(parcel.getPerson());						
								
			
		} else if (parcel.getDestinationType().equals(WORK)) {
			
			if (isWorking(parcel.getPerson())) {
				return true;
				
			} else {
				return false;
			}
			
			
		} else if (parcel.getDestinationType().equals(PACK_STATION)) {
			
			return true;
		}
		
		return false;
	}


	private boolean isHome(Person person) {
		return hasActivity(person, ActivityType.HOME);
	}
	
	private boolean isWorking(Person person) {
		return hasActivity(person, ActivityType.WORK);
	}
	
	private boolean hasActivity(Person person, ActivityType activity) {
		return person.currentActivity().activityType().equals(activity);
	}

	
	/**
	 * No parcel updates
	 *
	 * @param parcel the parcel
	 * @return true, if the parcel order was updated
	 */
	@Override
	public boolean updateParcelDelivery(Parcel parcel) {
		return false;
	}

}
