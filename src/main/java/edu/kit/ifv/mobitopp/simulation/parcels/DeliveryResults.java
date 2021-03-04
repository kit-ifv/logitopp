package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class DeliveryResults {

	private final static Category resultCategoryState = createResultCategoryState();
	private final static Category resultCategoryOrder = createResultCategoryOrder();
	private final static Category resultCategoryEmployee = createResultCategoryEmployee();
	private final static Category resultCategoryRescheduling = createResultCategoryRescheduling();
	private Results results;
	
	public DeliveryResults(Results results) {
		this.results = results;
	}

	public void logChange(Parcel parcel, DeliveryPerson deliveryGuy, Time currentTime, boolean isAttempt) {
		String msg = "";
		msg += currentTime.toString() + "; ";
		msg += parcel.getOId() + "; ";
		msg += parcel.getPerson().getOid() + "; ";
		msg += parcel.getDestinationType().name() + "; ";
		msg += parcel.getState().name() + "; ";
		msg += isAttempt + "; ";
		msg += ((deliveryGuy != null) ? deliveryGuy.getOid() : "NULL") + "; ";
		msg += parcel.getDistributionCenter().getName() + "; ";
		msg += parcel.getDeliveryAttempts() + "; ";
		msg += String.valueOf(parcel.getDeliveryTime()) + ";";
		msg += deliveryGuy.currentActivity().zone().getId() + ";";
		msg += parcel.getZone().getId() + ";";
		
		this.results.write(resultCategoryState, msg);
	}
	
	public static Category createResultCategoryState() {
		return new Category("parcel-states", Arrays.asList("Time", "ParcelID", "RecipientID", "DestinationType", "State", "IsDeliveryAttempt", "DeliveryGuyID", "DistributionCenter", "DeliveryAttempts", "DeliveryTime", "CurrentDeliveryGuyZone", "ParcelDestinationZone"));
	}
	
	
	
	public void logOrder(Parcel parcel) {
		String msg = "";
		
		msg += parcel.getOId() + "; ";
		msg += parcel.getPerson().getOid() + "; ";
		msg += parcel.getDestinationType().name() + "; ";
		msg += parcel.getPlannedArrivalDate().getDay() + "; ";
		msg += parcel.getDistributionCenter().getName() + ";";
		
		this.results.write(resultCategoryOrder, msg);
	}
	
	public static Category createResultCategoryOrder() {
		return new Category("parcel-orders", Arrays.asList("ParcelID", "RecipientID", "DestinationType", "ArrivalDay", "DistributionCenter"));
	}
	
	
	public void logEmployee(DeliveryPerson person, DistributionCenter distributionCenter) {
		String msg = "";
		
		msg += person.getId().getOid() + "; ";
		msg += distributionCenter.getName() + "; ";
		msg += distributionCenter.getOrganization() + "; ";
		msg += person.employment().name() + ";";
		
		this.results.write(resultCategoryEmployee, msg);
	}
	
	public static Category createResultCategoryEmployee() {
		return new Category("delivery-employees", Arrays.asList("PeronID", "DistributionCenter", "Organization", "EmploymentType"));
	}
	
	
	public void logDeliveryReschedulingEvent(DeliveryPerson person, Time currentTime, String reason, String before, String after) {
		String msg = "";
		
		msg += person.getId().getOid() + "; ";
		msg += currentTime.toString() + "; ";
		msg += currentTime.weekDay().name() + "; ";
		msg += reason + ";";
		msg += before + ";";
		msg += after;
		
		this.results.write(resultCategoryRescheduling, msg);
	}
	
	public static Category createResultCategoryRescheduling() {
		return new Category("delivery-rescheduling", Arrays.asList("PeronID", "Time", "Day", "Reason", "Before", "After"));
	}
	
}
