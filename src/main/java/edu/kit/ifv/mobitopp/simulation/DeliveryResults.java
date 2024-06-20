package edu.kit.ifv.mobitopp.simulation;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.business.Business;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ShipmentSize;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumOrientedLink;
import lombok.Getter;

/**
 * The Class DeliveryResults provides methods for logging results concerned with
 * parcels and deliveries.
 */
@Getter
public class DeliveryResults {

	private static final String SEP = ";";
	private final static Category resultCategoryStatePrivate = createResultCategoryStatePrivate();
	private final static Category resultCategoryStateBusiness = createResultCategoryStateBusiness();
	private final static Category resultCategoryPrivateOrder = createResultCategoryPrivateOrder();
	private final static Category resultCategoryBusinessOrder = createResultCategoryBusinessOrder();
	private final static Category resultCategoryBusinessProduction = createResultCategoryBusinessProduction();
	private final static Category resultCategoryNeighborDeliveries = createResultCategoryNeighborDeliveries();
	private final static Category resultCategoryVehicleEvents = createResultCategoryVehicleEvents();
	private final static Category resultCategoryChainPreferences = createResultCategoryChainPreference();
	private final static Category resultCategoryPartners = createResultCategoryPartners();
	private final static Category resultCategoryServiceArea = createResultCategoryServiceArea();
	private final static Category resultCategoryFleet = createResultCategoryFleet();
	private final static Category resultCategoryBusinessLocation = createResultCategoryBusinessLocation();
	private final static Category resultCategoryPlannedTour = createResultCategoryPlannedTour();
	private final static Category resultCategoryNode = createResultCategoryNode();
	private final static Category resultCategoryEdge = createResultCategoryEdge();
	private final static Category resultCategoryDepotLocation = createResultCategoryDepotLocation();

	private final Results results;

	/**
	 * Instantiates a new delivery results object with the given {@link Results}
	 * object.
	 *
	 * @param results the results
	 */
	public DeliveryResults(Results results) {
		this.results = results;
	}

	private static String locationDataLog(ZoneAndLocation start, DistributionCenter depot) {
		String row = "";
		if (depot != null) {
			row += depot.getName() + SEP;
			row += depot.getId() + SEP;
		} else {
			row += SEP + SEP;
		}
		row += start.zone().getId().getExternalId() + SEP;
		row += start.location().coordinatesP().getX() + SEP;
		row += start.location().coordinatesP().getY() + SEP;
		row += start.location().roadAccessEdgeId() + SEP;
		row += start.location().roadPosition() + SEP;
		return row;
	}

	public void logPlannedTour(
			DistributionCenter center,
			DeliveryVehicle vehicle,
			PlannedTour tour,
			Time currentTime
	) {
		DistributionCenter owner = vehicle.getOwner();

		String header = "";

		header += tour.getId() + SEP;
		header += tour.journeyId() + SEP;
		header += tour + SEP;
		header += tour.isReturning() + SEP;

		header += center.getName() + SEP;
		header += center.getId() + SEP;
		header += currentTime + SEP;
		header += currentTime.toMinutes() + SEP;

		header += vehicle.getId() + SEP;
		header += vehicle.getTag() + SEP;
		header += vehicle.getType().asInt() + SEP;

		DistributionCenter fromDepot = vehicle.getOwner();
		DistributionCenter toDepot;
		if (tour.isReturning()) {
			toDepot = tour.depot();
		} else {
			toDepot = tour.nextHub().orElse(null);
		}

		ZoneAndLocation start = owner.getZoneAndLocation();
		ZoneAndLocation destination;

		double totalDeliveryVolume = tour.getPreparedStops().stream().mapToDouble(
				s -> s.getParcels().stream().mapToDouble(IParcel::getVolume).sum()
		).sum();

		double currentVolume = totalDeliveryVolume;

		for (ParcelActivity stop : tour.getPreparedStops()) {
			destination = stop.getStopLocation();

			if (tour.isReturning()) { //in this case tour should currently only have length 1
				destination = tour.depot().getZoneAndLocation();
			}

			String row = header;
			if (tour.isReturning()) {
				row += locationDataLog(destination, toDepot);
				row += locationDataLog(start, fromDepot);
			} else {
				row += locationDataLog(start, fromDepot);
				row += locationDataLog(destination, toDepot);
			}
			row += stop.getNo() + SEP;
			row += stop.getPlannedTime() + SEP;
			row += stop.getPlannedTime().toMinutes() + SEP;
			row += stop.getDistance() + SEP;
			row += stop.getTripDuration() + SEP;
			row += stop.getDeliveryDuration() + SEP;

			fromDepot=null;
			toDepot=null;

			String deliveries = stop.getParcels().stream().map(p -> p.getOId()+"").collect(Collectors.joining(","));
			String pickups = stop.getPickUps().stream().map(p -> p.getOId()+"").collect(Collectors.joining(","));

			List<IParcel> nestedParcels = stop.getParcels()
					.stream()
					.filter(p -> p instanceof PlannedTour)
					.flatMap(t -> ((PlannedTour) t).getDeliveryParcels().stream())
					.collect(Collectors.toList());

			List<IParcel> nestedPickups = stop.getParcels()
					.stream()
					.filter(p -> p instanceof PlannedTour)
					.flatMap(t -> ((PlannedTour) t).getPickUpRequests().stream())
					.collect(Collectors.toList());

			String nestedDeliveries = nestedParcels
					.stream()
					.map(p -> p.getOId()+"")
					.collect(Collectors.joining(","));

			String plannedPickups =  nestedPickups
					.stream()
					.map(p -> p.getOId()+"")
					.collect(Collectors.joining(","));

			if (tour.isReturning()) {
				row += SEP + pickups + (!pickups.isEmpty() && !deliveries.isEmpty() ? "," : "") + deliveries + SEP;
				row += SEP + nestedDeliveries + SEP;
			} else {
				row += deliveries + SEP + pickups + SEP + nestedDeliveries + SEP + SEP + plannedPickups;
			}

			double deliverVolume = stop.getParcels().stream().mapToDouble(IParcel::getVolume).sum();
			double pickupVolume = stop.getPickUps().stream().mapToDouble(IParcel::getVolume).sum();
			row += SEP;
			row += currentVolume + SEP;
			row += (deliverVolume+pickupVolume) + SEP;
			row += (currentVolume - deliverVolume + pickupVolume) + SEP;
			row += vehicle.getVolume();

			currentVolume = currentVolume - deliverVolume + pickupVolume;


			results.write(resultCategoryPlannedTour, row);

			start = destination;
		}

		//TODO return trip

	}

	public static Category createResultCategoryPlannedTour() {
		return new Category("planned-tours",
			Arrays.asList(
					"tourId", "journeyId", "tourDescription", "isReturning",
					"dispatcher", "dispatcherId", "dispatchTime", "dispatchSimMin",
					"vehicleId", "vehicleTag", "vehicleType",
					"fromDepot", "fromDepotId", "fromZone", "fromX", "fromY", "fromEdge", "fromEdgePos",
					"toDepot", "toDepotId", "toZone", "toX", "toY", "toEdge", "toEdgePos",
					"stopNo", "plannedTime", "plannedTimeSimMin", "distance", "tripDur", "deliveryDur",
					"deliveries", "pickups", "nestedDeliveries", "nestedPickups", "plannedLastMilePickups",
					"volumeBefore", "volumeChange", "volumeAfter", "maxVolume"
			)
		);
	}


	public void logServiceArea(DistributionCenter dc) {
		Collection<Zone> zones = dc.getRegionalStructure().getServiceArea().getZones();

		String dcIdentifier = dc.getName() + SEP + dc.getId() + SEP;

		StringBuilder msg = new StringBuilder();

		for (Zone zone : zones) {
			msg.append(dcIdentifier)
			   .append(zone.getId().getExternalId())
			   .append("\n");
		}

		results.write(resultCategoryServiceArea, msg.toString());
	}

	public static Category createResultCategoryServiceArea() {
		return new Category("service-area",
				Arrays.asList("distributionCenter","id", "zone"));
	}

	public void logBusinessPosition(Business business) {
		ZoneAndLocation zoneAndLocation = business.getZoneAndLocation();
		Location location = zoneAndLocation.location();
		Point2D coordinates = location.coordinatesP();

		String msg = "";

		msg += business.getName() + SEP;
		msg += business.getId() + SEP;
		msg += zoneAndLocation.zone().getId().getExternalId() + SEP;
		msg += coordinates.getX() + SEP;
		msg += coordinates.getY() + SEP;
		msg += location.roadAccessEdgeId() + SEP;
		msg += location.roadPosition();

		results.write(resultCategoryBusinessLocation, msg);
	}

	public static Category createResultCategoryBusinessLocation() {
		return new Category("business-location",
				Arrays.asList("business","id", "zone", "x", "y", "edge", "pos"));
	}

	public void logDepotPosition(DistributionCenter dc) {
		ZoneAndLocation zoneAndLocation = dc.getZoneAndLocation();
		Location location = zoneAndLocation.location();
		Point2D coordinates = location.coordinatesP();

		String msg = "";

		msg += dc.getName() + SEP;
		msg += dc.getId() + SEP;
		msg += zoneAndLocation.zone().getId().getExternalId() + SEP;
		msg += coordinates.getX() + SEP;
		msg += coordinates.getY() + SEP;
		msg += location.roadAccessEdgeId() + SEP;
		msg += location.roadPosition();

		results.write(resultCategoryDepotLocation, msg);
	}

	public static Category createResultCategoryDepotLocation() {
		return new Category("depot-location",
				Arrays.asList("depot","id", "zone", "x", "y", "edge", "pos"));
	}

	public void logNode(VisumNode node) {
		String msg = "";
		msg += node.id() + SEP;
		msg += node.name + SEP;
		msg += node.type + SEP;
		msg += node.coordinate().getX() + SEP;
		msg += node.coordinate().getY();

		results.write(resultCategoryNode, msg);
	}

	public static Category createResultCategoryNode() {
		return new Category("nodes",
				Arrays.asList("id","name", "type", "x", "y"));
	}

	public void logEdge(VisumOrientedLink edge) {
		String msg = "";
		msg += edge.id + SEP;
		msg += edge.name + SEP;
		msg += edge.from.id() + SEP;
		msg += edge.to.id() + SEP;
		msg += edge.length + SEP;
		msg += edge.linkType.name + SEP;
		msg += edge.transportSystems.transportSystems.stream().map(s -> s.code).collect(Collectors.joining(","));

		results.write(resultCategoryEdge, msg);
	}

	public static Category createResultCategoryEdge() {
		return new Category("edges",
				Arrays.asList("id","name", "from", "to", "length", "type", "transportSystems"));
	}

	public void logVehicle(DeliveryVehicle vehicle) {
		String msg = "";

		msg += vehicle.getOwner().getName() + SEP;
		msg += vehicle.getOwner().getId() + SEP;
		msg += vehicle.getId() + SEP;
		msg += vehicle.getType().name() + SEP;
		msg += vehicle.getType().asInt() + SEP;
		msg += vehicle.getVolume();

		results.write(resultCategoryFleet, msg);
	}

	public static Category createResultCategoryFleet() {
		return new Category("fleet",
				Arrays.asList("owner","ownerId", "vehicleId", "vehicleType", "vehicleTypeCode", "maxVolume"));
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
		msg += parcel.getZone().getId() + SEP;
		msg += parcel.isPickUp();

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
						"ParcelDestinationZone", "IsPickup"));
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
		msg += parcel.getZone().getId() + SEP;
		msg += parcel.isPickUp();

		this.results.write(resultCategoryStateBusiness, msg);
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
						"ParcelDestinationZone", "IsPickup"));
	}

	/**
	 * Logs the order of the given private parcel.
	 *
	 * @param parcel the ordered parcel
	 */
	public void logPrivateOrder(PrivateParcel parcel) {
		int person = parcel.getPerson().getOid();
		int household = parcel.getPerson().household().getOid();
		this.logPrivateOrder(parcel.getOId(), person, household, parcel.getShipmentSize(), parcel.getVolume(),
				parcel.getDestinationType().name(), parcel.getPlannedArrivalDate().getDay() + "",
                (DistributionCenter) parcel.getProducer(), parcel.getPlannedArrivalDate(), parcel.getZoneAndLocation()
		);
	}

	/**
	 * Log private parcel order.
	 *
	 * @param pid                the pid
	 * @param person          	 the recipient
	 * @param shipmentSize       the shipment size
	 * @param destination        the destination
	 * @param day                the day
	 * @param distributionCenter the distribution center
	 * @param currentTime        the current time
	 */
	private void logPrivateOrder(int pid, int person, int household, ShipmentSize shipmentSize, double volume, String destination, String day,
			DistributionCenter distributionCenter, Time currentTime, ZoneAndLocation recipientLoc) {
		String msg = "";

		msg += pid + SEP;
		msg += shipmentSize + SEP;
		msg += volume + SEP;
		msg += person + SEP;
		msg += household + SEP;
		msg += destination + SEP;
		msg += locationDataLog(recipientLoc, distributionCenter);
		msg += day + SEP;
		msg += currentTime.toString() + SEP;
		msg += distributionCenter.getZone().getId().getExternalId() + SEP;
		msg += distributionCenter.getLocation().coordinatesP().getX() + SEP;
		msg += distributionCenter.getLocation().coordinatesP().getY();

		this.results.write(resultCategoryPrivateOrder, msg);
	}

	/**
	 * Creates the result category order for private parcel-orders results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryPrivateOrder() {
		return new Category("parcel-orders-private",
				Arrays.asList("ParcelID", "Size", "Volume", "RecipientID", "HouseholdID", "DestinationType",
						"DistributionCenter", "DistributionCenterID",
						"DestinationZone", "DestinationX", "DestinationY", "DestinationEdge", "DestinationEdgePos",
						"ArrivalDay", "ArrivalTime",
						"DcZone", "DcX", "DcY"));
	}

	/**
	 * Log business parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void logBusinessOrder(BusinessParcel parcel) {
		ZoneAndLocation producerLoc = parcel.getProducer().getZoneAndLocation();
		ZoneAndLocation consumerLoc = parcel.getConsumer().getZoneAndLocation();

		Category category;
		String fromName;
		String toName;
		String fromId;
		String toId;


		if (parcel.getConsumer().equals(parcel.getBusiness())) {
			category = resultCategoryBusinessOrder;
			toName = parcel.getBusiness().getName();
			toId = parcel.getBusiness().getId()+"";
			fromName = ((DistributionCenter) parcel.getProducer()).getName();
			fromId = ((DistributionCenter) parcel.getProducer()).getId()+"";

		} else {
			category = resultCategoryBusinessProduction;
			fromName = parcel.getBusiness().getName();
			fromId = parcel.getBusiness().getId()+"";
			toName = ((DistributionCenter) parcel.getConsumer()).getName();
			toId = ((DistributionCenter) parcel.getConsumer()).getId()+"";
		}

		this.logBusinessOrder(parcel.getOId(), parcel.getShipmentSize(), parcel.getVolume(), fromName,
				toName, fromId, toId, producerLoc.zone().getId().getExternalId(), producerLoc.location(),
				consumerLoc.zone().getId().getExternalId(), consumerLoc.location(),
				parcel.getPlannedArrivalDate().getDay() + "", parcel.getPlannedArrivalDate(), category);
	}

	private void logBusinessOrder(int pid, ShipmentSize size, double volume, String from, String to, String fromId, String toId,
			String zoneIdFrom, Location locationFrom, String zoneIdTo, Location locationTo,
			String day, Time currentTime, Category category) {
		String msg = "";

		msg += pid + SEP;
		msg += size.name() + SEP;
		msg += volume + SEP;
		msg += from + SEP;
		msg += fromId + SEP;
		msg += to + SEP;
		msg += toId + SEP;
		msg += zoneIdFrom + SEP;
		msg += locationFrom.coordinate.getX() + SEP;
		msg += locationFrom.coordinate.getY() + SEP;
		msg += locationFrom.roadAccessEdgeId() + SEP;
		msg += locationFrom.roadPosition() + SEP;
		msg += zoneIdTo + SEP;
		msg += locationTo.coordinate.getX() + SEP;
		msg += locationTo.coordinate.getY() + SEP;
		msg += locationTo.roadAccessEdgeId() + SEP;
		msg += locationTo.roadPosition() + SEP;
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
				Arrays.asList("ParcelID", "Size", "Volume",
						"From", "FromId", "To", "ToId",
						"FromZoneId", "FromLocationX", "FromLocationY", "FromEdge", "FromEdgePos",
						"ToZoneId", "ToLocationX", "ToLocationY", "ToEdge", "ToEdgePos",
						"ArrivalDay", "ArrivalTime"));
	}

	/**
	 * Creates the result category order for business parcel-production results.
	 *
	 * @return the category
	 */
	public static Category createResultCategoryBusinessProduction() {
		return new Category("parcel-production-business",
				Arrays.asList("ParcelID", "Size", "Volume",
						"From", "FromId", "To", "ToId",
						"FromZoneId", "FromLocationX", "FromLocationY", "FromEdge", "FromEdgePos",
						"ToZoneId", "ToLocationX", "ToLocationY", "ToEdge", "ToEdgePos",
						"ArrivalDay", "ArrivalTime"));
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

		results.write(resultCategoryNeighborDeliveries, msg);
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
		return new Category("business_partners", Arrays.asList("Business", "Sector", "ServiceProvider",
				"ServiceProviderId", "Tag", "BusinessDemand", "CapacityFactor", "EstimatedDemand", "NumOfPartners"));
	}
	
	
	
	public void logLoadEvent(DeliveryVehicle vehicle, Time time, int tourId, int numStops, int toDeliver, int toPickUp, ZoneAndLocation location, double distance, int tripDuration, int deliveryDuration) {
		System.out.println(vehicle.getOwner().getName() + " " + vehicle + " leaves with " + toDeliver + " parcels and " + toPickUp + " requested pickups");
		this.logVehicleEvent(vehicle, time, "load", numStops, tourId, toDeliver, 0, toPickUp, 0, location, distance, tripDuration, deliveryDuration);
	}
	
	public void logStopEvent(DeliveryVehicle vehicle, Time time, int no, int tourId,  int toDeliver, int deliverySuccess, int toPickUp, int pickUpSuccess, ZoneAndLocation location, double distance, int tripDuration, int deliveryDuration) {
		System.out.println(vehicle.getOwner().getName() + " " + vehicle + " delivers " + deliverySuccess + "/" + toDeliver + " and  picks up " + pickUpSuccess + "/" + toPickUp + " parcels at " + location.location().toString());
		this.logVehicleEvent(vehicle, time, "stop", no, tourId, toDeliver, deliverySuccess, toPickUp, pickUpSuccess, location, distance, tripDuration, deliveryDuration);
	}
	
	public void logUnloadEvent(DeliveryVehicle vehicle, int tourId, Time time, ZoneAndLocation location) {
		System.out.println(vehicle.getOwner().getName() + " " + vehicle + " returns with " + vehicle.getPickedUpParcels().size() + " picked up parcels and returns " + vehicle.getReturningParcels().size() + " unsuccessfull parcels.");
		this.logVehicleEvent(vehicle, time, "unload", -1, tourId, 0, 0, 0, 0, location, 0.0, 0, 0);
	}
	private static Category createResultCategoryVehicleEvents() {
		return new Category("vehicle_events", Arrays.asList("day", "time", "sim_sec", "cepsp", "dc", "dc_id", "veh_id", "veh_name",
				"tour_id", "event",  "stop_no", "to_deliver", "success_delivery", "to_pickup", "success_pickup", "returning", "collected",
				"distance", "trip-duration", "delivery-duration",
				"zoneId", "locationX", "locationY", "locationEdge", "locationEdgePos"));
	}
	private void logVehicleEvent(DeliveryVehicle vehicle, Time time, String event, int no, int tourId, int toDeliver, int deliverySuccess, int toPickUp, int pickUpSuccess, ZoneAndLocation location, double distance, int tripDuration, int deliveryDuration) {
		String msg = "";
		
		msg += time.getDay() + SEP;
		msg += time + SEP;
		msg += time.toSeconds() + SEP;
		
		msg += vehicle.getOwner().carrierTag() + SEP;
		msg += vehicle.getOwner().getName() + SEP;
		msg += vehicle.getOwner().getId() + SEP;
		
		msg += vehicle.getId() + SEP;
		msg += vehicle.getTag() + SEP;

		msg += tourId + SEP;
		msg += event + SEP;
		msg += no + SEP;
		
		msg += toDeliver + SEP;
		msg += deliverySuccess + SEP;
		msg += toPickUp + SEP;
		msg += pickUpSuccess+ SEP;
		msg += vehicle.getReturningParcels().size() + SEP;
		msg += vehicle.getPickedUpParcels().size() + SEP;

		msg += distance + SEP;
		msg += tripDuration + SEP;
		msg += deliveryDuration + SEP;

		msg += location.zone().getId().getExternalId() + SEP;
		msg += location.location().coordinate.getX() + SEP;
		msg += location.location().coordinate.getY() + SEP;
		msg += location.location().roadAccessEdgeId() + SEP;
		msg += location.location().roadPosition();
		
		this.results.write(resultCategoryVehicleEvents, msg);

	}
	
	private static Category createResultCategoryChainPreference() {
		return new Category("chain_preferences", Arrays.asList("choice_id", "time", "sim_second",
				"parcel_id", "is_pickup",
				"from_hub", "to_hub", "modes",
				"probability", "utility", "cost", "duration", "distance", "capacity", "selected"));
	}
	
	public void logTransportChainPreference(int choiceId, Time time, IParcel parcel, TransportChain chain, double probability, double utility, double cost, double duration, double distance, int capacity, boolean selected) {
		String msg = "";
		
		msg += choiceId + SEP;
		msg += time.toString() + SEP;
		msg += time.toSeconds() + SEP;
		msg += parcel.getOId() + SEP;
		msg += parcel.isPickUp() + SEP;
		
		msg += chain.first().getName() + SEP;
		msg += chain.last().getName() + SEP;
		msg += chain.getVehicleTypes().stream().map(VehicleType::name).collect(Collectors.joining("-")) + SEP;
		
		msg += probability + SEP;
		msg += utility + SEP;
		msg += cost + SEP;
		msg += duration + SEP;
		msg += distance + SEP;
		msg += capacity + SEP;
		msg += selected;
		
		this.results.write(resultCategoryChainPreferences, msg);
	}
}
