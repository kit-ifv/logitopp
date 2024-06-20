package edu.kit.ifv.mobitopp.simulation.distribution.fleet;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class DeliveryVehicle {
	private static int idCnt = 0;
	
	private final int id;
	private final DistributionCenter owner;
	private final VehicleType type;
	private final double volume;
	private final int maxParcelCount;
	private final List<IParcel> returningParcels;
	private final List<IParcel> pickedUpParcels;
	private final String tag;

	@Getter
	private int currentTour = 0;

	public DeliveryVehicle(VehicleType type, double volume, DistributionCenter owner, int maxParcelCount) {
		this.maxParcelCount = maxParcelCount;
		this.id = idCnt++;
		this.owner = owner;
		this.type = type;
		this.volume = volume;
		this.tag = type.name() + "_" + owner.getId() + "_" + this.id;
		this.returningParcels = new ArrayList<>();
		this.pickedUpParcels = new ArrayList<>();
	}

	public DeliveryVehicle(VehicleType type, int volume, DistributionCenter owner, int maxParcelCount, String tag) {
		this.maxParcelCount = maxParcelCount;
		this.id = idCnt++;
		this.owner = owner;
		this.type = type;
		this.volume = volume;
		this.tag = tag;
		this.returningParcels = new ArrayList<>();
		this.pickedUpParcels = new ArrayList<>();
	}
	
	public void addReturningParcel(IParcel parcel) {
		this.returningParcels.add(parcel);
	}
	
	public void addPickedUpParcel(IParcel parcel) {
		this.pickedUpParcels.add(parcel);
	}
	
	public void unloadAndReturn(Time currentTime) {
		owner.getResults().logUnloadEvent(this, currentTour, currentTime, owner.getZoneAndLocation());
		
		this.returningParcels.forEach(owner::addParcel);		
		this.returningParcels.forEach(p -> p.unload(currentTime, this));
		
		this.pickedUpParcels.forEach(p -> p.tryDelivery(currentTime, this)); //TODO unpack if container, add precalculated bike route etc. (maybe call deliver here??)
		
		this.returningParcels.clear();
		this.pickedUpParcels.clear();
		setCurrentTour(0);

		this.owner.getFleet().returnVehicle(this);
	}

	public void setCurrentTour(int currentTour) {
		this.currentTour = currentTour;
	}

	@Override
	public String toString() {
		return this.tag;
	}
}
