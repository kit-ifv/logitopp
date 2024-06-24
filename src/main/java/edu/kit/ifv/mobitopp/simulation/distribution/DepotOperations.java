package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.dispatch.DispatchStrategy;
import edu.kit.ifv.mobitopp.simulation.distribution.dispatch.DispatchTimeStrategy;
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
//	protected final DispatchStrategy dispatchStrategy;
	protected final DispatchTimeStrategy dispatchTimeStrategy;
	
	protected final ParcelArrivalScheduler scheduler;
	
	private final ImpedanceIfc impedance;
	protected final DeliveryResults results;

	
	public DepotOperations(TourPlanningStrategy tourStrategy, ParcelPolicyProvider policyProvider, /*DispatchStrategy dispatchStrategy,*/ DistributionCenter center, DispatchTimeStrategy dispatchTimeStrategy, DeliveryResults results, ImpedanceIfc impedance) {
		this.center = center;
		
		this.tourStrategy = tourStrategy;
		this.policyProvider = policyProvider;
//		this.dispatchStrategy = dispatchStrategy;
		this.dispatchTimeStrategy = dispatchTimeStrategy;
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
				} else {

					if (!center.getName().contains("HUB")) {
						System.out.println(center + " found and deleted planned tran tour from previous day?:\n    " + t);
						storage.deletePlannedTour(t);
					}


				}
			}

			storage.addPlannedTours(
				this.tourStrategy.planTours(center.getStorage().getParcels(), center.getStorage().getRequests(), center.getFleet(), currentTime)
			);
			
			
			System.out.println("	planned " + plannedTours().size() + " tours; " + center.getFleet().size() + " vehicles available!");

			dispatchTimes.clear();
			plannedDispatch.clear();

		}


		
	}



	protected Map<Time, List<PlannedTour>> dispatchTimes = new LinkedHashMap<>();
	protected Set<PlannedTour> plannedDispatch = new LinkedHashSet<>();


	protected void dispatchAvailableTours(Time currentTime) {
		Collection<PlannedTour> plannedTours = plannedTours();
		if(plannedTours.size() > 0) {
			System.out.println(center.getName() + ": " + plannedTours.size());
		}

		for (PlannedTour tour: plannedTours) {
			if (!plannedDispatch.contains(tour)) {

				Time dispatch = dispatchTimeStrategy.dispatchTimeFor(tour, center, currentTime);
				dispatch = (dispatch.isBeforeOrEqualTo(currentTime)) ? Time.start : dispatch;

				if (!dispatchTimes.containsKey(dispatch)) {
					dispatchTimes.put(dispatch, new ArrayList<>());
				}

				dispatchTimes.get(dispatch).add(tour);
				plannedDispatch.add(tour);

			}
		}

		List<Time> past = dispatchTimes.keySet().stream().filter(t -> t.isBeforeOrEqualTo(currentTime)).collect(Collectors.toList());
		for (Time dispatch: past) {

			List<PlannedTour> tours = dispatchTimes.get(dispatch);
			List<PlannedTour> dispatched = new ArrayList<>();

			for (PlannedTour tour: tours) {
				if (dispatchTimeStrategy.dispatchHours(tour, center, currentTime)) {
					Optional<DeliveryVehicle> vehicle = dispatchTimeStrategy.getVehicleForTour(tour, center, currentTime);
					if (vehicle.isPresent()) {
						System.out.println(this.center.getName() + "  dispatches " + tour);
						dispatchTour(currentTime, tour, vehicle.get());
						dispatched.add(tour);
					}
				}
			}

			tours.removeAll(dispatched);
			if (tours.isEmpty()) {
				dispatchTimes.remove(dispatch);
			}

		}


//		Collection<PlannedTour> plannedTours = plannedTours();

//		if(plannedTours.size() > 0) {
//			System.out.println(center.getName() + ": " + plannedTours.size());
//		}

//		for (PlannedTour tour : plannedTours) {
////			if (dispatchStrategy.canDispatch(tour, center, currentTime)) {
//
//			if (tour.latestDeparture().orElse(currentTime.startOfDay().plusHours(7)).isBeforeOrEqualTo(currentTime)) {
//
//				Optional<DeliveryVehicle> vehicle = dispatchStrategy.getVehicleForTour(tour, center, currentTime);
//				if (vehicle.isPresent()) {
//					System.out.println(this.center.getName() + "  dispatches " + tour);
//					dispatchTour(currentTime, tour, vehicle.get());
//				}
//
//			}
//		}

	}




	protected void dispatchTour(Time currentTime, PlannedTour tour, DeliveryVehicle vehicle) {
		
		Time returnTime = tour.prepare(currentTime, vehicle, impedance);

		results.logPlannedTour(center, vehicle, tour, currentTime);
		
		scheduler.dispatchVehicle(vehicle, returnTime, tour);
		scheduler.dispatchParcelActivities(tour, currentTime);	
		center.getStorage().pickPlannedTour(tour);

	}

	protected Collection<PlannedTour> plannedTours() {
		return new ArrayList<>(center.getStorage().getPlannedTours());
	}

	
//	protected Optional<PlannedTour> canDispatch(Time time) {
//		return plannedTours().stream().filter(t -> this.dispatchStrategy.canDispatch(t, center, time)).findFirst();
//	}

}
