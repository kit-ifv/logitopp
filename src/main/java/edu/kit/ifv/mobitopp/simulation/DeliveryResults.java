package edu.kit.ifv.mobitopp.simulation;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class DeliveryResults provides methods for logging results concerned with
 * parcels and deliveries.
 */
public class DeliveryResults {

	private static final String SEP = ";";
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
		msg += currentTime.toString() + SEP;
		msg += parcel.getOId() + SEP;
		msg += parcel.getPerson().getOid() + SEP;
		msg += parcel.getDestinationType().name() + SEP;
		msg += parcel.getState().name() + SEP;
		msg += isAttempt + SEP;
		msg += ((deliveryGuy != null) ? deliveryGuy.getOid() : "NULL") + SEP;
		msg += parcel.getProducer().toString() + SEP;
		msg += parcel.getDeliveryAttempts() + SEP;
		msg += String.valueOf(parcel.getDeliveryTime()) + SEP;
		msg += ((parcel.getRecipientType() != null) ? parcel.getRecipientType().name() : "NULL") + SEP;
		msg += ((deliveryGuy != null) ? deliveryGuy.currentActivity().zone().getId() : "NULL") + SEP;
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

	
	/**
	 * Log change of parcel state.
	 *
	 * @param parcel the parcel
	 * @param deliveryGuy the delivery guy
	 * @param currentTime the current time
	 * @param isAttempt the is attempt
	 */
	public void logChange(BusinessParcel parcel, DeliveryPerson deliveryGuy, Time currentTime, boolean isAttempt) {
		String msg = "";
		msg += currentTime.toString() + SEP;
		msg += parcel.getOId() + SEP;
		msg += parcel.getZone().getId().getExternalId() + SEP; //TODO remove whitespaces
		msg += parcel.getLocation().forLogging() + SEP;
		msg += parcel.getState().name() + SEP;
		msg += isAttempt + SEP;
		msg += ((deliveryGuy != null) ? deliveryGuy.getOid() : "NULL") + SEP;
		msg += parcel.getProducer().toString() + SEP;
		msg += parcel.getDeliveryAttempts() + SEP;
		msg += String.valueOf(parcel.getDeliveryTime()) + SEP;
		msg += ((parcel.getRecipientType() != null) ? parcel.getRecipientType().name() : "NULL") + SEP;
		msg += ((deliveryGuy != null) ? deliveryGuy.currentActivity().zone().getId() : "NULL") + SEP;
		msg += parcel.getZone().getId();

		this.results.write(resultCategoryStateBusines, msg);
	}

	
	/**
	 * Creates the result category state business.
	 *
	 * @return the category
	 */
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
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getProducer().toString(), parcel.getPlannedArrivalDate());
	}

	/**
	 * Log private parcel order.
	 *
	 * @param pid the pid
	 * @param recipient the recipient
	 * @param destination the destination
	 * @param day the day
	 * @param distributionCenter the distribution center
	 * @param currentTime the current time
	 */
	private void logPrivateOrder(int pid, String recipient, String destination, String day, String distributionCenter, Time currentTime) {
		String msg = "";

		msg += pid + SEP;
		msg += recipient + SEP;
		msg += destination + SEP;
		msg += day + SEP;
		msg += currentTime.toString() + SEP;
		msg += distributionCenter;

		this.results.write(resultCategoryPrivateOrder, msg);
	}

	/**
	 * Creates the result category order for private parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryPrivateOrder() {
		return new Category("parcel-orders-private", Arrays.asList("ParcelID", "RecipientID", "DestinationType", "ArrivalDay", "ArrivalTime",
				"DistributionCenter", "DeliveryService"));
	}
	
	
	
	
	
	/**
	 * Log business parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void logBusinessOrder(BusinessParcel parcel) {
		this.logBusinessOrder(parcel.getOId(), parcel.getZone().getId().getExternalId(), parcel.getLocation(),
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getProducer().toString(), parcel.getConsumer().toString(), parcel.getPlannedArrivalDate());
	}

	/**
	 * Log business parcel order.
	 *
	 * @param pid the parcel id
	 * @param zoneId the zone id
	 * @param location the location
	 * @param day the day
	 * @param from the producer
	 * @param to the consumer
	 * @param currentTime the current time
	 */
	private void logBusinessOrder(int pid, String zoneId, Location location, String day, String from, String to, Time currentTime) {
		String msg = "";

		msg += pid + SEP;
		msg += zoneId + SEP;
		msg += location.coordinate.getX() + SEP;
		msg += location.coordinate.getY() + SEP;
		msg += day + SEP;
		msg += currentTime + SEP;
		msg += from + SEP;
		msg += to;

		this.results.write(resultCategoryBusinessOrder, msg);
	}
	
	/**
	 * Creates the result category order for private parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryBusinessOrder() {
		return new Category("parcel-orders-business", Arrays.asList("ParcelID", "ZoneId", "LocationX", "LocationY", "ArrivalDay", "ArrivalTime",
				"From", "To"));
	}

	
	
	
	
	/**
	 * Logs the given employee of the given distribution center.
	 *
	 * @param person             the employee
	 * @param distributionCenter the distribution center
	 */
	public void logEmployee(DeliveryPerson person, DistributionCenter distributionCenter) {
		String msg = "";

		msg += person.getId().getOid() + SEP;
		msg += distributionCenter.getName() + SEP;
		msg += distributionCenter.getOrganization() + SEP;
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

		msg += person.getId().getOid() + SEP;
		msg += currentTime.toString() + SEP;
		msg += currentTime.weekDay().name() + SEP;
		msg += reason + SEP;
		msg += before + SEP;
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
	
	
	
	
	
	/**
	 * Log neighbor parcel delivery.
	 *
	 * @param id the id
	 * @param zone the zone
	 * @param currentTime the current time
	 * @param success the success state
	 * @param numOfNeighbors the number of neighbors
	 * @param checkedNeighbors the number of checked neighbors
	 */
	public void logNeighborDelivery(int id, Zone zone, Time currentTime, boolean success, int numOfNeighbors, int checkedNeighbors) {
		String msg = "";
		
		msg += id + SEP;
		msg += zone.getId().getExternalId() + SEP;
		msg += currentTime.toString() + SEP;
		msg += currentTime.weekDay().name() + SEP;
		msg += success + SEP;
		msg += numOfNeighbors + SEP;
		msg += checkedNeighbors + SEP;
		
		results.write(resultCategoryNeighbordeliveries, msg);
	}
	
	/**
	 * Creates the result category neighbor deliveries.
	 *
	 * @return the category
	 */
	private static Category createResultCategoryNeighborDeliveries() {
		return new Category("neighbor-deliveries", Arrays.asList("Delivery", "Zone", "Time", "Day", "Success", "NumberOfNeighbors", "CheckedNeighbors"));
	}

}
