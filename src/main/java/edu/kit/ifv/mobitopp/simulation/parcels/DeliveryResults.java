package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DeliveryResults provides methods for 
 * logging results concerned with parcels and deliveries.
 */
public class DeliveryResults {

	private final static Category resultCategoryState = createResultCategoryState();
	private final static Category resultCategoryOrder = createResultCategoryOrder();
	private final static Category resultCategoryEmployee = createResultCategoryEmployee();
	private final static Category resultCategoryRescheduling = createResultCategoryRescheduling();

	private Results results;
	
	/**
	 * Instantiates a new delivery results object with the given {@link Results} object.
	 *
	 * @param results the results
	 */
	public DeliveryResults(Results results) {
		this.results = results;
	}

	/**
	 * Log a parcels new state.
	 *
	 * @param parcel the parcel
	 * @param deliveryGuy the delivery guy
	 * @param currentTime the current time
	 * @param isAttempt the is attempt
	 */
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
		msg += ((parcel.getRecipientType() != null) ? parcel.getRecipientType().name() : "NULL") + "; ";
		msg += ((deliveryGuy != null) ? deliveryGuy.currentActivity().zone().getId() : "NULL") + "; ";
		msg += parcel.getZone().getId();
		
		this.results.write(resultCategoryState, msg);
	}
	
	/**
	 * Creates the result category for parcel-states results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryState() {
		return new Category("parcel-states", Arrays.asList("Time", "ParcelID", "RecipientID", "DestinationType", "State", "IsDeliveryAttempt", "DeliveryGuyID", "DistributionCenter", "DeliveryAttempts", "DeliveryTime", "RecipientType", "CurrentDeliveryGuyZone", "ParcelDestinationZone"));
	}
	
	
	
	/**
	 * Logs the order of the given parcel.
	 *
	 * @param parcel the ordered parcel
	 */
	public void logOrder(Parcel parcel) {
		String msg = "";
		
		msg += parcel.getOId() + "; ";
		msg += parcel.getPerson().getOid() + "; ";
		msg += parcel.getDestinationType().name() + "; ";
		msg += parcel.getPlannedArrivalDate().getDay() + "; ";
		msg += parcel.getDistributionCenter().getName() + ";";
		msg += parcel.getDeliveryService();
		
		this.results.write(resultCategoryOrder, msg);
	}
	
	/**
	 * Creates the result category order for parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryOrder() {
		return new Category("parcel-orders", Arrays.asList("ParcelID", "RecipientID", "DestinationType", "ArrivalDay", "DistributionCenter", "DeliveryService"));
	}
	
	
	/**
	 * Logs the given employee of the given distribution center.
	 *
	 * @param person the employee
	 * @param distributionCenter the distribution center
	 */
	public void logEmployee(DeliveryPerson person, DistributionCenter distributionCenter) {
		String msg = "";
		
		msg += person.getId().getOid() + "; ";
		msg += distributionCenter.getName() + "; ";
		msg += distributionCenter.getOrganization() + "; ";
		msg += person.employment().name();
		
		this.results.write(resultCategoryEmployee, msg);
	}
	
	/**
	 * Creates the result category employee for delivery-employees results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryEmployee() {
		return new Category("delivery-employees", Arrays.asList("PeronID", "DistributionCenter", "Organization", "EmploymentType"));
	}
	
	
	/**
	 * Logs a delivery rescheduling event.
	 *
	 * @param person the delivery person
	 * @param currentTime the current time
	 * @param reason the reason
	 * @param before the before
	 * @param after the after
	 */
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
	
	/**
	 * Creates the result category rescheduling for delivery-rescheduling results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryRescheduling() {
		return new Category("delivery-rescheduling", Arrays.asList("PeronID", "Time", "Day", "Reason", "Before", "After"));
	}
	
}
