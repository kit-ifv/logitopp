package edu.kit.ifv.mobitopp.simulation;

import java.util.Arrays;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
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
	private final static Category resultCategoryNeighbordeliveries = createResultCategoryNeighborDeliveries();
	private final static Category resultCategoryVehicleEvents = createResultCategoryVehicleEvents();

	private final static Category resultCategoryPartners = createResultCategoryPartners();

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
	 * Log change of private parcel state.
	 *
	 * @param parcel          the parcel
	 * @param deliveryVehicle the delivery vehicle
	 * @param currentTime     the current time
	 * @param isAttempt       whether the state change was a delivery attempt
	 */
	public void logChange(PrivateParcel parcel, DeliveryVehicle deliveryVehicle, Time currentTime, boolean isAttempt) {
		String msg = "";
		msg += currentTime.toString() + SEP;
		msg += parcel.getOId() + SEP;
		msg += parcel.getPerson().getOid() + SEP;
		msg += parcel.getDestinationType().name() + SEP;
		msg += parcel.getState().name() + SEP;
		msg += isAttempt + SEP;
		msg += ((deliveryVehicle != null) ? deliveryVehicle.getId() : "NULL") + SEP;
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
	 * @return the result category defining the header for private parcel state
	 *         changes
	 */
	public static Category createResultCategoryStatePrivate() {
		return new Category("parcel-states",
				Arrays.asList("Time", "ParcelID", "RecipientID", "DestinationType", "State", "IsDeliveryAttempt",
						"DeliveryGuyID", "DistributionCenter", "DeliveryAttempts", "DeliveryTime", "RecipientType",
						"ParcelDestinationZone"));
	}

	/**
	 * Log change of business parcel state.
	 *
	 * @param parcel          the parcel
	 * @param deliveryVehicle the delivery vehicle
	 * @param currentTime     the current time
	 * @param isAttempt       whether the state change was a delivery attempt
	 */
	public void logChange(BusinessParcel parcel, DeliveryVehicle deliveryVehicle, Time currentTime, boolean isAttempt) {
		String msg = "";
		msg += currentTime.toString() + SEP;
		msg += parcel.getOId() + SEP;
		msg += parcel.getZone().getId().getExternalId() + SEP;
		msg += parcel.getLocation().coordinates() + SEP;
		msg += parcel.getState().name() + SEP;
		msg += isAttempt + SEP;
		msg += ((deliveryVehicle != null) ? deliveryVehicle.getId() : "NULL") + SEP;
		msg += parcel.getProducer().toString() + SEP;
		msg += parcel.getConsumer().toString() + SEP;
		msg += parcel.getDeliveryAttempts() + SEP;
		msg += String.valueOf(parcel.getDeliveryTime()) + SEP;
		msg += ((parcel.getRecipientType() != null) ? parcel.getRecipientType().name() : "NULL") + SEP;
		msg += parcel.getZone().getId();

		this.results.write(resultCategoryStateBusines, msg);
	}

	/**
	 * Creates the result category state business.
	 *
	 * @return the result category which defines the header for business parcel
	 *         state changes
	 */
	public static Category createResultCategoryStateBusiness() {
		return new Category("business-parcel-states",
				Arrays.asList("Time", "ParcelID", "ZoneId", "Location", "State", "IsDeliveryAttempt", "DeliveryGuyID",
						"Producer", "Consumer", "DeliveryAttempts", "DeliveryTime", "RecipientType",
						"ParcelDestinationZone"));
	}

	/**
	 * Logs the order of the given private parcel.
	 *
	 * @param parcel the ordered parcel
	 */
	public void logPrivateOrder(PrivateParcel parcel) {
		this.logPrivateOrder(parcel.getOId(), parcel.getPerson().getOid() + "", parcel.getShipmentSize(),
				parcel.getDestinationType().name(), parcel.getPlannedArrivalDate().getDay() + "",
				parcel.getProducer().toString(), parcel.getPlannedArrivalDate(), parcel.getZoneAndLocation(),
				parcel.getProducer().getZoneAndLocation());
	}

	/**
	 * Log private parcel order.
	 *
	 * @param pid                the pid
	 * @param recipient          the recipient
	 * @param shipmentSize       the shipment size
	 * @param destination        the destination
	 * @param day                the day
	 * @param distributionCenter the distribution center
	 * @param currentTime        the current time
	 */
	private void logPrivateOrder(int pid, String recipient, ShipmentSize shipmentSize, String destination, String day,
			String distributionCenter, Time currentTime, ZoneAndLocation recipientLoc, ZoneAndLocation dcLoc) {
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
		return new Category("parcel-orders-private",
				Arrays.asList("ParcelID", "Size", "RecipientID", "DestinationType", "DestinationZone", "DestinationX",
						"DestinationY", "ArrivalDay", "ArrivalTime", "DistributionCenter", "DcZone", "DcX", "DcY"));
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
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getPlannedArrivalDate(), category);
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
	 * @param category    the result category
	 */
	private void logBusinessOrder(int pid, ShipmentSize size, String from, String to, String zoneIdFrom,
			Location locationFrom, String zoneIdTo, Location locationTo, String day, Time currentTime,
			Category category) {
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
		msg += checkedNeighbors;

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

	/**
	 * Log potential business partner.
	 *
	 * @param business         the business
	 * @param cepsp            the cep service provider
	 * @param tag              the tag to identify the context of the partnership
	 * @param demand           the parcel demand of the given business
	 * @param relativeCapacity the relative capacity of the given business
	 * @param estimatedDemand  the estimated demand of the given business
	 * @param numOfPartners    the number of selected partners for the given
	 *                         business
	 */
	public void logBusinessPartner(Business business, CEPServiceProvider cepsp, String tag, int demand,
			double relativeCapacity, double estimatedDemand, int numOfPartners) {
		String msg = "";

		msg += business.getId() + SEP;
		msg += business.getSector().asInt() + SEP;
		msg += cepsp.getName() + SEP;
		msg += cepsp.getId() + SEP;
		msg += tag + SEP;
		msg += demand + SEP;
		msg += relativeCapacity + SEP;
		msg += estimatedDemand + SEP;
		msg += numOfPartners;

		results.write(resultCategoryPartners, msg);
	}

	/**
	 * Creates the result category partners.
	 *
	 * @return the result category which defines the header for partners of businesses
	 */
	private static Category createResultCategoryPartners() {
		return new Category("business_partners", Arrays.asList("Business", "Sector", "DictributionCenter", "DcId",
				"Tag", "BusinessDemand", "CapacityFactor", "EstimatedDemand", "NumOfPartners"));
	}
	
	
	
	
	
	/**
	 * Creates the result category vehicle events.
	 *
	 * @return the result category which defines the header for partners of businesses
	 */
	private static Category createResultCategoryVehicleEvents() {
		return new Category("vehicle_events", Arrays.asList("day", "time", "sim_sec", "cepsp", "dc", "dc_id", "veh_id",
				"event", "stop_no", "to_deliver", "success_delivery", "to_pickup", "success_pickup", "returning", "collected",
				"zone_id", "zone_column", "location", "distance", "trip-duration", "delivery-duration"));
	}
	
	public void logLoadEvent(DeliveryVehicle vehicle, Time time, int numStops, int toDeliver, int toPickUp, ZoneAndLocation location, double distance, int tripDuration, int deliveryDuration) {
		System.out.println(vehicle.getOwner().getName() + " " + vehicle.toString() + " leaves with " + toDeliver + " parcels and " + toPickUp + " requested pickups");
		this.logVehicleEvent(vehicle, time, "load", numStops, toDeliver, 0, toPickUp, 0, location, distance, tripDuration, deliveryDuration);
	}
	
	public void logStopEvent(DeliveryVehicle vehicle, Time time, int no, int toDeliver, int deliverySuccess, int toPickUp, int pickUpSuccess, ZoneAndLocation location, double distance, int tripDuration, int deliveryDuration) {
		System.out.println(vehicle.getOwner().getName() + " " + vehicle.toString() + " delivers " + deliverySuccess + "/" + toDeliver + " and  picks up " + pickUpSuccess + "/" + toPickUp + " parcels at " + location.location().toString());
		this.logVehicleEvent(vehicle, time, "stop", no, toDeliver, deliverySuccess, toPickUp, pickUpSuccess, location, distance, tripDuration, deliveryDuration);
	}
	
	public void logUnloadEvent(DeliveryVehicle vehicle, Time time, ZoneAndLocation location) {
		System.out.println(vehicle.getOwner().getName() + " " + vehicle.toString() + " returns with " + vehicle.getPickedUpParcels().size() + " picked up parcels and returns " + vehicle.getReturningParcels().size() + " unsuccessfull parcels.");
		this.logVehicleEvent(vehicle, time, "unload", -1, 0, 0, 0, 0, location, 0.0, 0, 0);
	}
	
	private void logVehicleEvent(DeliveryVehicle vehicle, Time time, String event, int no, int toDeliver, int deliverySuccess, int toPickUp, int pickUpSuccess, ZoneAndLocation location, double distance, int tripDuration, int deliveryDuration) {
		String msg = "";
		
		msg += time.getDay() + SEP;
		msg += time.toString() + SEP;
		msg += time.toSeconds() + SEP;
		
		msg += vehicle.getOwner().carrierTag() + SEP;
		msg += vehicle.getOwner().getName() + SEP;
		msg += vehicle.getOwner().getId() + SEP;
		
		msg += vehicle.getId() + SEP;
		
		msg += event + SEP;
		msg += no + SEP;
		
		msg += toDeliver + SEP;
		msg += deliverySuccess + SEP;
		msg += toPickUp + SEP;
		msg += pickUpSuccess+ SEP;
		msg += vehicle.getReturningParcels().size() + SEP;
		msg += vehicle.getPickedUpParcels().size() + SEP;
		
		msg += location.zone().getId().getExternalId() + SEP;
		msg += location.zone().getId().getMatrixColumn() + SEP;
		msg += location.location().toString() + SEP;
		
		msg += distance + SEP;
		msg += tripDuration + SEP;
		msg += deliveryDuration;
		
		
		this.results.write(resultCategoryVehicleEvents, msg);

	}
}
