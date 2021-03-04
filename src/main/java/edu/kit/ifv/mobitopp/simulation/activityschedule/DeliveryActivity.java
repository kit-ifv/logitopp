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

/**
 * The Class DeliveryActivity represents the activity of delivering a parcel.
 */
@Getter
public class DeliveryActivity implements ActivityIfc, LinkedListElement {

	/** The wrapped/decorated activity. */
	private ActivityAsLinkedListElement activity;

	/** The parcel to be delivered. */
	private final Collection<Parcel> parcels;
	private final DeliveryPerson person;

	/**
	 * Instantiates a new delivery activity for the given parcel based on the given
	 * activity.
	 *
	 * @param activity the activity
	 * @param parcel   the parcel
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
	 * @param parcel               the parcel
	 */
	public DeliveryActivity(int oid, byte activityNrOfWeek, Time startDate, int duration,
		int observedTripDuration, float startFlexibility, float endFlexibility,
		float durationFlexibility, List<Parcel> parcels, DeliveryPerson person) {

		this(new ActivityAsLinkedListElement(oid, activityNrOfWeek, ActivityType.DELIVER_PARCEL,
			startDate, duration, observedTripDuration, startFlexibility, endFlexibility,
			durationFlexibility), parcels, person);
	}

	public DeliveryActivity(int oid, byte activityNrOfWeek, Time startDate, float startFlexibility,
		float endFlexibility, float durationFlexibility, List<Parcel> parcels,
		DeliveryPerson person) {

		this(new ActivityAsLinkedListElement(oid, activityNrOfWeek, ActivityType.DELIVER_PARCEL,
			startDate, duration(parcels, person.getEfficiency()), person.getEfficiency().getTripDuration(),
			startFlexibility, endFlexibility, durationFlexibility), parcels, person);

	}

	private static int duration(List<Parcel> parcels, DeliveryEfficiencyProfile efficiency) {
		return max(1, round(
			efficiency.getDeliveryDurAdd() + efficiency.getDeliveryDurMul() * parcels.size()));
	}

	public void tryDelivery(Time currentTime) {
		parcels.forEach(p -> this.tryDelivery(person, p, currentTime));
	}

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

	public void abortDelivery(DeliveryPerson person, Time currentTime) {
		parcels.forEach(p -> p.updateState(currentTime, person, false));
	}

	@Override
	public boolean isRunning() {
		return this.activity.isRunning();
	}

	@Override
	public void setRunning(boolean running_) {
		this.activity.setRunning(running_);
	}

	@Override
	public byte getActivityNrOfWeek() {
		return this.activity.getActivityNrOfWeek();
	}

	@Override
	public ActivityType activityType() {
		return this.activity.activityType();
	}

	@Override
	public boolean isModeSet() {
		return this.activity.isModeSet();
	}

	@Override
	public void setMode(Mode mode) {
		this.activity.setMode(mode);
	}

	@Override
	public Mode mode() {
		return this.activity.mode();
	}

	@Override
	public Time startDate() {
		return this.activity.startDate();
	}

	@Override
	public void setStartDate(Time date_) {
		this.activity.setStartDate(date_);
	}

	@Override
	public Time calculatePlannedEndDate() {
		return this.activity.calculatePlannedEndDate();
	}

	@Override
	public int duration() {
		return this.activity.duration();
	}

	@Override
	public void changeDuration(int newDuration) {
		this.activity.changeDuration(newDuration);
	}

	@Override
	public int observedTripDuration() {
		return this.activity.observedTripDuration();
	}

	@Override
	public int getOid() {
		return this.activity.getOid();
	}

	@Override
	public boolean isLocationSet() {
		return this.activity.isLocationSet();
	}

	@Override
	public void setLocation(ZoneAndLocation zoneAndlocation) {
		this.activity.setLocation(zoneAndlocation);
	}

	@Override
	public Zone zone() {
		return this.activity.zone();
	}

	@Override
	public Location location() {
		return this.activity.location();
	}

	@Override
	public ZoneAndLocation zoneAndLocation() {
		return this.activity.zoneAndLocation();
	}

	@Override
	public boolean hasFixedDuration() {
		return this.activity.hasFixedDuration();
	}

	@Override
	public boolean hasFixedStart() {
		return this.activity.hasFixedStart();
	}

	@Override
	public boolean hasFixedEnd() {
		return this.activity.hasFixedEnd();
	}

	@Override
	public float durationFlexibility() {
		return this.activity.durationFlexibility();
	}

	@Override
	public float startFlexibility() {
		return this.activity.startFlexibility();
	}

	@Override
	public float endFlexibility() {
		return this.activity.endFlexibility();
	}

	@Override
	public LinkedListElement next() {
		return this.activity.next();
	}

	@Override
	public LinkedListElement prev() {
		return this.activity.prev();
	}

	@Override
	public void setNext(LinkedListElement next) {
		this.activity.setNext(next);
	}

	@Override
	public void setPrev(LinkedListElement prev) {
		this.activity.setPrev(prev);
	}

}
