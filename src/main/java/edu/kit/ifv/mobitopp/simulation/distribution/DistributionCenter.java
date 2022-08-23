package edu.kit.ifv.mobitopp.simulation.distribution;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.NullParcelProducer;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryDurationModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryTourAssignmentStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

/**
 * The Class DistributionCenter represents a distribution center from where
 * delivery persons start to deliver parcels.
 */
@Getter
public class DistributionCenter implements NullParcelProducer {
	private String organization;
	private String name;
	private Zone zone;
	private Location location;
	private int numEmployees;
	private int attempts;
	private Collection<DeliveryPerson> employees;
	private double relativeShare;

	@Getter(value = lombok.AccessLevel.NONE)
	private Collection<IParcel> currentParcels;
	private Collection<IParcel> delivered;
	private Collection<IParcel> pickupRequests;

	@Setter private DeliveryTourAssignmentStrategy tourStrategy;
	@Setter private ParcelPolicyProvider policyProvider;
	@Setter private DeliveryClusteringStrategy clusteringStrategy;
	@Getter @Setter private DeliveryDurationModel durationModel;
	
	private final DemandQuantity demandQuantity;

	/**
	 * Instantiates a new distribution center.
	 *
	 * @param name           the distribution centers name
	 * @param organization   the organizations name
	 * @param zone           the zone
	 * @param location       the location
	 * @param numEmployees   the number of employees
	 * @param share          the share of parcels they receive
	 * @param attempts 		 the maximum number of delivery attempts
	 */
	public DistributionCenter(String name, String organization, Zone zone, Location location, int numEmployees,
			double share, int attempts) {
		this.name = name;
		this.organization = organization;

		this.zone = zone;
		this.location = location;
		
		this.attempts = attempts;
		this.relativeShare = share;
		this.numEmployees = numEmployees;
		this.employees = new ArrayList<DeliveryPerson>();

		this.currentParcels = new ArrayList<>();
		this.delivered = new ArrayList<>();
		this.pickupRequests = new ArrayList<>();
		
		this.demandQuantity = new DemandQuantity();
	}

	/**
	 * Assign parcels to the given delivery person.
	 *
	 * @param person            the delivery person
	 * @param work              the work
	 * @param currentTime       the current time
	 * @param remainingWorkTime the remaining work time
	 * @return the list of assigned deliveries
	 */
	public List<ParcelActivityBuilder> assignParcels(DeliveryPerson person, ActivityIfc work, Time currentTime,
			RelativeTime remainingWorkTime) {
		List<ParcelActivityBuilder> assigned = this.tourStrategy
				.assignParcels(this.getDeliveryActivities(currentTime), person, currentTime, remainingWorkTime);

		removeParcels(person, assigned, currentTime);

		return assigned;
	}

	/**
	 * The given delivery person loads the given parcels.
	 *
	 * @param person      the person
	 * @param assigned    the assigned
	 * @param currentTime the current time
	 */
	private void removeParcels(DeliveryPerson person, List<ParcelActivityBuilder> assigned, Time currentTime) {
		assigned.forEach(d -> {
			this.currentParcels.removeAll(d.getParcels());
			this.pickupRequests.removeAll(d.getParcels());
		});
	}

	/**
	 * Unload parcels of the given delivery person.
	 *
	 * @param person      the person
	 * @param currentTime the current time
	 */
	public void unloadParcels(DeliveryPerson person, Time currentTime) {
		Collection<IParcel> returning = person.unload(currentTime);
		currentParcels.addAll(returning);
	}

	/**
	 * Gets the currently available parcels. This includes all parcels that have
	 * arrived prior to the given time and have not been picked up by a delivery
	 * person.
	 *
	 * @param currentTime the current time
	 * @return the available parcels
	 */
	public List<IParcel> getAvailableParcels(Time currentTime) {
		return this.currentParcels.stream().filter(p -> p.getPlannedArrivalDate().isBeforeOrEqualTo(currentTime))
				.collect(toList());
	}

	public List<ParcelActivityBuilder> getDeliveryActivities(Time currentTime) {
		List<ParcelActivityBuilder> deliveries = new ArrayList<>();

		List<IParcel> available = getAvailableParcels(currentTime);
		
		clusteringStrategy.cluster(available)
						  .forEach(cluster -> deliveries.add(
								  new ParcelActivityBuilder(clusteringStrategy).addParcels(cluster).asDelivery().byDistributionCenter(this)
						));
		
		clusteringStrategy.cluster(new ArrayList<>(pickupRequests))
						  .forEach(cluster -> deliveries.add(
								  new ParcelActivityBuilder(clusteringStrategy).addParcels(cluster).asPickup().byDistributionCenter(this)
						   ));

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
	
	@Override
	public void addDelivered(IParcel parcel) {
		this.delivered.add(parcel);
	}
	
	public void requestPickup(IParcel parcel) {
		this.pickupRequests.add(parcel);
	}
	
	@Override
	public ParcelPolicyProvider getPolicyProvider() {
		return this.policyProvider;
	}

	/**
	 * Adds the employee.
	 *
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean addEmployee(DeliveryPerson p) {
		return this.employees.add(p);
	}

	/**
	 * Checks whether this distribution center has enough employees.
	 *
	 * @return true, if successful
	 */
	public boolean hasEnoughEmployees() {
		return this.employees.size() >= this.numEmployees;
	}

	/**
	 * Checks if the given delivery person is an employee of this distribution
	 * center.
	 *
	 * @param p the p
	 * @return true, if is employee
	 */
	public boolean isEmployee(Person p) {
		return employees.contains(p);
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

}
