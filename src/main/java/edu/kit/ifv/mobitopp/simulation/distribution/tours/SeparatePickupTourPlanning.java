package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SeparatePickupTourPlanning extends ClusterTourPlanningStrategy {
	
	private final ClusterTourPlanningStrategy delegate;

	public SeparatePickupTourPlanning(DeliveryClusteringStrategy clusteringStrategy,
			DeliveryDurationModel durationModel, ClusterTourPlanningStrategy delegate) {
		super(clusteringStrategy, durationModel);
		this.delegate = delegate;
	}

	@Override
	public List<PlannedDeliveryTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet,
			Time time) {
		
		DeliveryVehicle vehicle = fleet.getVehicles().iterator().next();
		List<PlannedDeliveryTour> tours = new ArrayList<>();
		
		System.out.print(vehicle.getOwner().getName() + " plans delivery: ");
		List<ParcelActivityBuilder> delActivities = getDeliveryActivities(deliveries, List.of());
		tours.addAll(
				delegate.planTours(delActivities, vehicle, time, RelativeTime.ofHours(6))
		);
		System.out.println(" -> " + tours.size());
		
		System.out.print(vehicle.getOwner().getName() + " plans pickup: ");
		List<ParcelActivityBuilder> pickActivities = getDeliveryActivities(List.of(), pickUps);
		tours.addAll(
				delegate.planTours(pickActivities, vehicle, time.startOfDay().plusHours(14), RelativeTime.ofHours(6))
		);
		System.out.println(" -> " + tours.size());
		
		return tours;
	}

	@Override
	protected List<PlannedDeliveryTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle,
			Time time, RelativeTime duration) {
		throw new UnsupportedOperationException("planTours(clusters) Should not be called on decorator " + this.getClass().getSimpleName());
	}

}
