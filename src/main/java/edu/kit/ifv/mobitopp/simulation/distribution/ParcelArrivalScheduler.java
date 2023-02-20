package edu.kit.ifv.mobitopp.simulation.distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivity;
import edu.kit.ifv.mobitopp.simulation.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.time.Time;

public class ParcelArrivalScheduler implements Hook {
	
	private final DistributionCenter operator;
	private final Map<Time, List<DeliveryVehicle>> vehicleReturnTimes;
	private final Map<Time, List<ParcelActivity>> arrivalTimes;

	private Time lastProcessed;
	
	public ParcelArrivalScheduler(DistributionCenter operator) {
		this.operator = operator;
		this.vehicleReturnTimes = new HashMap<>();
		this.arrivalTimes = new HashMap<>();
		this.lastProcessed = Time.start;
	}
		
	@Override
	public void process(Time date) {
		Set<Time> currentReturns = getTimeBefore(vehicleReturnTimes, date);
		Set<Time> currentArrivals = getTimeBefore(arrivalTimes, date);
		
		currentReturns.stream()
					  .map(vehicleReturnTimes::get)
					  .flatMap(x -> x.stream())
					  .forEach(v -> v.unloadAndReturn(date));
		
		currentArrivals.stream()
					   .map(arrivalTimes::get)
					   .flatMap(x -> x.stream())
					   .forEach(a -> a.executeActivity(date));

		currentReturns.forEach(vehicleReturnTimes::remove);
		currentArrivals.forEach(arrivalTimes::remove);
		
		this.lastProcessed = Time.start.plus(date.fromStart());
	}
	
	public void dispatchVehicle(DeliveryVehicle vehicle, Time returnTime) {
		if (returnTime.isBefore(lastProcessed)) {
			System.err.println("Cannot dispatch vehicle due to return in the past: est. return " + returnTime + ", simulation time " + lastProcessed);
		}
		
		vehicleReturnTimes.merge(returnTime, new ArrayList<>(List.of(vehicle)), this::mergeIntoFirst);
		
		operator.bookVehicleUntil(vehicle, returnTime);		
	}
	
	public void dispatchParcelActivities(Collection<ParcelActivity> activities, Time currentTime) {
		activities.forEach(a -> dispatchParcelActivity(a, currentTime));
	}
	
	public void dispatchParcelActivity(ParcelActivity activity, Time currentTime) {
		if (activity.getPlannedTime().isBefore(lastProcessed)) {
			System.err.println("Cannot dispatch activity in the past: est. arrival time " + activity.getPlannedTime() + ", simulation time " + lastProcessed);
		}
		
		arrivalTimes.merge(activity.getPlannedTime(), new ArrayList<>(List.of(activity)), this::mergeIntoFirst);
		activity.prepareAvtivity(currentTime); //TODO in prepare -> remove parcels and requests from operator 
	}
	
	private <T> List<T> mergeIntoFirst(List<T> l1, List<T> l2) {
		l1.addAll(l2);
		return l1;
	}
	
	private <T> Set<Time> getTimeBefore(Map<Time, T> map, Time date) {
		return map.keySet()
				  .stream()
				  .filter(t -> t.isBeforeOrEqualTo(date))
				  .collect(Collectors.toSet());
	}

}
