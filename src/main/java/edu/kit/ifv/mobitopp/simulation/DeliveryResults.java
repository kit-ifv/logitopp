package edu.kit.ifv.mobitopp.simulation;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

/**
 * The Class DeliveryResults provides methods for logging results concerned with
 * parcels and deliveries.
 */
public class DeliveryResults {

	private static final String SEP = ";";
	private final static Category resultCategoryStatePrivate = createResultCategoryStatePrivate();
	private final static Category resultCategoryStateBusines = createResultCategoryStateBusiness();
	private final static Category resultCategoryPrivateOrder = createResultCategoryPrivateOrder();
	private final static Category resultCategoryBusinessOrder = createResultCategoryBusinessOrder();
	private final static Category resultCategoryBusinessProduction = createResultCategoryBusinessProduction();
	private final static Category resultCategoryEmployee = createResultCategoryEmployee();
	private final static Category resultCategoryRescheduling = createResultCategoryRescheduling();
	private final static Category resultCategoryNeighbordeliveries = createResultCategoryNeighborDeliveries();

	@Getter
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
	public void logChange(PrivateParcel parcel, DeliveryAgent deliveryGuy, Time currentTime, boolean isAttempt) {
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
						"ParcelDestinationZone"));
	}

	/**
	 * Log change of parcel state.
	 *
	 * @param parcel      the parcel
	 * @param deliveryGuy the delivery guy
	 * @param currentTime the current time
	 * @param isAttempt   the is attempt
	 */
	public void logChange(BusinessParcel parcel, DeliveryAgent deliveryGuy, Time currentTime, boolean isAttempt) {
		String msg = "";
		msg += currentTime.toString() + SEP;
		msg += parcel.getOId() + SEP;
		msg += parcel.getZone().getId().getExternalId() + SEP;
		msg += parcel.getLocation().coordinates() + SEP;
		msg += parcel.getState().name() + SEP;
		msg += isAttempt + SEP;
		msg += ((deliveryGuy != null) ? deliveryGuy.getOid() : "NULL") + SEP;
		msg += parcel.getProducer().toString() + SEP;
		msg += parcel.getDeliveryAttempts() + SEP;
		msg += String.valueOf(parcel.getDeliveryTime()) + SEP;
		msg += ((parcel.getRecipientType() != null) ? parcel.getRecipientType().name() : "NULL") + SEP;
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
				Arrays.asList("Time", "ParcelID", "ZoneId", "Location", "State", "IsDeliveryAttempt", "DeliveryGuyID",
						"Producer", "DeliveryAttempts", "DeliveryTime", "RecipientType", "ParcelDestinationZone"));
	}

	/**
	 * Logs the order of the given parcel.
	 *
	 * @param parcel the ordered parcel
	 */
	public void logPrivateOrder(PrivateParcel parcel) {
		this.logPrivateOrder(parcel.getOId(), parcel.getPerson().getOid() + "", parcel.getShipmentSize(), parcel.getDestinationType().name(),
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getProducer().toString(),
				parcel.getPlannedArrivalDate(), parcel.getZoneAndLocation(), parcel.getProducer().getZoneAndLocation());
	}

	/**
	 * Log private parcel order.
	 *
	 * @param pid                the pid
	 * @param recipient          the recipient
	 * @param shipmentSize		 the shipment size
	 * @param destination        the destination
	 * @param day                the day
	 * @param distributionCenter the distribution center
	 * @param currentTime        the current time
	 */
	private void logPrivateOrder(int pid, String recipient, ShipmentSize shipmentSize, String destination, String day, String distributionCenter,
			Time currentTime, ZoneAndLocation recipientLoc, ZoneAndLocation dcLoc) {
		String msg = "";

		msg += pid + SEP;
		msg += shipmentSize + SEP;
		msg += recipient + SEP;
		msg += destination + SEP;
		msg += recipientLoc.zone().getId().getExternalId() + SEP;
		msg += recipientLoc.location().coordinatesP().getX() + SEP;
		msg += recipientLoc.location().coordinatesP().getY() + SEP;
		msg += day + SEP;
		msg += currentTime.toString() + SEP;
		msg += distributionCenter + SEP;
		msg += distributionCenter + SEP;
		msg += dcLoc.zone().getId().getExternalId() + SEP;
		msg += dcLoc.location().coordinatesP().getX() + SEP;
		msg += dcLoc.location().coordinatesP().getY();
		

		this.results.write(resultCategoryPrivateOrder, msg);
	}

	/**
	 * Creates the result category order for private parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryPrivateOrder() {
		return new Category("parcel-orders-private", Arrays.asList("ParcelID", "Size", "RecipientID", "DestinationType", "DestinationZone", "DestinationX", "DestinationY",
				"ArrivalDay", "ArrivalTime", "DistributionCenter", "DeliveryService", "DcZone", "DcX", "DcY"));
	}

	/**
	 * Log business parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void logBusinessOrder(BusinessParcel parcel) {
		ZoneAndLocation producerLoc = parcel.getProducer().getZoneAndLocation();
		ZoneAndLocation consumerLoc = parcel.getConsumer().getZoneAndLocation();
		
		Category category = resultCategoryBusinessProduction;
		
		if (parcel.getConsumer().equals(parcel.getBusiness())) {
			category = resultCategoryBusinessOrder;
		}
		
		this.logBusinessOrder(parcel.getOId(), parcel.getShipmentSize(), parcel.getProducer().toString(),
				parcel.getConsumer().toString(), producerLoc.zone().getId().getExternalId(), producerLoc.location(),
				consumerLoc.zone().getId().getExternalId(), consumerLoc.location(),
				parcel.getPlannedArrivalDate().getDay() + "",  parcel.getPlannedArrivalDate(), category);
	}

	/**
	 * Log business parcel order.
	 *
	 * @param pid         the parcel id
	 * @param zoneId      the zone id
	 * @param location    the location
	 * @param day         the day
	 * @param from        the producer
	 * @param to          the consumer
	 * @param currentTime the current time
	 * @param category 	  the result category
	 */
	private void logBusinessOrder(int pid, ShipmentSize size, String from, String to, String zoneIdFrom, Location locationFrom, String zoneIdTo,
			Location locationTo, String day,  Time currentTime, Category category) {
		String msg = "";

		msg += pid + SEP;
		msg += size.name() + SEP;
		msg += from + SEP;
		msg += to + SEP;
		msg += zoneIdFrom + SEP;
		msg += locationFrom.coordinate.getX() + SEP;
		msg += locationFrom.coordinate.getY() + SEP;
		msg += zoneIdTo + SEP;
		msg += locationTo.coordinate.getX() + SEP;
		msg += locationTo.coordinate.getY() + SEP;
		msg += day + SEP;
		msg += currentTime;
		

		this.results.write(category, msg);
	}

	/**
	 * Creates the result category order for business parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryBusinessOrder() {
		return new Category("parcel-orders-business",
				Arrays.asList("ParcelID", "Size", "From", "To", "FromZoneId", "FromLocationX", "FromLocationY",
						"ToZoneId", "ToLocationX", "ToLocationY", "ArrivalDay", "ArrivalTime"));
	}
	
	/**
	 * Creates the result category order for business parcel-production results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryBusinessProduction() {
		return new Category("parcel-production-business",
				Arrays.asList("ParcelID", "Size", "From", "To", "FromZoneId", "FromLocationX", "FromLocationY",
						"ToZoneId", "ToLocationX", "ToLocationY", "ArrivalDay", "ArrivalTime"));
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
	 * @param id               the id
	 * @param zone             the zone
	 * @param currentTime      the current time
	 * @param success          the success state
	 * @param numOfNeighbors   the number of neighbors
	 * @param checkedNeighbors the number of checked neighbors
	 */
	public void logNeighborDelivery(int id, Zone zone, Time currentTime, boolean success, int numOfNeighbors,
			int checkedNeighbors) {
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
		return new Category("neighbor-deliveries",
				Arrays.asList("Delivery", "Zone", "Time", "Day", "Success", "NumberOfNeighbors", "CheckedNeighbors"));
	}

}
