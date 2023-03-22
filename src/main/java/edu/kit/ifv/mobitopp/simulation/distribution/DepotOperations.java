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

	
	public DepotOperations(TourPlanningStrategy tourStrategy, ParcelPolicyProvider policyProvider, DispatchStrategy dispatchStrategy, DistributionCenter center, DeliveryResults results, ImpedanceIfc impedance) {
		this.center = center;
		
		this.tourStrategy = tourStrategy;
		this.policyProvider = policyProvider;
		this.dispatchStrategy = dispatchStrategy;
		this.impedance = impedance;
		this.results = results;
		
		this.scheduler = new ParcelArrivalScheduler(center);
		
		center.setOperations(this);
	}
	
	public void update(Time time) {
		scheduler.process(time);
		
		planTours(time);
		dispatchAvailableTours(time);
	}
	
	private void planTours(Time currentTime) {
		
		if (tourStrategy.shouldReplanTours(center, currentTime)) { //TODO add replanning for non-hubs
			
			
			DepotStorage storage = center.getStorage();
			
			plannedTours().stream().filter(t -> t != null).filter(PlannedDeliveryTour::isReplan).forEach(storage::deletePlannedTour);					

			storage.addPlannedTours(
				this.tourStrategy.planTours(center.getStorage().getParcels(), center.getStorage().getRequests(), center.getFleet(), currentTime)
			);
			
			System.out.println("	planned " + plannedTours().size() + " tours; " + center.getFleet().size() + " vehicles available!");

		}
		
		// TODO Auto-generated method stub
		// TODO check if it is time to (re)?plan delivery tours
		// TODO group deliveries and pickups to parcelActivities
		// TODO plan tours for parcel activities
		// TODO store planned tours
	}



	private void dispatchAvailableTours(Time currentTime) {
		
		Optional<PlannedDeliveryTour> tourCheck = null;
		
		while ((tourCheck = canDispatch(currentTime)).isPresent()) {
			
			Optional<DeliveryVehicle> vehicleCheck = center.getFleet().getAvailableVehicle();
			
			PlannedDeliveryTour tour = tourCheck.get();

			if (vehicleCheck.isPresent()) {
				
				DeliveryVehicle vehicle = vehicleCheck.get();
				
				Time returnTime = tour.prepare(currentTime, vehicle, impedance);
				
				scheduler.dispatchVehicle(vehicle, returnTime);
				scheduler.dispatchParcelActivities(tour, currentTime);	
				
			}
			
			
			center.getStorage().pickPlannedTour(tour);
			
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
