package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class AgentDeliveryClustering implements DeliveryClusteringStrategy {

	@Override
	public boolean canBeGrouped(IParcel a, IParcel b) {
		return haveSameAgent(a,b);
	}
	 
	private boolean haveSameAgent(IParcel a, IParcel b) {
		return agent(a).equals(agent(b));
	}

	private ParcelAgent agent(IParcel a) {
		if (a.isPickUp()) {
			return a.getProducer();
		} else {
			return a.getConsumer();
		}
	}

	@Override
	public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster) {
		return deliveryCluster.iterator().next().getZoneAndLocation();
	}

}
