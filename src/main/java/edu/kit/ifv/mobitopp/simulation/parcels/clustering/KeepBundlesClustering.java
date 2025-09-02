package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class KeepBundlesClustering implements DeliveryClusteringStrategy {

    private final DeliveryClusteringStrategy delegate;

    public KeepBundlesClustering(DeliveryClusteringStrategy delegate) {
        this.delegate = delegate;
    }


    @Override
    public Collection<ParcelCluster> cluster(List<IParcel> parcels, int maxCount) {
        return CollectionsUtil.groupBy(parcels, this::canBeGrouped)
                .stream()
                .flatMap(cluster -> partitionKeepingBundles(cluster, maxCount).stream())
                .map(cluster -> new ParcelCluster(cluster, this))
                .collect(toList());
    }

    private static List<List<IParcel>> partitionKeepingBundles(List<IParcel> cluster, int maxCount) {
        if (cluster == null || cluster.isEmpty()) return List.of();
        if (maxCount <= 0) throw new IllegalArgumentException("maxCount must be > 0 for partitioning parcel clusters!");

        // Group by bundleId
        Map<Integer, List<IParcel>> byBundle = cluster.stream().collect(groupingBy(IParcel::getBundleId));

        // Sort bundles by size descending to pack efficiently
        List<List<IParcel>> bundles = new ArrayList<>(byBundle.values());
        bundles.sort((a, b) -> Integer.compare(b.size(), a.size()));


        List<List<IParcel>> partitions = new ArrayList<>();
        List<Integer> remaining = new ArrayList<>(); // remaining capacity per partition

        for (List<IParcel> bundle : bundles) {
            int size = bundle.size();

            if (size > maxCount) {
                // Oversized bundle: warn and place it alone
                System.out.println(
                        "Warning: bundle size " + size + " exceeds maxCount " + maxCount + ".\n" +
                                "Parcel ids: " + bundle.stream().map(p -> p.getOId() + "").collect(Collectors.joining(", ")) + "\n" +
                        "It is now split into separate bundles!"
                );

                List<IParcel> rest;
                do {
                    List<IParcel> full = bundle.stream().limit(maxCount).collect(Collectors.toList());
                    rest = bundle.stream().filter(p -> !full.contains(p)).collect(Collectors.toList());

                    partitions.add(full);
                    remaining.add(0); // no remaining capacity enforced here

                    bundle = rest;
                } while (rest.size() > maxCount);
            }

            // First-fit: place into first partition with enough remaining capacity
            boolean placed = false;
            for (int i = 0; i < partitions.size(); i++) {
                if (remaining.get(i) >= size) {
                    partitions.get(i).addAll(bundle);
                    remaining.set(i, remaining.get(i) - size);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                // Start a new partition
                List<IParcel> part = new ArrayList<>(Math.min(maxCount, Math.max(size, 8)));
                part.addAll(bundle);
                partitions.add(part);
                remaining.add(maxCount - size);
            }
        }

        return partitions.stream()
                        .filter(c -> !c.isEmpty())
                        .collect(toList());
    }


    @Override
    public boolean canBeGrouped(IParcel a, IParcel b) {
        return a.getBundleId() == b.getBundleId() || delegate.canBeGrouped(a, b);
    }

    @Override
    public ZoneAndLocation getStopLocation(List<IParcel> deliveryCluster) {
        return delegate.getStopLocation(deliveryCluster);
    }

}
