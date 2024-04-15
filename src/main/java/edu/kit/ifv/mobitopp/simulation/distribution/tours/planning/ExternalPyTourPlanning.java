package edu.kit.ifv.mobitopp.simulation.distribution.tours.planning;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.distribution.delivery.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.DeliveryDurationModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedDeliveryTour;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.DeliveryClusteringStrategy;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ExternalPyTourPlanning extends ClusterTourPlanningStrategy {

	private final ImpedanceIfc impedance;

	public ExternalPyTourPlanning(DeliveryClusteringStrategy clusteringStrategy, DeliveryDurationModel durationModel, ImpedanceIfc impedance) {
		super(clusteringStrategy, durationModel);
		this.impedance = impedance;
	}

	@Override
	protected List<PlannedTour> planTours(Collection<ParcelActivityBuilder> activities, DeliveryVehicle vehicle,
			Time time, RelativeTime duration) {
		
		//vehicles depart at 8 o'clock
		Time departure = time.startOfDay().plusHours(8);

		// collect travel times between all stops in matrix
		// call python script for planning
		// parse results of planned tours
		
		
		// for all planned tours
		//build tours from stops
		// compute planned duration with return time to vehicle.owner.zone
		RelativeTime plannedDuration = RelativeTime.ofMinutes(42);
		PlannedDeliveryTour tour = new PlannedDeliveryTour(vehicle.getType(), plannedDuration, departure, true, impedance, vehicle.getOwner());
		tour.addStops(null); //add stops of parsed tour by id
		
		
		return null;
	}

}
