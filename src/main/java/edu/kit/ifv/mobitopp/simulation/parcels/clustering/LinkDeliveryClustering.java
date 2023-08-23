package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class LinkDeliveryClustering implements DeliveryClusteringStrategy {

	@Override
	public boolean canBeGrouped(IParcel a, IParcel b) {
		return areOnSameLink(a,b);
	}
	 
	private boolean areOnSameLink(IParcel a, IParcel b) {
		return zoneId(a).equals(zoneId(b)) && linkId(a) == linkId(b);
	}

	private ZoneId zoneId(IParcel p) {
		return p.getZone().getId();
	}

	private int linkId(IParcel a) {
		return Math.abs(a.getLocation().roadAccessEdgeId);
	}

	@Override
	public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster) {
		Comparator<IParcel> comp = Comparator.comparing(p -> p.getLocation().roadPosition);
		List<IParcel> sorted = deliveryCluster.stream().sorted(comp).collect(toList());
		
		
		return sorted.get(sorted.size()/2).getZoneAndLocation();
	}

}
