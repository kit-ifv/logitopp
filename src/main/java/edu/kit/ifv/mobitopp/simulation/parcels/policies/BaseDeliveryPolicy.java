package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.time.Time;



/**
 * The Class DummyDeliveryPolicy is an exemplary implementation of the ParcelDeliveryPolicy interface.
 */
public class BaseDeliveryPolicy implements ParcelDeliveryPolicy<PrivateParcel> {
	
	public BaseDeliveryPolicy() {
	}
	
	/**
	 * Checks whether the given parcel can be delivered.
	 * Home delivery: check if the recipient (or another household member) is at home.
	 * Work delivery: check if the recipient is working.
	 * Pack-station delivery: true
	 *
	 * @param parcel the parcel
	 * @return an optional {@link RecipientType} if the parcel can be delivered, an empty {@link Optional} otherwise
	 */
	@Override
	public Optional<RecipientType> canDeliver(PrivateParcel parcel, Time currentTime) {


		if (parcel.getDestinationType().equals(HOME)) {

			return optionalRecipient(isHome(parcel.getPerson()), RecipientType.PERSONAL);						
								
			
		} else if (parcel.getDestinationType().equals(WORK)) {
			
			return optionalRecipient(isWorking(parcel.getPerson()), RecipientType.PERSONAL);

			
			
		} else if (parcel.getDestinationType().equals(PACK_STATION)) {
			
			return optionalRecipient(true, RecipientType.PACKSTATION);
		}
		
		return Optional.empty();
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
	public boolean updateParcelDelivery(PrivateParcel parcel, Time currentTime) {
		return false;
	}
	
	private Optional<RecipientType> optionalRecipient(boolean check, RecipientType recipientType) {
		if (check) {
			return Optional.of(recipientType);
		} else {
			return Optional.empty();
		}
	}

}
