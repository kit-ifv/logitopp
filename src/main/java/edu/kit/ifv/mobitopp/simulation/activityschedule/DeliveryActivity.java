package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;
import edu.kit.ifv.mobitopp.time.Time;

public class DeliveryActivity extends ParcelActivity {

	public DeliveryActivity(ZoneAndLocation stopLocation, Collection<IParcel> parcels, DeliveryAgent agent, Time plannedTime) {
		super(stopLocation, parcels, agent, plannedTime);
	}
	
	@Override
	public void prepareAvtivity(Time currentTime) {
		this.getParcels().forEach(p -> p.loaded(currentTime, agent));
		
		agent.planDeliveries(List.of(this), currentTime);
	}

	/**
	 * Try delivery.
	 *
	 * @param currentTime the current time
	 */
	public void executeActivity(Time currentTime) {
		parcels.forEach(p -> p.tryDelivery(currentTime, agent));
		
		agent.remove(this);
	}

	/**
	 * Abort delivery.
	 *
	 * @param currentTime the current time
	 */
	public void abortActivity(Time currentTime) {
		parcels.forEach(p -> p.returning(currentTime, agent));
	}



}