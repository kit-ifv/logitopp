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
	private int tripDuration;
	private Collection<IParcel> parcels;

	public DeliveryActivityBuilder() {
		this.parcels = new ArrayList<>();
		this.tripDuration = -1;
	}

	public DeliveryActivityBuilder addParcel(IParcel parcel) {
		this.parcels.add(parcel);
		verifyZone();
		return this;
	}

	public DeliveryActivityBuilder addParcels(Collection<IParcel> parcels) {
		this.parcels.addAll(parcels);
		verifyZone();
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
	
	public DeliveryActivityBuilder withTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
		return this;
	}
	

	public DeliveryActivity build() {
		int tripDur = (tripDuration > 0) ? tripDuration : 5;
		return DeliveryActivityFactory.createDeliveryActivity(parcels, work, plannedTime,
				estimateDuration(deliveryPerson.getEfficiency()), tripDur, deliveryPerson);
	}

	protected void verifyZone() {
		if (this.parcels.stream().map(IParcel::getZone).distinct().count() > 1) {
			throw new IllegalStateException(
					"All parcels within a delivery activity should have the same delivery zone.");
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
	
	
	public DeliveryActivityBuilder merge(DeliveryActivityBuilder other) {
		return new DeliveryActivityBuilder().addParcels(this.getParcels()).addParcels(other.getParcels());
	}

}
