package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;

public interface DeliveryClusteringStrategy {
	
	public boolean canBeGrouped(IParcel a, IParcel b);
	
	public default Collection<ParcelCluster> cluster(List<IParcel> parcels, int maxClusterSize) {
		return CollectionsUtil.groupBy(parcels, this::canBeGrouped)
							  .stream()
							  .flatMap(cluster -> partition(cluster, maxClusterSize).stream())
							  .map(cluster -> new ParcelCluster(cluster, this))
							  .collect(toList());
	}
	
	public static <T> List<List<T>> partition(List<T> cluster, int maxSize) {
		if (cluster.size() <= maxSize) {
			return List.of(cluster);
		}
		
		int numParts = (int) Math.ceil((cluster.size() * 1.0) / (maxSize * 1.0));
		List<List<T>> partitions = new ArrayList<>(numParts);
		for (int i=0; i < numParts; i++) {
			partitions.add(new ArrayList<>(maxSize)); //TODO: average size
		}
		
		int i = 0;
		for (T t : cluster) {
			partitions.get(i % numParts).add(t);
			i++;
		}
		
		return partitions;
	};

	public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster);
	
}
