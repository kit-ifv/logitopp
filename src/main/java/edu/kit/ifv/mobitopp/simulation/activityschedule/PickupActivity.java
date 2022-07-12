package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.person.DeliveryAgent;
import edu.kit.ifv.mobitopp.time.Time;

public class PickupActivity extends ParcelActivity {

	public PickupActivity(ZoneAndLocation stopLocation, Collection<IParcel> parcels, DeliveryAgent agent, Time plannedTime) {
		super(stopLocation, parcels, agent, plannedTime);
	}
	
	@Override
	public void prepareAvtivity(Time currentTime) {
		agent.planDeliveries(List.of(this), currentTime);
	}
	
	@Override
	public void executeActivity(Time currentTime) {
		this.getParcels().forEach(p -> {
			p.loaded(currentTime, agent);
			agent.pickup(p);
			p.getProducer().removeParcel(p);
		});

	}

	@Override
	public void abortActivity(Time currentTime) {
		agent.remove(this);
		this.getParcels().forEach(p -> p.getProducer().addParcel(p));
	}




}
