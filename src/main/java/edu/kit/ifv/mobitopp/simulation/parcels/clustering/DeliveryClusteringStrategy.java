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
	
	public default Collection<ParcelCluster> cluster(List<IParcel> parcels, double maxVolume) {
		return CollectionsUtil.groupBy(parcels, this::canBeGrouped)
							  .stream()
							  .flatMap(cluster -> partition(cluster, maxVolume).stream())
							  .map(cluster -> new ParcelCluster(cluster, this))
							  .collect(toList());
	}
	
	private static List<List<IParcel>> partition(List<IParcel> cluster, double maxVolume) {
		if (cluster.isEmpty()) {return List.of();}

		double totalVolume = cluster.stream().mapToDouble(IParcel::getVolume).sum();

		if (totalVolume <= maxVolume) {
			return List.of(cluster);
		}
		
		int numParts = (int) Math.ceil(totalVolume / maxVolume);
		int averageClusterSize = (int) Math.round( (cluster.size() * 1.0) / (numParts * 1.0));

		List<List<IParcel>> partitions = new ArrayList<>(numParts);
		for (int i=0; i < numParts; i++) {
			partitions.add(new ArrayList<>(averageClusterSize)); //TODO: average size
		}
		
		int i = 0;
		for (IParcel p : cluster) {
			partitions.get(i % numParts).add(p);
			i++;
		}
		
		return partitions.stream()
						 .filter(c -> !c.isEmpty())
						 .collect(toList());
	};

	public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster);
	
}
