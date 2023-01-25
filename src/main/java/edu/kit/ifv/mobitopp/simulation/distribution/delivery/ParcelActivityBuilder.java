package edu.kit.ifv.mobitopp.simulation.distribution.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class ParcelActivityBuilder {
	protected DistributionCenter distributionCenter;
	protected DeliveryVehicle deliveryVehicle;
	protected Time plannedArrivalTime;
	
	protected final List<IParcel> parcels;
	protected final List<IParcel> pickUps;

	
	protected DeliveryClusteringStrategy clusteringStrategy;

	public ParcelActivityBuilder(DeliveryClusteringStrategy clusteringStrategy) {
		this.parcels = new ArrayList<>();
		this.pickUps = new ArrayList<>();
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
	
	public ParcelActivityBuilder addPickUp(IParcel pickUp) {
		this.pickUps.add(pickUp);
		return this;
	}

	public ParcelActivityBuilder addPickUps(Collection<IParcel> pickUps) {
		this.pickUps.addAll(pickUps);
		return this;
	}

	public int estimateDuration() {
		return (int) distributionCenter.getDurationModel().estimateDuration(deliveryVehicle, parcels);
	}

	public ParcelActivityBuilder plannedAt(Time time) {
		this.plannedArrivalTime = time;
		return this;
	}
	
	public ParcelActivityBuilder byDistributionCenter(DistributionCenter distributionCenter) {
		this.distributionCenter = distributionCenter;
		return this;
	}
	
	public ParcelActivity buildWorkerActivity() {
		return new ParcelActivity(getZoneAndLocation(), parcels, pickUps, deliveryVehicle, plannedArrivalTime);		
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

	public int size() {
		return this.parcels.size();
	}
	
	public int volume() {
		return this.parcels.stream().mapToInt(p -> p.getShipmentSize().getVolume(p)).sum();
	}
	
	public ParcelActivityBuilder merge(ParcelActivityBuilder other) {
		ParcelActivityBuilder newBuilder = new ParcelActivityBuilder(this.clusteringStrategy);
		newBuilder.addParcels(this.getParcels());
		newBuilder.addParcels(other.getParcels());
		
		return newBuilder;
	}

	
}
