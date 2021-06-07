package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * A factory for creating {@link DeliveryActivity DeliveryActivities}.
 */
public class DeliveryActivityFactory {

	/**
	 * The activity counter. Counts negative for delivery related sub-activities.
	 */
	static int activityCounter = -1;// ?


	/**
	 * Creates a new DeliveryActivity for the given parcel with the given trip and
	 * delivery duration. The activities flexibilities are determined by the given
	 * work activity.
	 *
	 * @param parcels         the parcels to be delivered
	 * @param work            the work
	 * @param startDate       the start date
	 * @param duration        the duration
	 * @param person 		  the delivery person
	 * @return the delivery activity
	 */
	public static DeliveryActivity createDeliveryActivity(Collection<IParcel> parcels, ActivityIfc work, Time startDate, int duration, DeliveryPerson person) {
		
		return new DeliveryActivity(activityCounter--, work.getActivityNrOfWeek(), startDate, duration, person.getEfficiency().getTripDuration(),
				work.startFlexibility(), work.endFlexibility(), work.durationFlexibility(), parcels, person);

	}

	/**
	 * Creates a new unload Activity object based on the given work activity and the
	 * given efficiency profile. The efficiency profile determines the trip and
	 * unload duration.
	 *
	 * @param work       the work activity
	 * @param efficiency the efficiency profile
	 * @return the unload activity
	 */
	public static ActivityIfc createUnloadParcelsActivity(ActivityIfc work, DeliveryEfficiencyProfile efficiency) {
		return createUnloadParcelsActivity(work, efficiency.getTripDuration(), efficiency.getUnloadDuration());
	}

	/**
	 * Creates a new DeliveryActivity object. The activities flexibilities are
	 * determined by the given work activity.
	 * 
	 * @param work           the work
	 * @param tripDuration   the trip duration
	 * @param unloadDuration the unload duration
	 * @return the unload activity
	 */
	public static ActivityIfc createUnloadParcelsActivity(ActivityIfc work, int tripDuration, int unloadDuration) {
		Time startUnloadTime = work.startDate().plusMinutes(work.duration() - unloadDuration);

		ActivityIfc activity = new ActivityAsLinkedListElement(activityCounter--, work.getActivityNrOfWeek(),
				ActivityType.WORK, startUnloadTime, tripDuration, unloadDuration, work.startFlexibility(),
				work.endFlexibility(), work.durationFlexibility());

		return activity;
	}

	/**
	 * Creates a new pick up parcel Activity with the given trip and pick up
	 * duration.
	 *
	 * @param current        the current activity
	 * @param tripDuration   the trip duration
	 * @param pickUpDuration the pick up duration
	 * @return the pick up parcel activity
	 */
	public static ActivityIfc createPickUpParcelsActivity(ActivityIfc current, int tripDuration, int pickUpDuration) {
		ActivityIfc activity = new ActivityAsLinkedListElement(activityCounter--, current.getActivityNrOfWeek(),
				ActivityType.PICK_UP_PARCEL, current.startDate().plusMinutes(current.duration()), tripDuration,
				pickUpDuration, current.startFlexibility(), current.endFlexibility(), current.durationFlexibility());

		return activity;
	}
	
}
