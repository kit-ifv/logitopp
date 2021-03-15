package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DeliveryActivity objects.
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
	 * @param parcels the parcels
	 * @param work            the work
	 * @param startDate       the start date
	 * @param person the person
	 * @return the delivery activity
	 */
	public static ActivityIfc createDeliveryActivity(List<Parcel> parcels, ActivityIfc work, Time startDate, DeliveryPerson person) {
		
		ActivityIfc activity = new DeliveryActivity(activityCounter--, work.getActivityNrOfWeek(), startDate,
				work.startFlexibility(), work.endFlexibility(), work.durationFlexibility(), parcels, person);

		return activity;
	}

	/**
	 * Creates a new unload Activity object based on the given work activity and the
	 * given efficiency profile. The efficiency profile determines the trip and
	 * unload duration.
	 *
	 * @param work       the work
	 * @param efficiency the efficiency
	 * @return the activity ifc
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
	 * @return the activity ifc
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
	
	/**
	 * Creates a new DeliveryActivity object.
	 *
	 * @param next the next
	 * @param tripDuration the trip duration
	 * @return the activity ifc
	 */
	public static ActivityIfc createSaturdayWorkActivity(ActivityIfc next, int tripDuration) {
		Time startTime = next.startDate();

		ActivityIfc activity = new ActivityAsLinkedListElement(activityCounter--, next.getActivityNrOfWeek(),
				ActivityType.WORK, startTime, tripDuration, 420, next.startFlexibility(),
				next.endFlexibility(), next.durationFlexibility());

		return activity;
	}
}
