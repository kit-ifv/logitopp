package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class ParcelActivityBuilder {
	protected DistributionCenter distributionCenter;
	protected DeliveryAgent deliveryAgent;
	protected ActivityIfc work;
	protected Time plannedTime;
	protected int tripDuration;
	protected List<IParcel> parcels;
	protected boolean isPickup;
	protected DeliveryClusteringStrategy clusteringStrategy;

	public ParcelActivityBuilder(DeliveryClusteringStrategy clusteringStrategy) {
		this.parcels = new ArrayList<>();
		this.tripDuration = -1;
		this.clusteringStrategy = clusteringStrategy;
	}

	public ParcelActivityBuilder addParcel(IParcel parcel) {
		this.parcels.add(parcel);
		return this;
	}

	public ParcelActivityBuilder addParcels(Collection<IParcel> parcels) {
		this.parcels.addAll(parcels);
		return this;
	}

	public int estimateDuration() {
		return (int) distributionCenter.getDurationModel().estimateDuration(deliveryAgent, parcels);
	}

	public ParcelActivityBuilder deliveredBy(DeliveryPerson person) {
		this.deliveryAgent = person;
		return this;
	}

	public ParcelActivityBuilder during(ActivityIfc work) {
		this.work = work;
		return this;
	}

	public ParcelActivityBuilder plannedAt(Time time) {
		this.plannedTime = time;
		return this;
	}
	
	public ParcelActivityBuilder withTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
		return this;
	}
	
	public ParcelActivityBuilder asPickup() {
		this.isPickup = true;
		return this;
	}
	
	public ParcelActivityBuilder asDelivery() {
		this.isPickup = false;
		return this;
	}
	
	public ParcelActivityBuilder byDistributionCenter(DistributionCenter distributionCenter) {
		this.distributionCenter = distributionCenter;
		return this;
	}
	
	
	
	public PersonParcelActivity buildPersonActivity() {
		int tripDur = (tripDuration > 0) ? tripDuration : 5;
		DeliveryPerson deliveryPerson = (DeliveryPerson) deliveryAgent;
		
		if (isPickup) {
			return DeliveryActivityFactory.createPickupActivity(parcels, work, plannedTime,
					estimateDuration(), tripDur, deliveryPerson, getZoneAndLocation());
			
		} else {
			return DeliveryActivityFactory.createDeliveryActivity(parcels, work, plannedTime,
				estimateDuration(), tripDur, deliveryPerson, getZoneAndLocation());
		}
	}
	
	public ParcelActivity buildWorkerActivity() {
		
		if (isPickup) {
			return new PickupActivity(getZoneAndLocation(), parcels, deliveryAgent, plannedTime);
		} else {
			return new DeliveryActivity(getZoneAndLocation(), parcels, deliveryAgent, plannedTime);
		}
		
	}
	
	
	
	
	
	public ZoneAndLocation getZoneAndLocation() {
		return clusteringStrategy.getStopLocation(this.parcels);
	}
	

	public Zone getZone() {
		return this.getZoneAndLocation().zone();
	}

	public Location getLocation() {
		return this.getZoneAndLocation().location();
	}

	public ParcelActivityBuilder merge(ParcelActivityBuilder other) {
		ParcelActivityBuilder newBuilder = new ParcelActivityBuilder(this.clusteringStrategy);
		newBuilder.addParcels(this.getParcels());
		newBuilder.addParcels(other.getParcels());
		
		return newBuilder;
	}

	
}
