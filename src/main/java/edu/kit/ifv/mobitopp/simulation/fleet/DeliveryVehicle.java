package edu.kit.ifv.mobitopp.simulation.fleet;

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
	private final int capacity;
	private final List<IParcel> returningParcels;
	private final List<IParcel> pickedUpParcels;
	
	public DeliveryVehicle(VehicleType type, int capacity, DistributionCenter owner) {
		this.id = idCnt++;
		this.owner = owner;
		this.type = type;
		this.capacity = capacity;
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
		System.out.println(this.owner.getName() + " " + this.toString() + " returns with " + pickedUpParcels.size() + " picked up parcels and returns " + returningParcels.size() + " unsuccessfull parcels.");
		
		this.returningParcels.forEach(p -> p.unload(currentTime, this));
		this.returningParcels.forEach(owner::addParcel);
		this.pickedUpParcels.forEach(p -> p.tryDelivery(currentTime, this)); //TODO unpack if container, add precalculated bike route etc. (maybe call deliver here??)
		
		this.returningParcels.clear();
		this.pickedUpParcels.clear();
		this.owner.returnVehicle(this);
	}
	
	@Override
	public String toString() {
		return "veh_" + owner.getId() + "_" + this.id;
	}
}
