package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ModifiableActivitySchedule;
import edu.kit.ifv.mobitopp.simulation.ReschedulingStrategy;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class PickUpParcelReschedulingStrategy implements ReschedulingStrategy  {

	private PickUpParcelPerson person;
	private ReschedulingStrategy defaultRescheduling;
	private boolean isPickupPlanned = false;
	
	
	public PickUpParcelReschedulingStrategy(PickUpParcelPerson person, ReschedulingStrategy defaultRescheduling) {
		this.person = person;
		this.defaultRescheduling = defaultRescheduling;
	}
	
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
				ActivityIfc pickUpActivity = DeliveryActivityFactory.createPickUpParcelsActivity(prev, 15, 3);//TODO trip/pick up duration
				activitySchedule.insertActivityAfter(prev, pickUpActivity);
				
				this.isPickupPlanned = true;
				
			} else {
				System.out.println("Could not plan pickup for person " + this.person.getOid());
			}

		}
				
		this.defaultRescheduling.adjustSchedule(activitySchedule, beginningActivity, plannedStartTime, currentTime);
		
	}
	
}
