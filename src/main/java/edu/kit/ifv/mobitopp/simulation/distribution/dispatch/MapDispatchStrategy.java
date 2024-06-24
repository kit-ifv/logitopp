package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MapDispatchStrategy implements DispatchStrategy {

    private final TimeTable timeTable;
    private final Map<PlannedTour, Time> tourDispatchTime = new LinkedHashMap<>();

    public MapDispatchStrategy(TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    private void recordTourDispatch(PlannedTour tour, DistributionCenter origin, Time currentTime) {
        Time dispatchTime = tour.latestDeparture().orElse(currentTime.startOfDay().plusHours(7));

        if (useTimeTable(tour, origin) && tour.isReturning()) {

            DistributionCenter destination = tour.nextHub().get();

            Optional<Connection> freeReturnConnection =
                    timeTable.getFreeConnectionsOnDay(origin, destination, currentTime).findFirst();

            if (freeReturnConnection.isPresent()) {
                Connection connection = freeReturnConnection.get();
                if (connection.getDeparture().isBefore(dispatchTime)) {
                    dispatchTime = connection.getDeparture();
                    //TODO book connection
                }

            }

        }

        tourDispatchTime.put(tour, dispatchTime);

    }

    private void removeTourDispatch(PlannedTour tour) {
        tourDispatchTime.remove(tour);
    }

    @Override
    public boolean canDispatch(PlannedTour tour, DistributionCenter origin, Time time) {
        if (!tourDispatchTime.containsKey(tour)) {
            recordTourDispatch(tour, origin, time);
        }

        return tourDispatchTime.get(tour).isBeforeOrEqualTo(time);
    }

    private boolean useTimeTable(PlannedTour tour, DistributionCenter origin) {

        Optional<DistributionCenter> handler = tour.isReturning() ? tour.nextHub() : Optional.of(origin);

        return handler.filter(distributionCenter -> distributionCenter.getVehicleType() == VehicleType.TRAM).isPresent();

    }

    @Override
    public Optional<DeliveryVehicle> getVehicleForTour(PlannedTour tour, DistributionCenter origin, Time time) {

/*		if (!tour.isReturning() && tour.latestDeparture().orElse(time).minusMinutes(1).isAfter(time)) {
			return Optional.empty();
		}*/

        DistributionCenter dc = (tour.isReturning()) ? tour.nextHub().get() : origin;

        VehicleType mode = dc.getVehicleType();

        Optional<DeliveryVehicle> vehicle;

        if (mode.equals(VehicleType.TRAM)) {

            Optional<Connection> connection = tour.usedConnection();
            if (connection.isPresent()) {

				/*if (connection.get().getDeparture().isAfter(time)) {
					return Optional.empty();
				}*/

                String tag = connection.get().getTag();
                vehicle = dc.getFleet().getVehicles().stream()
                        .filter(v -> Objects.equals(v.getTag(), tag))
                        .findFirst();

            } else {
                vehicle = Optional.of(new DeliveryVehicle(mode, 1, dc, 1));
            }

        } else {
            vehicle = dc.getFleet().getAvailableVehicle();
        }

        if (vehicle.isPresent()) {
            tourDispatchTime.remove(tour);
        }

        return vehicle;
    }
}
