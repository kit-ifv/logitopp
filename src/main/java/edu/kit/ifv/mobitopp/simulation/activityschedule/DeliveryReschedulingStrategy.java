package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.HOME;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;
import static java.lang.Math.max;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ModifiableActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DeliveryReschedulingStrategy is a ReschedulingStrategy for persons carrying out deliveries.
 * It generates the delivery person's delivery activities once he arrives at work.
 * Furthermore, the activity schedule is simplified to a work-home-work-home-... schedule and recalculated every evening.
 * The rest of the time a default rescheduling strategy is used.
 * 
 * This strategy is not state-less since it depends on the delivery person and their unload-activities.
 */
public class DeliveryReschedulingStrategy implements ReschedulingStrategy {
	
	private static int replacedActivityId = -1;
	private DistributionCenter center;
	private DeliveryPerson person;
	private ReschedulingStrategy defaultRescheduling;
	private ActivityIfc nextUnload = null;
	private DeliveryResults results;

	private boolean firstRescheduling = true;

	/**
	 * Instantiates a new delivery rescheduling strategy for the given person and distribution center.
	 *
	 * @param center the center
	 * @param person the person
	 * @param defaultRescheduling the default rescheduling strategy
	 * @param results the delivery results
	 */
	public DeliveryReschedulingStrategy(DistributionCenter center, DeliveryPerson person, ReschedulingStrategy defaultRescheduling, DeliveryResults results) {
		this.person = person;
		this.defaultRescheduling = defaultRescheduling;
		this.results = results;
		this.center = center;
	}

	/**
	 * Adjusts the delivery person's schedule.
	 * At the first work aktivity fo the day: split the work activity into loading, small delivery activities and unloading.
	 * For every delivery activity: check if the person is running late. Abort delivery after 19:30 pm.
	 * When the next work activity after loading is reached: unload the parcels that are returning.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @param plannedStartTime the planned start time
	 * @param currentTime the current time
	 */
	@Override
	public void adjustSchedule(ModifiableActivitySchedule activitySchedule, ActivityIfc beginningActivity,
			Time plannedStartTime, Time currentTime) {

		assert beginningActivity != null;
		assert currentTime != null;
		
		
		
		if (firstRescheduling) {
			this.replaceSchedule(activitySchedule, beginningActivity, plannedStartTime, currentTime);

			this.firstRescheduling = false;
			
		}
		
		String before = activityTypeSequence(activitySchedule, beginningActivity);
		String reason = "Other";
		
		if (isFirstWorkActivityOfDay(activitySchedule, beginningActivity)) {
			
			createTourAndLoad(activitySchedule, beginningActivity, currentTime);
			
			reason = "Start Working (Load " 
				   + person.getCurrentTour().size() 
				   + ": " 
				   + activitySchedule.getSucceedingActivitiesUntilDate(beginningActivity, Time.future)
				   					 .stream()
				   					 .filter(a -> a.activityType().equals(ActivityType.DELIVER_PARCEL)).count() 
				   + ")";
			
		} else if (isDelivery(beginningActivity)) {
			
			//Check whether the delivery trips should be interrupted, if the end of the workinghours is almost reached
			if (currentTime.isAfter(currentTime.startOfDay().plusHours(19).plusMinutes(30))) {	
				
				skipRestOfTour(activitySchedule, beginningActivity, currentTime);
				
				reason = "Skip Rest of Tour (" + person.getCurrentTour().size() + ")";
			}
			
		
		} else {
			if (beginningActivity.activityType().equals(ActivityType.WORK)) {

				unload(currentTime);
				reason = "Stop Work (Unload" + person.getCurrentTour().size() + "])";
				replaceSchedule(activitySchedule, beginningActivity, plannedStartTime, currentTime);
				
			} else {
				
				defaultRescheduling.adjustSchedule(activitySchedule, beginningActivity, plannedStartTime, currentTime);
			}
			
			
		}
		
		String after = activityTypeSequence(activitySchedule, beginningActivity);
		this.results.logDeliveryReschedulingEvent(person, currentTime, reason, before, after);

	}

	/**
	 * Unload the returning parcels at the distribution center.
	 *
	 * @param currentTime the current time
	 */
	private void unload(Time currentTime) {

		System.out.println("Person " + person.getOid() + " is unloading truck: (" + this.person.getCurrentTour().size()  + ")");

		
		this.center.unloadParcels(this.person, currentTime);
		this.nextUnload = null;
	}

	/**
	 * Creates the tour and loads the parcels.
	 * Wait at the distribution center until 8:00 am before starting the deliveries.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @param currentTime the current time
	 */
	private void createTourAndLoad(ModifiableActivitySchedule activitySchedule,
		ActivityIfc beginningActivity, Time currentTime) {
		
		Time _8am = beginningActivity.startDate().startOfDay().plusHours(8);
		Time _1800pm = beginningActivity.startDate().startOfDay().plusHours(18);
		
		RelativeTime remainingTime = _1800pm.differenceTo(currentTime);
		
		List<DeliveryActivityBuilder> deliveries = center.assignParcels(person, beginningActivity, currentTime, remainingTime);
				
		beginningActivity.changeDuration(getLoadDuration());
		ActivityIfc currentActivity = beginningActivity;
		
		int parcelCnt = deliveries.stream().mapToInt(l -> l.getParcels().size()).sum();
		System.out.println(person.getOid() + " is assigned " + parcelCnt + " parcels (" + deliveries.size() + " deliveries)");
		
		
		
		if (currentActivity.startDate().isBefore(_8am)) {
				int minDuration = Math.abs(_8am.differenceTo(currentActivity.startDate()).toMinutes());
				int newDuration = Math.max(minDuration, currentActivity.duration());
				
				currentActivity.changeDuration(newDuration);
		};
			
		for (DeliveryActivityBuilder d : deliveries) {
			
			Time startDate = currentActivity.startDate().plusMinutes(currentActivity.duration());
			startDate = startDate.plusMinutes(getTripDuration());
			
			ActivityIfc delivery = d.deliveredBy(person)
									.during(beginningActivity)
									.plannedAt(startDate)
									.build();
			
			activitySchedule.insertActivityAfter(currentActivity, delivery);
			currentActivity = delivery;
		}
		
		
		
		ActivityIfc unload = DeliveryActivityFactory.createUnloadParcelsActivity(beginningActivity, getEfficiency());
		unload.setLocation(this.center.getZoneAndLocation());
		activitySchedule.insertActivityAfter(currentActivity, unload);
		this.nextUnload = unload;
		activitySchedule.fixStartTimeOfActivities();
		
		if (nextUnload.calculatePlannedEndDate().isBefore(_1800pm)) {
			int restOfWork = _1800pm.differenceTo(nextUnload.calculatePlannedEndDate()).toMinutes();
			
			this.nextUnload.changeDuration(nextUnload.duration() + restOfWork);
		}

		System.out.println("Person " + person.getOid() + " loads truck with " + deliveries.stream().mapToInt(l -> l.getParcels().size()).sum() + ": planned unload at " + nextUnload.startDate().toString());
	}

	/**
	 * Returns a String representation of the activity type sequence.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @return the string
	 */
	private String activityTypeSequence(ModifiableActivitySchedule activitySchedule,
		ActivityIfc beginningActivity) {
		return beginningActivity.activityType().getTypeAsInt() + "," +
			   activitySchedule.getSucceedingActivitiesUntilDate(beginningActivity, Time.future)
							   .stream()
							   .map(a -> "" + a.activityType().getTypeAsInt())
							   .collect(Collectors.joining(","));
	}
	
	/**
	 * Returns a String representation of the activity sequence.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @return the string
	 */
	private String activitySequence(ModifiableActivitySchedule activitySchedule,
		ActivityIfc beginningActivity) {
		
		
		return activityTimeString(beginningActivity) + "," + activitySchedule.getSucceedingActivitiesUntilDate(beginningActivity, Time.future)
							   .stream()
							   .map(a -> activityTimeString(a))
							   .collect(Collectors.joining(","));
	}

	/**
	 * Returns a String representation of the given activity with the following format:
	 * '([start day] [start hour]:[start minute] - [activity type] - [end day] [end hour]:[end minute])'
	 *
	 * @param a the a
	 * @return the string
	 */
	private String activityTimeString(ActivityIfc a) {
		return "(" + a.startDate().getDay() + " " + a.startDate().getHour() + ":" + a.startDate().getMinute() +
			         " - " +   a.activityType().name() + " - " + 
			         a.calculatePlannedEndDate().getDay() + " " + a.calculatePlannedEndDate().getHour() + ":" + a.calculatePlannedEndDate().getMinute() + ")";
	}

	/**
	 * Skip the rest of the current tour, 
	 * abort the remaining delivery activities and remove them from the schedule.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @param currentTime the current time
	 */
	private void skipRestOfTour(ModifiableActivitySchedule activitySchedule,
		ActivityIfc beginningActivity, Time currentTime) {
		
		System.out.println("Skip rest of tour for " + person.getOid() + ":");

		List<ActivityIfc> nextActivities = activitySchedule.getSucceedingActivitiesUntilDate(beginningActivity, Time.future);
		
		for (ActivityIfc activity: nextActivities) {
			if (isDelivery(activity)) {
				
				((DeliveryActivity) activity).abortDelivery(person, currentTime);
				activitySchedule.removeActivity(activity);
				
			}
		}
		
		activitySchedule.fixStartTimeOfActivities();
	}
	
	
	/**
	 * Replace the current schedule by work-home-work-home...
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @param plannedStartTime the planned start time
	 * @param currentTime the current time
	 */
	private void replaceSchedule(ModifiableActivitySchedule activitySchedule, ActivityIfc beginningActivity,
		Time plannedStartTime, Time currentTime) {
		String before = activitySequence(activitySchedule, beginningActivity);
		
		Time time = beginningActivity.startDate();
		Time _730am = time.startOfDay().plusHours(7).plusMinutes(30);
		Time _1800pm = time.startOfDay().plusHours(18);
		Time endOfWork = Time.start.startOfDay().plusDays(6);
		Time endOfWeek = Time.start.plusDays(7).plusHours(1);
		
		//Remove all activities
		Collection<ActivityIfc> rest = activitySchedule.getSucceedingActivitiesUntilDate(beginningActivity, Time.future);
		rest.forEach(activitySchedule::removeActivity);
		
		List<Integer> ids = rest.stream().map(ActivityIfc::getOid).collect(toList());
		
		
		ActivityType type = beginningActivity.activityType();
		
		//Adjust duration of beginning activity
		Time adjustTimeTo = null;
		if (type == ActivityType.WORK) {
			adjustTimeTo = _1800pm;
		} else {
			adjustTimeTo = _730am;
		}
		
		int remainingDuration = max(1, adjustTimeTo.differenceTo(time).toMinutes());
		beginningActivity.changeDuration(remainingDuration);
		
		
		ActivityIfc lastActivity = null;
	
		if (type == ActivityType.WORK) {
			lastActivity = insertHomeActivity(activitySchedule, beginningActivity, ids);
		} else {
			lastActivity = beginningActivity;
		}
		
		//For each day until the end of the week add work and home activity
		while (lastActivity.calculatePlannedEndDate().isBefore(endOfWork)) {
			lastActivity = insertWorkActivity(activitySchedule, lastActivity, ids);
			lastActivity = insertHomeActivity(activitySchedule, lastActivity, ids);
		}

		int restOfWeek = max(1,endOfWeek.differenceTo(lastActivity.calculatePlannedEndDate()).toMinutes());
		lastActivity.changeDuration(lastActivity.duration() + restOfWeek);


		String after = activitySequence(activitySchedule, beginningActivity);
		this.results.logDeliveryReschedulingEvent(person, currentTime, "Replace Schedule", before, after);
		
	}
	
	/**
	 * Insert a new home activity after the given last activity.
	 *
	 * @param activitySchedule the activity schedule
	 * @param lastActivity the last activity
	 * @param ids the ids
	 * @return the new home activity
	 */
	private ActivityIfc insertHomeActivity(ModifiableActivitySchedule activitySchedule, ActivityIfc lastActivity, List<Integer> ids) {
		
		Time time = lastActivity.calculatePlannedEndDate().plusMinutes(15);
		Time currentDay = time.startOfDay();
		Time _730am = currentDay.plusHours(7).plusMinutes(30);

		
		if (time.isAfter(_730am)) {
			_730am = _730am.plusDays(1);
		}
		
		int homeDuration = max(1, _730am.differenceTo(time).toMinutes());
		ActivityIfc home = new ActivityAsLinkedListElement(newId(ids), lastActivity.getActivityNrOfWeek()+1, HOME, time, homeDuration, 15);
		
		activitySchedule.insertActivityAfter(lastActivity, home);
		
		return home;
	}
	
	/**
	 * Insert a work activity after the given last activity.
	 *
	 * @param activitySchedule the activity schedule
	 * @param lastActivity the last activity
	 * @param ids the ids
	 * @return the new work activity
	 */
	private ActivityIfc insertWorkActivity(ModifiableActivitySchedule activitySchedule, ActivityIfc lastActivity, List<Integer> ids) {
		
		Time time = lastActivity.calculatePlannedEndDate().plusMinutes(15);
		Time currentDay = time.startOfDay();
		Time _1800pm = currentDay.plusHours(18);

		
		if (time.isAfter(_1800pm)) {
			_1800pm = _1800pm.plusDays(1);
		}
		
		int workDuration = max(1, _1800pm.differenceTo(time).toMinutes());
		ActivityIfc work = new ActivityAsLinkedListElement(newId(ids), lastActivity.getActivityNrOfWeek()+1, WORK, time, workDuration, 15);
		
		activitySchedule.insertActivityAfter(lastActivity, work);
		
		return work;
	}
	
	/**
	 * Returns a new activity id.
	 * If the given list of ids is not empty, the will be recycled.
	 *
	 * @param ids the ids
	 * @return the int
	 */
	private int newId(List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return replacedActivityId--;
		} else {
			return ids.remove(0);
		}
	}



	/**
	 * Checks if the given activity is the first work activity of day.
	 *
	 * @param activitySchedule the activity schedule
	 * @param beginningActivity the beginning activity
	 * @return true, if it is the first work activity of day
	 */
	private boolean isFirstWorkActivityOfDay(ModifiableActivitySchedule activitySchedule, ActivityIfc beginningActivity) {
		return 	beginningActivity.activityType().isWorkActivity() &&
			    !(beginningActivity == this.nextUnload);
	}
	
	/**
	 * Checks if the given activity is a delivery.
	 *
	 * @param activity the activity
	 * @return true, if it is a delivery
	 */
	private boolean isDelivery(ActivityIfc activity) {
		return activity.activityType().equals(ActivityType.DELIVER_PARCEL);
	}

	/**
	 * Gets the efficiency profile.
	 *
	 * @return the efficiency profile
	 */
	public DeliveryEfficiencyProfile getEfficiency() {
		return this.person.getEfficiency();
	}

	/**
	 * Gets the trip duration.
	 *
	 * @return the trip duration
	 */
	private int getTripDuration() {
		return getEfficiency().getTripDuration();
	}

	/**
	 * Gets the load duration.
	 *
	 * @return the load duration
	 */
	private int getLoadDuration() {
		return getEfficiency().getLoadDuration();
	}
}
