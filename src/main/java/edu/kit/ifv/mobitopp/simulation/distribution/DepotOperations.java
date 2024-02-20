package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.dispatch.DispatchStrategy;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.planning.TourPlanningStrategy;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;

@Getter
public class DepotOperations {

	protected final DistributionCenter center;
	
	private final TourPlanningStrategy tourStrategy;
	private final ParcelPolicyProvider policyProvider;
	protected final DispatchStrategy dispatchStrategy;
	
	protected final ParcelArrivalScheduler scheduler;
	
	private final ImpedanceIfc impedance;
	protected final DeliveryResults results;

	
	public DepotOperations(TourPlanningStrategy tourStrategy, ParcelPolicyProvider policyProvider, DispatchStrategy dispatchStrategy, DistributionCenter center, DeliveryResults results, ImpedanceIfc impedance) {
		this.center = center;
		
		this.tourStrategy = tourStrategy;
		this.policyProvider = policyProvider;
		this.dispatchStrategy = dispatchStrategy;
		this.impedance = impedance;
		this.results = results;
		
		this.scheduler = new ParcelArrivalScheduler(); //TODO singleton scheduler
		
		center.setOperations(this);
	}
	
	public void update(Time time) {
		scheduler.process(time);
		
		planTours(time);
		dispatchAvailableTours(time);
	}
	
	protected void planTours(Time currentTime) {
		
		if (tourStrategy.shouldReplanTours(center, currentTime)) { //TODO add replanning for non-hubs
			System.out.println("Replan tours!");
			
			DepotStorage storage = center.getStorage();

			for (PlannedTour t : plannedTours()) {
				if (t.isReplanningAllowed()) {
					storage.deletePlannedTour(t);
				}
			}

			storage.addPlannedTours(
				this.tourStrategy.planTours(center.getStorage().getParcels(), center.getStorage().getRequests(), center.getFleet(), currentTime)
			);
			
			
			System.out.println("	planned " + plannedTours().size() + " tours; " + center.getFleet().size() + " vehicles available!");
		}
		
		
	}



	protected void dispatchAvailableTours(Time currentTime) {
		
		Optional<PlannedTour> tourCheck = null;
		
		while ((tourCheck = canDispatch(currentTime)).isPresent()) {
			PlannedTour tour = tourCheck.get();
			Optional<DeliveryVehicle> vehicle = dispatchStrategy.getVehicleForTour(tour, center, currentTime);
			
			if (vehicle.isPresent()) {
				dispatchTour(currentTime, tour, vehicle.get());
			}
			
		}
		
	}

	protected void dispatchTour(Time currentTime, PlannedTour tour, DeliveryVehicle vehicle) {
		
		Time returnTime = tour.prepare(currentTime, vehicle, impedance);
		
		scheduler.dispatchVehicle(vehicle, returnTime);
		scheduler.dispatchParcelActivities(tour, currentTime);	
		center.getStorage().pickPlannedTour(tour);

	}

	protected Collection<PlannedTour> plannedTours() {
		return new ArrayList<>(center.getStorage().getPlannedTours());
	}

	
	protected Optional<PlannedTour> canDispatch(Time time) {
		return plannedTours().stream().filter(t -> this.dispatchStrategy.canDispatch(t, center, time)).findFirst();
	}

}
