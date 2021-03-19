package edu.kit.ifv.mobitopp.simulation.parcels;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

/**
 * The Class DistributionCenter represents a distribution center from 
 * where delivery persons start to deliver parcels.
 */
@Getter
public class DistributionCenter {
	private String organization;
	private String name;
	private Zone zone;
	private Location location;
	private int numEmployees;
	private Collection<DeliveryPerson> employees;
	private double relativeShare;
	
	@Getter(value = lombok.AccessLevel.NONE)
	private Collection<Parcel> currentParcels;
	
	private Set<Parcel> delivered;
	private DeliveryTourAssignmentStrategy tourStrategy;
	private ParcelDeliveryPolicy policy;
	
	/**
	 * Instantiates a new distribution center.
	 *
	 * @param name the distribution centers name
	 * @param organization the organizations name
	 * @param zone the zone
	 * @param location the location
	 * @param numEmployees the number of employees
	 * @param share the share of parcels they receive
	 * @param tourStrategy the tour assignment strategy
	 * @param policy the delivery policy
	 */
	public DistributionCenter(String name, String organization, Zone zone, Location location, int numEmployees, double share, DeliveryTourAssignmentStrategy tourStrategy, ParcelDeliveryPolicy policy) {
		this.name = name;
		this.organization = organization;
		
		this.zone = zone;
		this.location = location;
		
		this.relativeShare = share;
		this.numEmployees = numEmployees;
		this.employees = new ArrayList<DeliveryPerson>();
		
		this.tourStrategy = tourStrategy;
		this.policy = policy;

		this.currentParcels = new ArrayList<Parcel>();
		this.delivered = new HashSet<Parcel>();
	}
	
	/**
	 * Assign parcels to the given delivery person.
	 *
	 * @param person the delivery person
	 * @param work the work
	 * @param currentTime the current time
	 * @return the list
	 */
	public List<List<Parcel>> assignParcels(DeliveryPerson person, ActivityIfc work, Time currentTime) {	
		List<Parcel> assigned = this.tourStrategy.assignParcels(this, person, work);
		List<Location> tourLocations = assigned.stream().map(p -> p.getLocation()).collect(toList());
			
		loadParcels(person, assigned, currentTime);
		
		
		Map<Location, List<Parcel>> locationMap = assigned.stream().collect(groupingBy(p -> p.getLocation()));
		List<Location> locationOrder = new ArrayList<Location>(locationMap.keySet());
		locationOrder.sort(Comparator.comparingInt(tourLocations::indexOf));
		
		List<List<Parcel>> parcelChunks = new ArrayList<List<Parcel>>();
		for (Location l : locationOrder) {
			List<Parcel> parcels = locationMap.get(l);
			List<Parcel> forPackStation = parcels.stream().filter(p -> p.getDestinationType().equals(ParcelDestinationType.PACK_STATION)).collect(toList());
			List<Parcel> otherParcels = parcels.stream().filter(p -> !p.getDestinationType().equals(ParcelDestinationType.PACK_STATION)).collect(toList());
			
			if (!forPackStation.isEmpty()) {
				parcelChunks.add(forPackStation);
			}
			
			if (! otherParcels.isEmpty()) {
				parcelChunks.addAll(otherParcels.stream().collect(groupingBy(p -> p.getPerson().household().getOid())).values());
			}
			
		}

		return parcelChunks;
	}

	/**
	 * The given delivery person loads the given parcels.
	 *
	 * @param person the person
	 * @param assigned the assigned
	 * @param currentTime the current time
	 */
	private void loadParcels(DeliveryPerson person, List<Parcel> assigned, Time currentTime) {
		person.load(assigned, currentTime);
		this.currentParcels.removeAll(assigned);
	}
	
	
	/**
	 * Unload parcels of the given delivery person.
	 *
	 * @param person the person
	 * @param currentTime the current time
	 */
	public void unloadParcels(DeliveryPerson person, Time currentTime) {
		Collection<Parcel> returning = person.unload(currentTime);
		currentParcels.addAll(returning);
	}
	
	
	/**
	 * Gets the currently available parcels.
	 * This includes all parcels that have arrived prior to the given time
	 * and have not been picked up by a delivery person.
	 *
	 * @param currentTime the current time
	 * @return the available parcels
	 */
	public List<Parcel> getAvailableParcels(Time currentTime) {
		return this.currentParcels.stream().filter(p -> p.getPlannedArrivalDate().isBeforeOrEqualTo(currentTime)).collect(toList());
	}
	

	
	/**
	 * Adds the parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void addParcelOrder(Parcel parcel) {
		this.currentParcels.add(parcel);
	}
	
	/**
	 * Removes the parcel order.
	 *
	 * @param parcel the parcel
	 */
	public void removeParcelOrder(Parcel parcel) {
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
	 * Checks if the given delivery person is an employee of this distribution center.
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

}
