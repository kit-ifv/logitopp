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

@Getter
public class DistributionCenter {
	private String organization;
	private String name;
	private Zone zone;
	private Location location;
	private int numEmployees;
	private Collection<DeliveryPerson> employees;
	private float relativeShare;
	
//	@Getter(value = lombok.AccessLevel.NONE)
//	private Collection<Parcel> allParcels;
	@Getter(value = lombok.AccessLevel.NONE)
	private Collection<Parcel> currentParcels;
	
	private Set<Parcel> delivered;
	private DeliveryTourAssignmentStrategy tourStrategy;
	private ParcelDeliveryPolicy policy;
	
	public DistributionCenter(String name, String organization, Zone zone, Location location, int numEmployees, float share, DeliveryTourAssignmentStrategy tourStrategy, ParcelDeliveryPolicy policy) {
		this.name = name;
		this.organization = organization;
		
		this.zone = zone;
		this.location = location;
		
		this.relativeShare = share;
		this.numEmployees = numEmployees;
		this.employees = new ArrayList<DeliveryPerson>();
		
		this.tourStrategy = tourStrategy;
		this.policy = policy;
		
//		this.allParcels = new ArrayList<Parcel>();
		this.currentParcels = new ArrayList<Parcel>();
		this.delivered = new HashSet<Parcel>();
		
	}
	
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

	private void loadParcels(DeliveryPerson person, List<Parcel> assigned, Time currentTime) {
		person.load(assigned, currentTime);
		this.currentParcels.removeAll(assigned);
	}
	
	
	public void unloadParcels(DeliveryPerson person, Time currenTime) {
		Collection<Parcel> returning = person.unload(currenTime);
		currentParcels.addAll(returning);
	}
	
	
	public List<Parcel> getAvailableParcels(Time currentTime) {
		return this.currentParcels.stream().filter(p -> p.getPlannedArrivalDate().isBeforeOrEqualTo(currentTime)).collect(toList());
	}
	
//	public Collection<Parcel> getAssignedParcels(DeliveryPerson person) {//TODO check if this method can be removed
//		return person.getCurrentTour();
//	}

	
	public void addParcelOrder(Parcel parcel) {
//		this.allParcels.add(parcel);
		this.currentParcels.add(parcel);
	}
	
	public void removeParcelOrder(Parcel parcel) {
//		this.allParcels.remove(parcel);
		this.currentParcels.remove(parcel);
	}
	
	public boolean addEmployee(DeliveryPerson p) {
		return this.employees.add(p);
	}
	
	public boolean hasEnoughEmployees() {
		return this.employees.size() >= this.numEmployees;
	}

	public boolean isEmployee(Person p) {
		return employees.contains(p);
	}
	
	public ZoneAndLocation getZoneAndLocation() {
		return new ZoneAndLocation(this.zone, this.location);
	}

}
