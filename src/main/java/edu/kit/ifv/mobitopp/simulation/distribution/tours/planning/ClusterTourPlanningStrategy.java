package edu.kit.ifv.mobitopp.simulation.distribution.tours.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryDurationModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class ClusterTourPlanningStrategy implements TourPlanningStrategy {

	protected final DeliveryClusteringStrategy clusteringStrategy;
	protected final DeliveryDurationModel durationModel;
	
	public ClusterTourPlanningStrategy(DeliveryClusteringStrategy clusteringStrategy, DeliveryDurationModel durationModel) {
		this.clusteringStrategy = clusteringStrategy;
		this.durationModel = durationModel;
	}
	
	@Override
	public List<PlannedTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps,
			Fleet fleet, Time time) {
		
		List<ParcelActivityBuilder> activities = getDeliveryActivities(deliveries, pickUps, fleet.getVehicleParcelCount());
		DeliveryVehicle vehicle = fleet.getVehicles().iterator().next();
		
		return planTours(activities, vehicle, time, RelativeTime.ofHours(8));
	}

	protected abstract List<PlannedTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle, Time time, RelativeTime duration);

	@Override
	public boolean shouldReplanTours(DistributionCenter center, Time time) {
		return time.equals(time.startOfDay().plusHours(6));
	}

	
	protected List<ParcelActivityBuilder> getDeliveryActivities(Collection<IParcel> deliveries, Collection<IParcel> pickUps, int parcelCount) {
		List<ParcelActivityBuilder> activities = new ArrayList<>();
	
		List<IParcel> available = new ArrayList<>(deliveries);
		available.addAll(pickUps);
		
		if (available.isEmpty()) {
			return activities;
		}

		clusteringStrategy.cluster(available, parcelCount)
						  .stream()
						  .map(cluster -> new ParcelActivityBuilder(cluster.getParcels(), cluster.getZoneAndLocation()))
						  .map(a -> a.withDuration(durationModel))
						  .forEach(activities::add);
	
		return activities;
	}
	
}
