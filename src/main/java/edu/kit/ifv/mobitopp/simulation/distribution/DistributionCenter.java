package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.NullParcelProducer;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryDurationModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryTourAssignmentStrategy;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class DistributionCenter represents a distribution center from where
 * delivery persons start to deliver parcels.
 */
public class DistributionCenter implements NullParcelProducer, Hook {
	@Getter private final int id;
	@Getter private final String organization;
	@Getter private final String name;
	@Getter private final Zone zone;
	@Getter private final Location location;
	@Getter private int numEmployees;
	@Getter private int attempts;
	@Getter private double sharePrivate;
	@Getter private double shareBusiness;
	@Getter private final DemandQuantity demandQuantity;
	
	private final Collection<DeliveryVehicle> vehicles;
	private final Map<DeliveryVehicle, Time> returnTimes;
	private final VehicleType vehicleType;
	
	private final Collection<IParcel> currentParcels;
	private final Collection<IParcel> collectedPickups;
	private final Collection<IParcel> pickupRequests;
	private final Collection<PlannedDeliveryTour> plannedTours;

			@Setter	private DeliveryTourAssignmentStrategy tourStrategy;
			@Setter	private ParcelPolicyProvider policyProvider;
			@Setter	private DeliveryClusteringStrategy clusteringStrategy;
	@Getter	@Setter private DeliveryDurationModel durationModel;
	@Getter			private final ParcelArrivalScheduler scheduler;
	
	

	/**
	 * Instantiates a new distribution center.
	 *
	 * @param id            the id
	 * @param name          the distribution centers name
	 * @param organization  the organizations name
	 * @param zone          the zone
	 * @param location      the location
	 * @param numEmployees  the number of employees
	 * @param sharePrivate  the market share for private recipients
	 * @param shareBusiness the market share for business recipients
	 * @param attempts      the maximum number of delivery attempts
	 */
	public DistributionCenter(int id, String name, String organization, Zone zone, Location location, int numEmployees,
			double sharePrivate, double shareBusiness, int attempts, VehicleType vehicleType) {
		this.id = id;
		this.name = name;
		this.organization = organization;

		this.zone = zone;
		this.location = location;

		this.attempts = attempts;
		this.sharePrivate = sharePrivate;
		this.shareBusiness = shareBusiness;
		this.numEmployees = numEmployees;
		
		this.vehicles = new ArrayList<>();
		
		this.returnTimes = new LinkedHashMap<>();
		this.vehicleType = vehicleType;

		this.currentParcels = new ArrayList<>();
		this.collectedPickups = new ArrayList<>();
		this.pickupRequests = new ArrayList<>();
		this.plannedTours = new ArrayList<>();
		
		this.scheduler = new ParcelArrivalScheduler(this);

		this.demandQuantity = new DemandQuantity();
	}
	
	
	
	@Override
	public void process(Time date) {
		// TODO check if it is time to (re)?plan delivery tours
		// TODO group deliveries and pickups to parcelActivities
		// TODO plan tours for parcel activities
		// TODO store planned tours
		
		// TODO check if vehicle can and should be dispatched -> dummy dispatch strategy time window, tour available and vehicle available
		// TODO dispatch tour
		// TODO check again
	}
	
	

//	/**
//	 * Assign parcels to the given delivery person.
//	 *
//	 * @param agent            the delivery person
//	 * @param work              the work
//	 * @param currentTime       the current time
//	 * @param remainingWorkTime the remaining work time
//	 * @return the list of assigned deliveries
//	 */
//	public List<ParcelActivityBuilder> assignParcels(DeliveryAgent agent, ActivityIfc work, Time currentTime,
//			RelativeTime remainingWorkTime) {
//		List<ParcelActivityBuilder> assigned = this.tourStrategy.assignParcels(this.getDeliveryActivities(currentTime),
//				agent, currentTime, remainingWorkTime, vehicleType);
//
//		removeParcels(agent, assigned, currentTime);
//
//		return assigned;
//	}
//
//	/**
//	 * The given delivery person loads the given parcels.
//	 *
//	 * @param person      the person
//	 * @param assigned    the assigned
//	 * @param currentTime the current time
//	 */
//	private void removeParcels(DeliveryAgent person, List<ParcelActivityBuilder> assigned, Time currentTime) {
//		assigned.forEach(d -> {
//			this.currentParcels.removeAll(d.getParcels());
//			this.pickupRequests.removeAll(d.getParcels());
//		});
//	}
//
//	/**
//	 * Unload parcels of the given delivery person.
//	 *
//	 * @param person      the person
//	 * @param currentTime the current time
//	 */
//	public void unloadParcels(DeliveryAgent person, Time currentTime) {
//		Collection<IParcel> returning = person.unload(currentTime);
//		currentParcels.addAll(returning);
//	}
//
//	/**
//	 * Gets the currently available parcels. This includes all parcels that have
//	 * arrived prior to the given time and have not been picked up by a delivery
//	 * person.
//	 *
//	 * @param currentTime the current time
//	 * @return the available parcels
//	 */
//	public List<IParcel> getAvailableParcels(Time currentTime) {
//		return this.currentParcels.stream().filter(p -> p.getPlannedArrivalDate().isBeforeOrEqualTo(currentTime))
//				.collect(toList());
//	}

	private List<ParcelActivityBuilder> getDeliveryActivities(Time currentTime) { //TODO move to clustering??
		List<ParcelActivityBuilder> deliveries = new ArrayList<>();

		List<IParcel> available = new ArrayList<>(currentParcels);
		available.addAll(pickupRequests);

		clusteringStrategy.cluster(available)
						  .forEach(cluster -> {
							  ParcelActivityBuilder activity = new ParcelActivityBuilder(clusteringStrategy).byDistributionCenter(this);
							  cluster.stream().filter(currentParcels::contains).forEach(activity::addParcel);
							  cluster.stream().filter(pickupRequests::contains).forEach(activity::addPickUp);
							  deliveries.add(activity);							  
						  });

		return deliveries;
	}

	@Override
	public void addParcel(IParcel parcel) {
		this.currentParcels.add(parcel);
	}

	@Override
	public void removeParcel(IParcel parcel) {
		this.currentParcels.remove(parcel);
	}

	public void removePickupRequest(IParcel parcel) {
		this.pickupRequests.remove(parcel);
	}
	
	@Override
	public void addDelivered(IParcel parcel) {
		this.collectedPickups.add(parcel);
	}

	public void requestPickup(IParcel parcel) {
		this.pickupRequests.add(parcel);
	}

	@Override
	public ParcelPolicyProvider getPolicyProvider() {
		return this.policyProvider;
	}

	/**
	 * Gets the zone and location of this distribution center.
	 *
	 * @return the zone and location
	 */
	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(this.zone, this.location);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public int currentDeliveryDemand() {
		return currentParcels.size();
	}

	public int currentShippingDemand() {
		return pickupRequests.size();
	}



	public void bookVehicleUntil(DeliveryVehicle vehicle, Time returnTime) {
		if (this.returnTimes.containsKey(vehicle)) {
			throw new IllegalArgumentException("Vehicle is already booked:" + vehicle + " until " + returnTimes.get(vehicle));
		}
		
		this.returnTimes.put(vehicle, returnTime);
		this.vehicles.remove(vehicle);		
	}
	
	public void returnVehicle(DeliveryVehicle vehicle) {
		if (!this.returnTimes.containsKey(vehicle)) {
			throw new IllegalArgumentException("The given vehicle is not booked and cannot be returned: " + vehicle);
		}
		
		this.returnTimes.remove(vehicle);
		this.vehicles.add(vehicle);
	}

	


}
