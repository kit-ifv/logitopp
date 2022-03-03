package edu.kit.ifv.mobitopp.simulation.distribution;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.NullParcelProducer;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryTourAssignmentStrategy;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;
import lombok.Getter;

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
	private Collection<DeliveryPerson> employees;
	private double relativeShare;

	@Getter(value = lombok.AccessLevel.NONE)
	private Collection<IParcel> currentParcels;
	private Collection<IParcel> delivered;

	private DeliveryTourAssignmentStrategy tourStrategy;
	private ParcelPolicyProvider policyProvider;

	/**
	 * Instantiates a new distribution center.
	 *
	 * @param name           the distribution centers name
	 * @param organization   the organizations name
	 * @param zone           the zone
	 * @param location       the location
	 * @param numEmployees   the number of employees
	 * @param share          the share of parcels they receive
	 * @param tourStrategy   the tour assignment strategy
	 * @param policyProvider the policy provider
	 */
	public DistributionCenter(String name, String organization, Zone zone, Location location, int numEmployees,
			double share, DeliveryTourAssignmentStrategy tourStrategy, ParcelPolicyProvider policyProvider) {
		this.name = name;
		this.organization = organization;

		this.zone = zone;
		this.location = location;

		this.relativeShare = share;
		this.numEmployees = numEmployees;
		this.employees = new ArrayList<DeliveryPerson>();

		this.tourStrategy = tourStrategy;
		this.policyProvider = policyProvider;

		this.currentParcels = new ArrayList<>();
		this.delivered = new ArrayList<>();
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
	public List<DeliveryActivityBuilder> assignParcels(DeliveryPerson person, ActivityIfc work, Time currentTime,
			RelativeTime remainingWorkTime) {
		List<DeliveryActivityBuilder> assigned = this.tourStrategy
				.assignParcels(this.getDeliveryActivities(currentTime), person, currentTime, remainingWorkTime);

		loadParcels(person, assigned, currentTime);

		return assigned;
	}

	/**
	 * The given delivery person loads the given parcels.
	 *
	 * @param person      the person
	 * @param assigned    the assigned
	 * @param currentTime the current time
	 */
	private void loadParcels(DeliveryPerson person, List<DeliveryActivityBuilder> assigned, Time currentTime) {
		assigned.forEach(d -> {
			person.load(d.getParcels(), currentTime);
			this.currentParcels.removeAll(d.getParcels());
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

	public List<DeliveryActivityBuilder> getDeliveryActivities(Time currentTime) {
		List<DeliveryActivityBuilder> deliveries = new ArrayList<>();

		List<IParcel> available = getAvailableParcels(currentTime);

		CollectionsUtil.groupBy(available, IParcel::canBeDeliveredTogether)
				.forEach(pcls -> deliveries.add(new DeliveryActivityBuilder().addParcels(pcls)));

		return deliveries;
	}

	/**
	 * Adds the parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void addParcelOrder(IParcel parcel) {
		this.currentParcels.add(parcel);
	}

	/**
	 * Removes the parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void removeParcelOrder(IParcel parcel) {
		this.currentParcels.remove(parcel);
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
	
	
	
	
	public DistributionServiceProvider getServiceProvider() {
		return null;//TODO
	}



}
