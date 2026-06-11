package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Optional;

public class SeparatePickupDispatchTimeStrategy implements IDispatchTimeStrategy {

    private final IDispatchTimeStrategy delegate;

    public SeparatePickupDispatchTimeStrategy(IDispatchTimeStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public Time dispatchTimeFor(PlannedTour tour, DistributionCenter origin, Time time) {

        if (hasPickups(tour) && !tour.isReturning()) {
            Time earliestDispatch = todayAt14h(time);
            Time delegateTime = delegate.dispatchTimeFor(tour, origin, time);

            if (delegateTime.isAfterOrEqualTo(earliestDispatch)) {
                return delegateTime;
            } else {
                return earliestDispatch;
            }
        }

        return delegate.dispatchTimeFor(tour, origin, time);
    }

    @Override
    public boolean dispatchHours(PlannedTour tour, DistributionCenter origin, Time time) {
        if (hasPickups(tour) && !tour.isReturning()) {
            return time.getHour() >= 14;
        } else {
            return delegate.dispatchHours(tour, origin, time);
        }
    }

    @Override
    public Optional<DeliveryVehicle> getVehicleForTour(PlannedTour tour, DistributionCenter origin, Time time) {
        return delegate.getVehicleForTour(tour, origin, time);
    }

    private boolean hasPickups(PlannedTour tour) {
        return !tour.getPickUpRequests().isEmpty();
    }

    private Time todayAt14h(Time time) {
        return time.startOfDay().plusHours(14);
    }

}
