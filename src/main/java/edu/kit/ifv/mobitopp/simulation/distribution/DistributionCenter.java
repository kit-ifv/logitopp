package edu.kit.ifv.mobitopp.simulation.distribution;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.NullParcelProducer;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.region.RegionalReach;
import edu.kit.ifv.mobitopp.simulation.distribution.region.ServiceArea;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
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
	
	
	@Getter private int attempts;
	
	@Getter private final DemandQuantity demandQuantity;
	
	
	@Getter private final DepotStorage storage;
	@Getter private final RegionalReach regionalStructure;
	@Getter @Setter private DepotOperations operations;
	
	@Getter private final  Fleet fleet;
	
	
	/**
	 * Instantiates a new distribution center.
	 *
	 * @param id           the id
	 * @param name         the distribution centers name
	 * @param organization the organizations name
	 * @param zone         the zone
	 * @param location     the location
	 * @param numVehicles  the number of vehicles
	 * @param attempts     the maximum number of delivery attempts
	 * @param vehicleType  the vehicle type
	 * @param serviceArea  the center's service area
	 */
	public DistributionCenter(int id, String name, String organization, Zone zone, Location location, int numVehicles,
			int attempts, VehicleType vehicleType, ServiceArea serviceArea) {
		this.id = id;
		this.name = name;
		this.organization = organization;
		this.zone = zone;
		this.location = location;

		this.attempts = attempts;		
		this.demandQuantity = new DemandQuantity();
		
		this.storage = new DepotStorage();
		this.regionalStructure = new RegionalReach(this, serviceArea);
		this.fleet = new Fleet(vehicleType, numVehicles, this);
		
	}
	
	
	
	
	
	@Override
	public void process(Time currentTime) { //TODO register dc and its scheduler as hook (maybe dc calls its own scheduler instead of main hook)

		this.operations.update(currentTime);
		
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





//	private List<ParcelActivityBuilder> getDeliveryActivities() { //TODO move to clustering??
//		List<ParcelActivityBuilder> deliveries = new ArrayList<>();
//
//		List<IParcel> available = new ArrayList<>(currentParcels);
//		available.addAll(pickupRequests);
//		
//		if (available.isEmpty()) {
//			return deliveries;
//		}
//
//		System.out.println(name + " processes " + available.size() + " parcels.");
//		clusteringStrategy.cluster(available, 150) //TODO replace by vehicle type capacity
//						  .stream()
//						  .map(cluster -> new ParcelActivityBuilder(cluster.getParcels(), cluster.getZoneAndLocation()))
//						  .forEach(deliveries::add);
//		System.out.println(name + " processes " + deliveries.size() + " stops.");
//
//		return deliveries;
//	}

	@Override
	public void addParcel(IParcel parcel) {
		this.storage.addParcel(parcel);
	}

	@Override
	public void removeParcel(IParcel parcel) {
		this.storage.removeParcel(parcel);
	}

	public void removePickupRequest(IParcel parcel) {
		this.storage.removeRequest(parcel);
	}
	
	@Override
	public void addDelivered(IParcel parcel) {
		this.storage.receive(parcel);
	}

	public void requestPickup(IParcel parcel) {
		this.storage.addRequest(parcel);
	}
	
	
	public int currentDeliveryDemand() {
		return this.storage.currentDeliveryDemand();
	}

	public int currentShippingDemand() {
		return this.storage.currentShippingDemand();
	}
	
	public int getTotalVehicles() {
		return this.fleet.size();
	}
	
	public DeliveryResults getResults() {
		return this.operations.getResults();
	}

	@Override
	public ParcelPolicyProvider getPolicyProvider() {
		return this.operations.getPolicyProvider();
	}

	@Override
	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(this.zone, this.location);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String carrierTag() {
		return this.name;
	}

}
