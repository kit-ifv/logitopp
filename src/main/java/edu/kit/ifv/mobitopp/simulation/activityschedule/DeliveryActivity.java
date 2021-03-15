package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static java.lang.Math.max;
import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.LinkedListElement;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * The Class DeliveryActivity represents the activity of delivering a parcel.
 */

@Getter
public class DeliveryActivity implements ActivityIfc, LinkedListElement {

	/** The wrapped/decorated activity. */
	private ActivityAsLinkedListElement activity;

	/** The parcel to be delivered. */
	private final Collection<Parcel> parcels;
	
	/** The person. */
	private final DeliveryPerson person;

	/**
	 * Instantiates a new delivery activity for the given parcel based on the given
	 * activity.
	 *
	 * @param activity the activity
	 * @param parcels the parcels
	 * @param person the person
	 */
	private DeliveryActivity(ActivityAsLinkedListElement activity, List<Parcel> parcels,
		DeliveryPerson person) {
		this.activity = activity;
		this.parcels = new ArrayList<Parcel>(parcels);
		this.person = person;

		if (!parcels.isEmpty()) {
			setLocation(parcels.get(0).getZoneAndLocation());
		}

	}

	/**
	 * Instantiates a new delivery activity for the given parcel.
	 *
	 * @param oid                  the oid
	 * @param activityNrOfWeek     the activity nr of week
	 * @param startDate            the start date
	 * @param duration             the duration
	 * @param observedTripDuration the observed trip duration
	 * @param startFlexibility     the start flexibility
	 * @param endFlexibility       the end flexibility
	 * @param durationFlexibility  the duration flexibility
	 * @param parcels the parcels
	 * @param person the person
	 */
	public DeliveryActivity(int oid, byte activityNrOfWeek, Time startDate, int duration,
		int observedTripDuration, float startFlexibility, float endFlexibility,
		float durationFlexibility, List<Parcel> parcels, DeliveryPerson person) {

		this(new ActivityAsLinkedListElement(oid, activityNrOfWeek, ActivityType.DELIVER_PARCEL,
			startDate, duration, observedTripDuration, startFlexibility, endFlexibility,
			durationFlexibility), parcels, person);
	}

	/**
	 * Instantiates a new delivery activity.
	 *
	 * @param oid the oid
	 * @param activityNrOfWeek the activity nr of week
	 * @param startDate the start date
	 * @param startFlexibility the start flexibility
	 * @param endFlexibility the end flexibility
	 * @param durationFlexibility the duration flexibility
	 * @param parcels the parcels
	 * @param person the person
	 */
	public DeliveryActivity(int oid, byte activityNrOfWeek, Time startDate, float startFlexibility,
		float endFlexibility, float durationFlexibility, List<Parcel> parcels,
		DeliveryPerson person) {

		this(new ActivityAsLinkedListElement(oid, activityNrOfWeek, ActivityType.DELIVER_PARCEL,
			startDate, duration(parcels, person.getEfficiency()), person.getEfficiency().getTripDuration(),
			startFlexibility, endFlexibility, durationFlexibility), parcels, person);

	}

	/**
	 * Duration.
	 *
	 * @param parcels the parcels
	 * @param efficiency the efficiency
	 * @return the int
	 */
	private static int duration(List<Parcel> parcels, DeliveryEfficiencyProfile efficiency) {
		return max(1, round(
			efficiency.getDeliveryDurAdd() + efficiency.getDeliveryDurMul() * parcels.size()));
	}

	/**
	 * Try delivery.
	 *
	 * @param currentTime the current time
	 */
	public void tryDelivery(Time currentTime) {
		parcels.forEach(p -> this.tryDelivery(person, p, currentTime));
	}

	/**
	 * Try delivery.
	 *
	 * @param person the person
	 * @param parcel the parcel
	 * @param currentTime the current time
	 */
	private void tryDelivery(DeliveryPerson person, Parcel parcel, Time currentTime) {
		String msg = "";

		if (parcel.getDistributionCenter().getPolicy().canDeliver(parcel)) {
			parcel.deliver(currentTime, person);
			msg = "Successful delivery of ";

		} else {
			parcel.getDistributionCenter().getPolicy().updateParcelDelivery(parcel);
			msg = "Unable to deliver ";
		}

		parcel.updateState(currentTime, person, true);

		System.out
			.println(msg + "parcel " + parcel.getOId() + "(" + parcel.getState() + ")"
				+ " to person " + parcel.getPerson().getOid() + " at "
				+ parcel.getDestinationType().name() + " by delivery guy " + person.getOid()
				+ ". (attempt " + parcel.getDeliveryAttempts() + ")");
	}

	/**
	 * Abort delivery.
	 *
	 * @param person the person
	 * @param currentTime the current time
	 */
	public void abortDelivery(DeliveryPerson person, Time currentTime) {
		parcels.forEach(p -> p.updateState(currentTime, person, false));
	}

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	@Override
	public boolean isRunning() {
		return this.activity.isRunning();
	}

	/**
	 * Sets the running.
	 *
	 * @param running_ the new running
	 */
	@Override
	public void setRunning(boolean running_) {
		this.activity.setRunning(running_);
	}

	/**
	 * Gets the activity nr of week.
	 *
	 * @return the activity nr of week
	 */
	@Override
	public byte getActivityNrOfWeek() {
		return this.activity.getActivityNrOfWeek();
	}

	/**
	 * Activity type.
	 *
	 * @return the activity type
	 */
	@Override
	public ActivityType activityType() {
		return this.activity.activityType();
	}

	/**
	 * Checks if is mode set.
	 *
	 * @return true, if is mode set
	 */
	@Override
	public boolean isModeSet() {
		return this.activity.isModeSet();
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the new mode
	 */
	@Override
	public void setMode(Mode mode) {
		this.activity.setMode(mode);
	}

	/**
	 * Mode.
	 *
	 * @return the mode
	 */
	@Override
	public Mode mode() {
		return this.activity.mode();
	}

	/**
	 * Start date.
	 *
	 * @return the time
	 */
	@Override
	public Time startDate() {
		return this.activity.startDate();
	}

	/**
	 * Sets the start date.
	 *
	 * @param date_ the new start date
	 */
	@Override
	public void setStartDate(Time date_) {
		this.activity.setStartDate(date_);
	}

	/**
	 * Calculate planned end date.
	 *
	 * @return the time
	 */
	@Override
	public Time calculatePlannedEndDate() {
		return this.activity.calculatePlannedEndDate();
	}

	/**
	 * Duration.
	 *
	 * @return the int
	 */
	@Override
	public int duration() {
		return this.activity.duration();
	}

	/**
	 * Change duration.
	 *
	 * @param newDuration the new duration
	 */
	@Override
	public void changeDuration(int newDuration) {
		this.activity.changeDuration(newDuration);
	}

	/**
	 * Observed trip duration.
	 *
	 * @return the int
	 */
	@Override
	public int observedTripDuration() {
		return this.activity.observedTripDuration();
	}

	/**
	 * Gets the oid.
	 *
	 * @return the oid
	 */
	@Override
	public int getOid() {
		return this.activity.getOid();
	}

	/**
	 * Checks if is location set.
	 *
	 * @return true, if is location set
	 */
	@Override
	public boolean isLocationSet() {
		return this.activity.isLocationSet();
	}

	/**
	 * Sets the location.
	 *
	 * @param zoneAndlocation the new location
	 */
	@Override
	public void setLocation(ZoneAndLocation zoneAndlocation) {
		this.activity.setLocation(zoneAndlocation);
	}

	/**
	 * Zone.
	 *
	 * @return the zone
	 */
	@Override
	public Zone zone() {
		return this.activity.zone();
	}

	/**
	 * Location.
	 *
	 * @return the location
	 */
	@Override
	public Location location() {
		return this.activity.location();
	}

	/**
	 * Zone and location.
	 *
	 * @return the zone and location
	 */
	@Override
	public ZoneAndLocation zoneAndLocation() {
		return this.activity.zoneAndLocation();
	}

	/**
	 * Checks for fixed duration.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasFixedDuration() {
		return this.activity.hasFixedDuration();
	}

	/**
	 * Checks for fixed start.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasFixedStart() {
		return this.activity.hasFixedStart();
	}

	/**
	 * Checks for fixed end.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasFixedEnd() {
		return this.activity.hasFixedEnd();
	}

	/**
	 * Duration flexibility.
	 *
	 * @return the float
	 */
	@Override
	public float durationFlexibility() {
		return this.activity.durationFlexibility();
	}

	/**
	 * Start flexibility.
	 *
	 * @return the float
	 */
	@Override
	public float startFlexibility() {
		return this.activity.startFlexibility();
	}

	/**
	 * End flexibility.
	 *
	 * @return the float
	 */
	@Override
	public float endFlexibility() {
		return this.activity.endFlexibility();
	}

	/**
	 * Next.
	 *
	 * @return the linked list element
	 */
	@Override
	public LinkedListElement next() {
		return this.activity.next();
	}

	/**
	 * Prev.
	 *
	 * @return the linked list element
	 */
	@Override
	public LinkedListElement prev() {
		return this.activity.prev();
	}

	/**
	 * Sets the next.
	 *
	 * @param next the new next
	 */
	@Override
	public void setNext(LinkedListElement next) {
		this.activity.setNext(next);
	}

	/**
	 * Sets the prev.
	 *
	 * @param prev the new prev
	 */
	@Override
	public void setPrev(LinkedListElement prev) {
		this.activity.setPrev(prev);
	}

}
