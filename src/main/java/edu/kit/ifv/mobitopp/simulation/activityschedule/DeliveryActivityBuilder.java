package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static java.lang.Math.max;
import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class DeliveryActivityBuilder {

	protected DeliveryPerson deliveryPerson;
	protected ActivityIfc work;
	protected Time plannedTime;
	protected int tripDuration;
	protected List<IParcel> parcels;
	protected DeliveryClusteringStrategy clusteringStrategy;

	public DeliveryActivityBuilder(DeliveryClusteringStrategy clusteringStrategy) {
		this.parcels = new ArrayList<>();
		this.tripDuration = -1;
		this.clusteringStrategy = clusteringStrategy;
	}

	public DeliveryActivityBuilder addParcel(IParcel parcel) {
		this.parcels.add(parcel);
		return this;
	}

	public DeliveryActivityBuilder addParcels(Collection<IParcel> parcels) {
		this.parcels.addAll(parcels);
		return this;
	}

	public int estimateDuration(DeliveryEfficiencyProfile efficiency) {//TODO delivery duration model
		return max(1,
				round(efficiency.getDeliveryDurBase() + getParcels().size() * efficiency.getDeliveryDurPerParcel()));
	}

	public DeliveryActivityBuilder deliveredBy(DeliveryPerson person) {
		this.deliveryPerson = person;
		return this;
	}

	public DeliveryActivityBuilder during(ActivityIfc work) {
		this.work = work;
		return this;
	}

	public DeliveryActivityBuilder plannedAt(Time time) {
		this.plannedTime = time;
		return this;
	}
	
	public DeliveryActivityBuilder withTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
		return this;
	}

	public DeliveryActivity build() {
		int tripDur = (tripDuration > 0) ? tripDuration : 5;
		return DeliveryActivityFactory.createDeliveryActivity(parcels, work, plannedTime,
				estimateDuration(deliveryPerson.getEfficiency()), tripDur, deliveryPerson, getZoneAndLocation());
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

	public DeliveryActivityBuilder merge(DeliveryActivityBuilder other) {
		DeliveryActivityBuilder newBuilder = new DeliveryActivityBuilder(this.clusteringStrategy);
		newBuilder.addParcels(this.getParcels());
		newBuilder.addParcels(other.getParcels());
		
		return newBuilder;
	}

	
}
