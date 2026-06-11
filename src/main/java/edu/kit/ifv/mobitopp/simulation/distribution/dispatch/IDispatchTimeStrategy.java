package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Optional;

public interface IDispatchTimeStrategy {
    public Time dispatchTimeFor(PlannedTour tour, DistributionCenter origin, Time time);
    public boolean dispatchHours(PlannedTour tour, DistributionCenter origin, Time time);
    public Optional<DeliveryVehicle> getVehicleForTour(PlannedTour tour, DistributionCenter origin, Time time);
}
