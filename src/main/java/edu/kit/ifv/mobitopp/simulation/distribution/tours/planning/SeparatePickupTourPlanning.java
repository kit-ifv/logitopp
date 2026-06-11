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

public class SeparatePickupTourPlanning implements TourPlanningStrategy {
	
	private final TourPlanningStrategy delegate;

	public SeparatePickupTourPlanning(TourPlanningStrategy delegate) {
		this.delegate = delegate;
	}

	@Override
	public List<PlannedTour> planTours(Collection<IParcel> deliveries, Collection<IParcel> pickUps, Fleet fleet,
			Time time) {
		
		DeliveryVehicle vehicle = fleet.getVehicles().iterator().next();

        System.out.print(vehicle.getOwner().getName() + " plans delivery: ");
        List<PlannedTour> tours = new ArrayList<>(delegate.planTours(deliveries, List.of(), fleet, time));
		System.out.println(" -> " + tours.size());
		
		System.out.print(vehicle.getOwner().getName() + " plans pickup: ");
		tours.addAll(delegate.planTours(List.of(), pickUps, fleet, time));
		System.out.println(" -> " + tours.size());
		
		return tours;
	}

	@Override
	public boolean shouldReplanTours(DistributionCenter center, Time time) {
		return delegate.shouldReplanTours(center, time);
	}

}
