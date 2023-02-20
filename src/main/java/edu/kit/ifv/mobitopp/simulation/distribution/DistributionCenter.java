package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
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
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
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
	@Getter private int numVehicles;
	@Getter private int attempts;
	@Getter private final DemandQuantity demandQuantity;
	
	
	private final Collection<DeliveryVehicle> vehicles;
	private final Map<DeliveryVehicle, Time> returnTimes;
	private final VehicleType vehicleType;
	
	private final Collection<IParcel> currentParcels;
	private final Collection<IParcel> collectedPickups;
	private final Collection<IParcel> pickupRequests;
	private final Collection<PlannedDeliveryTour> plannedTours;
	private final ImpedanceIfc impedance;
	
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
	 * @param numVehicles   the number of vehicles
	 * @param attempts      the maximum number of delivery attempts
	 */
	public DistributionCenter(int id, String name, String organization, Zone zone, Location location, int numVehicles,
			int attempts, VehicleType vehicleType, ImpedanceIfc impedance) {
		this.id = id;
		this.name = name;
		this.organization = organization;
		this.zone = zone;
		this.location = location;

		this.attempts = attempts;
		this.numVehicles = numVehicles;
		
		this.demandQuantity = new DemandQuantity();
		
		this.returnTimes = new LinkedHashMap<>();
		this.vehicleType = vehicleType;
		this.vehicles = new ArrayList<>();

		this.currentParcels = new ArrayList<>();
		this.collectedPickups = new ArrayList<>();
		this.pickupRequests = new ArrayList<>();
		this.plannedTours = new ArrayList<>();
		this.impedance = impedance;
		
		this.scheduler = new ParcelArrivalScheduler(this);

		initVehicles();
		
	}
	
	private void initVehicles() {
		
		for (int i = 0; i < numVehicles; i++) {
			vehicles.add(new DeliveryVehicle(vehicleType, 150, this)); //TODO determine capacity in vehicle type
		}
		
	}
	
	
	
	@Override
	public void process(Time currentTime) { //TODO register dc and its scheduler as hook (maybe dc calls its own scheduler instead of main hook)

		scheduler.process(currentTime);
		
		planTours(currentTime);
		dispatchAvailableTours(currentTime);
		
	}
	
	private void planTours(Time currentTime) {
		
		if (currentTime.equals(currentTime.startOfDay().plusHours(6))) { //TODO add replanning for non-hubs
			
			ArrayList<DeliveryVehicle> vehs = new ArrayList<>(this.vehicles);
			vehs.addAll(returnTimes.keySet());
			
			List<ParcelActivityBuilder> activities = getDeliveryActivities();
			
			if (!activities.isEmpty()) {
				this.plannedTours.addAll(
					this.tourStrategy.planTours(activities, vehs.iterator().next(), currentTime, RelativeTime.ofHours(8))
				);
			}
			
			System.out.println("	planned " + plannedTours.size() + " tours; " + vehicles.size() + " vehicles available!");

		}
		
		// TODO Auto-generated method stub
		// TODO check if it is time to (re)?plan delivery tours
		// TODO group deliveries and pickups to parcelActivities
		// TODO plan tours for parcel activities
		// TODO store planned tours
	}



	private void dispatchAvailableTours(Time currentTime) {
		
		if (canDispatch(currentTime)) {
			
			Optional<PlannedDeliveryTour> tour = plannedTours.stream().filter(t-> endsBeforeEndOfDeliveryTime(currentTime, t)).findFirst();
			
			
			if (tour.isPresent()) {
				DeliveryVehicle vehicle = vehicles.iterator().next();
				tour.get().dispatchTour(currentTime, vehicle, impedance);
				
				plannedTours.remove(tour.get());
				
				dispatchAvailableTours(currentTime);
			}						
		}
	}



	
	private boolean canDispatch(Time time) { //TODO extract dispatch times strategy
		int hour=time.getHour();
		
		if (time.weekDay().equals(DayOfWeek.SUNDAY)) {return false;}
		
		return !plannedTours.isEmpty() && !vehicles.isEmpty() && 8 <= hour && hour <= 18 && plannedTours.stream().anyMatch(t -> endsBeforeEndOfDeliveryTime(time, t));
	}

	private boolean endsBeforeEndOfDeliveryTime(Time time, PlannedDeliveryTour t) {
		return time.plus(t.getPlannedDuration()).getHour() <= 21;
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





	private List<ParcelActivityBuilder> getDeliveryActivities() { //TODO move to clustering??
		List<ParcelActivityBuilder> deliveries = new ArrayList<>();

		List<IParcel> available = new ArrayList<>(currentParcels);
		available.addAll(pickupRequests);
		
		if (available.isEmpty()) {
			return deliveries;
		}

		System.out.println(name + " processes " + available.size() + " parcels.");
		clusteringStrategy.cluster(available, 150) //TODO replace by vehicle type capacity
						  .stream()
						  .map(cluster -> new ParcelActivityBuilder(cluster.getParcels(), cluster.getZoneAndLocation()))
						  .forEach(deliveries::add);
		System.out.println(name + " processes " + deliveries.size() + " stops.");

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

	@Override
	public String carrierTag() {
		return this.name;
	}


}
