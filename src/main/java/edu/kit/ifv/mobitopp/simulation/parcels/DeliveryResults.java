package edu.kit.ifv.mobitopp.simulation.parcels;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.parcels.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DeliveryResults provides methods for logging results concerned with
 * parcels and deliveries.
 */
public class DeliveryResults {

	private final static Category resultCategoryStatePrivate = createResultCategoryStatePrivate();
	private final static Category resultCategoryStateBusines = createResultCategoryStatePrivate();
	private final static Category resultCategoryPrivateOrder = createResultCategoryPrivateOrder();
	private final static Category resultCategoryBusinessOrder = createResultCategoryBusinessOrder();
	private final static Category resultCategoryEmployee = createResultCategoryEmployee();
	private final static Category resultCategoryRescheduling = createResultCategoryRescheduling();
	private final static Category resultCategoryNeighbordeliveries = createResultCategoryNeighborDeliveries();

	private Results results;

	/**
	 * Instantiates a new delivery results object with the given {@link Results}
	 * object.
	 *
	 * @param results the results
	 */
	public DeliveryResults(Results results) {
		this.results = results;
	}

	
	
	
	/**
	 * Log a parcels new state.
	 *
	 * @param parcel      the parcel
	 * @param deliveryGuy the delivery guy
	 * @param currentTime the current time
	 * @param isAttempt   the is attempt
	 */
	public void logChange(PrivateParcel parcel, DeliveryPerson deliveryGuy, Time currentTime, boolean isAttempt) {
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

		this.results.write(resultCategoryStatePrivate, msg);
	}

	/**
	 * Creates the result category for parcel-states results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryStatePrivate() {
		return new Category("parcel-states",
				Arrays.asList("Time", "ParcelID", "RecipientID", "DestinationType", "State", "IsDeliveryAttempt",
						"DeliveryGuyID", "DistributionCenter", "DeliveryAttempts", "DeliveryTime", "RecipientType",
						"CurrentDeliveryGuyZone", "ParcelDestinationZone"));
	}

	
	public void logChange(BusinessParcel parcel, DeliveryPerson deliveryGuy, Time currentTime, boolean isAttempt) {
		String msg = "";
		msg += currentTime.toString() + "; ";
		msg += parcel.getOId() + "; ";
		msg += parcel.getZone().getId().getExternalId() + "; ";
		msg += parcel.getLocation().forLogging() + "; ";
		msg += parcel.getState().name() + "; ";
		msg += isAttempt + "; ";
		msg += ((deliveryGuy != null) ? deliveryGuy.getOid() : "NULL") + "; ";
		msg += parcel.getDistributionCenter().getName() + "; ";
		msg += parcel.getDeliveryAttempts() + "; ";
		msg += String.valueOf(parcel.getDeliveryTime()) + ";";
		msg += ((parcel.getRecipientType() != null) ? parcel.getRecipientType().name() : "NULL") + "; ";
		msg += ((deliveryGuy != null) ? deliveryGuy.currentActivity().zone().getId() : "NULL") + "; ";
		msg += parcel.getZone().getId();

		this.results.write(resultCategoryStateBusines, msg);
	}

	
	public static Category createResultCategoryStateBusiness() {
		return new Category("business-parcel-states",
				Arrays.asList("Time", "ParcelID", "ZoneId", "Location", "State", "IsDeliveryAttempt",
						"DeliveryGuyID", "DistributionCenter", "DeliveryAttempts", "DeliveryTime", "RecipientType",
						"CurrentDeliveryGuyZone", "ParcelDestinationZone"));
	}
	
	
	
	/**
	 * Logs the order of the given parcel.
	 *
	 * @param parcel the ordered parcel
	 */
	public void logPrivateOrder(PrivateParcel parcel) {
		this.logPrivateOrder(parcel.getOId(), parcel.getPerson().getOid() + "", parcel.getDestinationType().name(),
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getDistributionCenter().getName());
	}

	private void logPrivateOrder(int pid, String recipient, String destination, String day, String distributioneCneter) {
		String msg = "";

		msg += pid + "; ";
		msg += recipient + "; ";
		msg += destination + "; ";
		msg += day + "; ";
		msg += distributioneCneter;

		this.results.write(resultCategoryPrivateOrder, msg);
	}

	/**
	 * Creates the result category order for private parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryPrivateOrder() {
		return new Category("parcel-orders", Arrays.asList("ParcelID", "RecipientID", "DestinationType", "ArrivalDay",
				"DistributionCenter", "DeliveryService"));
	}
	
	
	
	
	
	public void logBusinessOrder(BusinessParcel parcel) {
		this.logBusinessOrder(parcel.getOId(), parcel.getZone().getId().getExternalId(), parcel.getLocation().forLogging(),
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getDistributionCenter().getName());
	}

	private void logBusinessOrder(int pid, String zoneId, String location, String day, String distributioneCneter) {
		String msg = "";

		msg += pid + "; ";
		msg += zoneId + "; ";
		msg += location + "; ";
		msg += day + "; ";
		msg += distributioneCneter;

		this.results.write(resultCategoryBusinessOrder, msg);
	}
	
	/**
	 * Creates the result category order for private parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryBusinessOrder() {
		return new Category("parcel-orders", Arrays.asList("ParcelID", "ZoneId", "Location", "ArrivalDay",
				"DistributionCenter", "DeliveryService"));
	}

	
	
	
	
	/**
	 * Logs the given employee of the given distribution center.
	 *
	 * @param person             the employee
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
		return new Category("delivery-employees",
				Arrays.asList("PeronID", "DistributionCenter", "Organization", "EmploymentType"));
	}

	
	
	
	
	/**
	 * Logs a delivery rescheduling event.
	 *
	 * @param person      the delivery person
	 * @param currentTime the current time
	 * @param reason      the reason
	 * @param before      the before
	 * @param after       the after
	 */
	public void logDeliveryReschedulingEvent(DeliveryPerson person, Time currentTime, String reason, String before,
			String after) {
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
		return new Category("delivery-rescheduling",
				Arrays.asList("PeronID", "Time", "Day", "Reason", "Before", "After"));
	}
	
	
	
	
	
	public void logNeighborDelivery(int id, Zone zone, Time currentTime, boolean success, int numOfneighbors, int checkedNeighbors) {
		String msg = "";
		
		msg += id + ";";
		msg += zone.getId().getExternalId() + ";";
		msg += currentTime.toString() + ";";
		msg += currentTime.weekDay().name() + ";";
		msg += success + ";";
		msg += numOfneighbors + ";";
		msg += checkedNeighbors + ";";
		
		results.write(resultCategoryNeighbordeliveries, msg);
	}
	
	private static Category createResultCategoryNeighborDeliveries() {
		return new Category("neighbor-deliveries", Arrays.asList("Delivery", "Zone", "Time", "Day", "Success", "NumberOfNeighbors", "CheckedNeighbors"));
	}

}
