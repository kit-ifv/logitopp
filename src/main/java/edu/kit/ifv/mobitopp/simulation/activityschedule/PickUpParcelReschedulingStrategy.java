package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ModifiableActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class PickUpParcelReschedulingStrategy is a {@link ReschedulingStrategy} for {@link PickUpParcelPerson}s.
 * It inserts a PickUpParcel {@link ActivityIfc} if the person has parcels waiting at a pack station.
 * It decorates a {@link ReschedulingStrategy} which will be applied after inserting parcel pick-ups.
 */
public class PickUpParcelReschedulingStrategy implements ReschedulingStrategy  {

	/**
	 * The activity counter. Counts negative for delivery related sub-activities.
	 */
	static int activityCounter = -1;
	
	private PickUpParcelPerson person;
	private ReschedulingStrategy defaultRescheduling;
	private boolean isPickupPlanned = false;
	
	
	/**
	 * Instantiates a new pick up parcel rescheduling strategy.
	 *
	 * @param person the person
	 * @param defaultRescheduling the default rescheduling
	 */
	public PickUpParcelReschedulingStrategy(PickUpParcelPerson person, ReschedulingStrategy defaultRescheduling) {
		this.person = person;
		this.defaultRescheduling = defaultRescheduling;
	}
	
	/**
	 * Adjust the schedule. Insert PickUpParcel {@link ActivityIfc activities} if the person has parcels waiting at the pack station.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @param plannedStartTime the planned start time
	 * @param currentTime the current time
	 */
	@Override
	public void adjustSchedule(ModifiableActivitySchedule activitySchedule, ActivityIfc beginningActivity,
			Time plannedStartTime, Time currentTime) {
		
		if (!person.hasParcelInPackstation()) {
			this.defaultRescheduling.adjustSchedule(activitySchedule, beginningActivity, plannedStartTime, currentTime);
			return;
		}
		
		if (isPickupPlanned && beginningActivity.activityType().equals(ActivityType.PICK_UP_PARCEL)) {
			
			this.person.pickUpParcels();
			
			this.isPickupPlanned = false;
			
		} else if (!isPickupPlanned) {
			
			ActivityIfc nextHome = null;
			if (activitySchedule.hasNextActivity(beginningActivity)) {
				nextHome = activitySchedule.nextHomeActivity(beginningActivity);
			}
			
			if (nextHome != null && activitySchedule.hasPrevActivity(nextHome)) {
				
				ActivityIfc prev = activitySchedule.prevActivity(nextHome);
				ActivityIfc pickUpActivity = createPickUpParcelsActivity(prev, 15, 3);//TODO trip/pick up duration
				activitySchedule.insertActivityAfter(prev, pickUpActivity);
				
				this.isPickupPlanned = true;
				
			} else {
				System.out.println("Could not plan pickup for person " + this.person.getOid());
			}

		}
				
		this.defaultRescheduling.adjustSchedule(activitySchedule, beginningActivity, plannedStartTime, currentTime);
		
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
