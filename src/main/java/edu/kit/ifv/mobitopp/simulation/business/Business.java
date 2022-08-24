package edu.kit.ifv.mobitopp.simulation.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;
import lombok.Getter;

@Getter
public class Business implements ParcelAgent {

	private static final Pair<Time, Time> empty = new Pair<>(Time.future, Time.start);
	private final long id;
	private final String name;
	
	private final Branch branch;
	private final BuildingType buildingType;
	
	private final int employees;
	private final double area;
	private final Map<DayOfWeek, Pair<Time, Time>> openingHours;
	private final ZoneAndLocation location;
	private final Fleet fleet;
	
	private final ParcelPolicyProvider policyProvider;
	private final Random random;
	
	private final Collection<DistributionCenter> deliveryPartners;
	private final Collection<DistributionCenter> shippingPartners;

	private final Collection<IParcel> currentParcels;
	private final Collection<IParcel> delivered;
	
	private final DemandQuantity demandQuantity;
	
	public Business(long id, String name, Branch branch, BuildingType buildingType, int employees, double area,
			Map<DayOfWeek, Pair<Time, Time>> openingHours, ZoneAndLocation location, Fleet fleet,
			ParcelPolicyProvider policyProvider, Random random) {
		this.id = id;
		this.name = name;

		this.branch = branch;
		this.buildingType = buildingType;
		this.employees = employees;

		this.area = area;
		this.openingHours = openingHours;
		this.location = location;
		this.fleet = fleet;

		this.policyProvider = policyProvider;
		this.random = random;
		this.deliveryPartners = new ArrayList<>();
		this.shippingPartners = new ArrayList<>();

		this.currentParcels = new ArrayList<>();
		this.delivered = new ArrayList<>();
		
		this.demandQuantity = new DemandQuantity();
	}


	public boolean isOpen(Time currentTime) {
		Pair<Time, Time> interval = this.openingHours.getOrDefault(currentTime.weekDay(), empty);
		return interval.getFirst().isBeforeOrEqualTo(currentTime) && currentTime.isBeforeOrEqualTo(interval.getSecond());
	}
	
	public void addDeliveryPartner(DistributionCenter distributionCenter) {
		this.deliveryPartners.add(distributionCenter);
	}
	
	public void addShippingPartner(DistributionCenter distributionCenter) {
		this.shippingPartners.add(distributionCenter);
	}

	public ZoneAndLocation location() {
		return this.getLocation();
	}

	public double getNextRandom() {
		return this.random.nextDouble();
	}





	@Override
	public void removeParcel(IParcel parcel) {
		this.currentParcels.remove(parcel);
	}

	@Override
	public void addParcel(IParcel parcel) {
		//this.currentParcels.add(parcel);
		
		if (parcel.getConsumer() instanceof DistributionCenter) {//TODO fixx
			DistributionCenter dc = (DistributionCenter) parcel.getConsumer();
			
			System.out.println("Business " + this.id + " requests " + dc.getName() + " to pick up parcel " + parcel.getOId());
			dc.requestPickup(parcel);
		}
	}

	@Override
	public void addDelivered(IParcel parcel) {
		this.delivered.add(parcel);
	}
	
	@Override
	public ParcelPolicyProvider getPolicyProvider() {
		return this.policyProvider;
	}

	
	@Override
	public String toString() {
		return this.id+"";
	}


	@Override
	public ZoneAndLocation getZoneAndLocation() {
		return this.location();
	}

}
