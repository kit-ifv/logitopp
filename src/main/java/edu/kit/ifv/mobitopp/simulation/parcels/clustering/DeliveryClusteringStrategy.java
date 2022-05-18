package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;

public interface DeliveryClusteringStrategy {
	
	public boolean cabBeGrouped(IParcel a, IParcel b);
	
	default Collection<List<IParcel>> cluster(List<IParcel> parcels) {
		return CollectionsUtil.groupBy(parcels, this::cabBeGrouped);
	}
	
	public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster);
	
}
