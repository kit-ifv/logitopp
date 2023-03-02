package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.dispatch.DispatchStrategy;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.TourPlanningStrategy;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class DepotOperations {

	private final DistributionCenter center;
	
	private final TourPlanningStrategy tourStrategy;
	private final ParcelPolicyProvider policyProvider;
	private final DispatchStrategy dispatchStrategy;
	
	private final ParcelArrivalScheduler scheduler;
	
	private final ImpedanceIfc impedance;
	private final DeliveryResults results;
	
//	@Setter	private DeliveryClusteringStrategy clusteringStrategy; //TODO move to tour planning strategy
//	@Setter private DeliveryDurationModel durationModel; //TODO move to tour planning model??
	
	public DepotOperations(TourPlanningStrategy tourStrategy, ParcelPolicyProvider policyProvider, DispatchStrategy dispatchStrategy, DistributionCenter center, DeliveryResults results, ImpedanceIfc impedance) {
		this.center = center;
		this.tourStrategy = tourStrategy;
		this.policyProvider = policyProvider;
		this.dispatchStrategy = dispatchStrategy;
		this.impedance = impedance;
		this.results = results;
		
		this.scheduler = new ParcelArrivalScheduler(center);
	}
	
	public void update(Time time) {
		scheduler.process(time);
		
		planTours(time);
		dispatchAvailableTours(time);
	}
	
	private void planTours(Time currentTime) {
		
		if (tourStrategy.shouldReplanTours(center, currentTime)) { //TODO add replanning for non-hubs
			
			Collection<PlannedDeliveryTour> plannedTours = plannedTours();
			
			plannedTours.clear();//TODO seperate list of planned and unplanned parcels? also filter tours that should not be thrown away
					

			plannedTours.addAll(
				this.tourStrategy.planTours(center.getStorage().getCurrentParcels(), center.getStorage().getPickupRequests(), center.getFleet())
			);
			
			System.out.println("	planned " + plannedTours.size() + " tours; " + center.getFleet().size() + " vehicles available!");

		}
		
		// TODO Auto-generated method stub
		// TODO check if it is time to (re)?plan delivery tours
		// TODO group deliveries and pickups to parcelActivities
		// TODO plan tours for parcel activities
		// TODO store planned tours
	}



	private void dispatchAvailableTours(Time currentTime) {
		
		Optional<PlannedDeliveryTour> tour = null;
		
		while ((tour = canDispatch(currentTime)).isPresent()) {
			
			Optional<DeliveryVehicle> vehicle = center.getFleet().getAvailableVehicle();
			
			if (vehicle.isPresent()) {
				tour.get().dispatchTour(currentTime, vehicle.get(), impedance);
				
				int parcels = tour.get().getStops().stream().mapToInt(s -> s.getDeliveries().size()).sum();
				int pickUps = tour.get().getStops().stream().mapToInt(s -> s.getPickUps().size()).sum();
				results.logLoadEvent(vehicle.get(), currentTime, parcels, pickUps, center.getZoneAndLocation());
			}
			
			
			plannedTours().remove(tour.get());
			
			dispatchAvailableTours(currentTime);
		}
		
	}

	private Collection<PlannedDeliveryTour> plannedTours() {
		return center.getStorage().getPlannedTours();
	}

	
	private Optional<PlannedDeliveryTour> canDispatch(Time time) {
		return this.dispatchStrategy.canDispatch(plannedTours(), center.getFleet(), time);
	}

}
