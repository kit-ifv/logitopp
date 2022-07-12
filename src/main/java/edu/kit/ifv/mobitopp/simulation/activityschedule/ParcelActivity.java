package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public abstract class ParcelActivity {
	
	protected final Collection<IParcel> parcels;
	protected final ZoneAndLocation stopLocation;
	protected final Time plannedTime;
	protected final DeliveryAgent agent;
	
	public ParcelActivity(ZoneAndLocation stopLocation, Collection<IParcel> parcels, DeliveryAgent agent, Time plannedTime) {
		this.parcels = parcels;
		this.stopLocation = stopLocation;
		this.plannedTime = plannedTime;
		this.agent = agent;
	}
	
	public ZoneAndLocation getZoneAndLocation() {
		return this.stopLocation;
	}
	
	public Time startDate() {
		return this.plannedTime;
	}
	
	public abstract void prepareAvtivity(Time currentTime);
	
	public abstract void executeActivity(Time currentTime);
	
	public abstract void abortActivity(Time currentTime);

}
