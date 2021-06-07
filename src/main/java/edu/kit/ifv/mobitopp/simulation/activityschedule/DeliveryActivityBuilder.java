package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static java.lang.Math.max;
import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryEfficiencyProfile;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class DeliveryActivityBuilder {

	private DeliveryPerson deliveryPerson;
	private ActivityIfc work;
	private Time plannedTime;
	private Collection<IParcel> parcels;

	public DeliveryActivityBuilder() {
		this.parcels = new ArrayList<>();
	}

	public DeliveryActivityBuilder addParcel(IParcel parcel) {
		this.parcels.add(parcel);
		verifyLocations();
		return this;
	}

	public DeliveryActivityBuilder addParcels(Collection<IParcel> parcels) {
		this.parcels.addAll(parcels);
		verifyLocations();
		return this;
	}

	public int estimateDuration(DeliveryEfficiencyProfile efficiency) {
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

	public DeliveryActivity build() {
		return DeliveryActivityFactory.createDeliveryActivity(parcels, work, plannedTime,
				estimateDuration(deliveryPerson.getEfficiency()), deliveryPerson);
	}

	private void verifyLocations() {
		if (this.parcels.stream().map(IParcel::getLocation).distinct().count() > 1) {
			throw new IllegalStateException(
					"All parcels within a delivery activity should have the same delivery location.");
		}
	}
	
	public Location getLocation() {
		if (this.parcels.isEmpty()) {
			throw new IllegalStateException("Cannot determine location of delivery without parcels");
		}
		
		return parcels.iterator().next().getLocation();
	}
	
	public Zone getZone() {
		if (this.parcels.isEmpty()) {
			throw new IllegalStateException("Cannot determine zone of delivery without parcels");
		}
		
		return parcels.iterator().next().getZone();
	}

}
