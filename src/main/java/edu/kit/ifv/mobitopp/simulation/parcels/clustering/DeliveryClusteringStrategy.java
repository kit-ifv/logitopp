package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public interface DeliveryClusteringStrategy {
	
	public boolean canBeGrouped(IParcel a, IParcel b);
	
	public default Collection<ParcelCluster> cluster(List<IParcel> parcels, int maxCount) {
		return CollectionsUtil.groupBy(parcels, this::canBeGrouped)
							  .stream()
							  .flatMap(cluster -> partition(cluster, maxCount).stream())
							  .map(cluster -> new ParcelCluster(cluster, this))
							  .collect(toList());
	}
	
	private static List<List<IParcel>> partition(List<IParcel> cluster, int maxCount) {
		if (cluster.isEmpty()) {return List.of();}

		int totalCount = cluster.size();

		if (totalCount <= maxCount) {
			return List.of(cluster);
		}
		
		int numParts = (int) Math.ceil((double) totalCount / maxCount);
		int averageClusterSize = (int) Math.round( (cluster.size() * 1.0) / (numParts * 1.0));

		List<List<IParcel>> partitions = new ArrayList<>(numParts);
		for (int i=0; i < numParts; i++) {
			partitions.add(new ArrayList<>(averageClusterSize));
		}
		
		int i = 0;
		for (IParcel p : cluster) {
			partitions.get(i % numParts).add(p);
			i++;
		}
		
		return partitions.stream()
						 .filter(c -> !c.isEmpty())
						 .collect(toList());
	}

	public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster);
	
}
