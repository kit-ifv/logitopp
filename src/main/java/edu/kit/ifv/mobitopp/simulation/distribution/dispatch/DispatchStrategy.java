package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import java.util.Objects;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

public interface DispatchStrategy {

	public boolean canDispatch(PlannedTour tour, DistributionCenter origin, Time time);
	
	default public Optional<DeliveryVehicle> getVehicleForTour(PlannedTour tour, DistributionCenter origin, Time time) {
		DistributionCenter dc = (tour.isReturning()) ? tour.nextHub().get() : origin;
		
		VehicleType mode = dc.getVehicleType();

		if (mode.equals(VehicleType.TRAM)) {

			Optional<Connection> connection = tour.usedConnection();
			if (connection.isPresent()) {

				String tag = connection.get().getTag();
				return dc.getFleet().getVehicles().stream()
						.filter(v -> Objects.equals(v.getTag(), tag))
						.findFirst();
			}

			return Optional.of(new DeliveryVehicle(mode, 1, dc));
		}	
		
		return dc.getFleet().getAvailableVehicle();
	}
	
}
