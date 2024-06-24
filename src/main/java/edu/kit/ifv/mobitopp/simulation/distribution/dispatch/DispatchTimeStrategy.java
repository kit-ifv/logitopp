package edu.kit.ifv.mobitopp.simulation.distribution.dispatch;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.DeliveryVehicle;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.Fleet;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.PlannedTour;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.Objects;
import java.util.Optional;

import static edu.kit.ifv.mobitopp.time.DayOfWeek.SUNDAY;

public class DispatchTimeStrategy {

    private final TimeTable timeTable;

    public DispatchTimeStrategy(TimeTable timeTable) {
        this.timeTable = timeTable;
    }


    public Time dispatchTimeFor(PlannedTour tour, DistributionCenter origin, Time time) {

        DistributionCenter handler = (tour.isReturning()) ? tour.nextHub().get() : origin;

        VehicleType vehicle = handler.getVehicleType();

        switch (vehicle) {
            case BIKE:
            case TRUCK:
                return time.startOfDay().plusHours(7);

            case TRAM:
                return tramDispatchTime(tour, origin, time);

            case OTHER:
            default:
                return time;
        }

    }

    public boolean dispatchHours(PlannedTour tour, DistributionCenter origin, Time time) {
        DistributionCenter handler = (tour.isReturning()) ? tour.nextHub().get() : origin;

        VehicleType vehicle = handler.getVehicleType();

        switch (vehicle) {
            case BIKE:
            case TRUCK:
                return isInVehicleDispatchHours(time) && !isSunday(time) && endsBeforeEndOfDeliveryTime(time, tour);

            case TRAM:
                return isInTramDispatchHours(time) && endsBeforeEndOfDeliveryTime(time, tour);

            case OTHER:
            default:
                return true;
        }
    }


    public Optional<DeliveryVehicle> getVehicleForTour(PlannedTour tour, DistributionCenter origin, Time time) {
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

            return Optional.of(new DeliveryVehicle(mode, 1, dc, 1));
        }

        return dc.getFleet().getAvailableVehicle();
    }


    private Time tramDispatchTime(PlannedTour tour, DistributionCenter origin, Time time) {

        if (tour.isReturning()) {

            DistributionCenter destination = tour.nextHub().get();

            return timeTable.getFreeConnectionsOnDay(origin, destination, time).findFirst().map(
                    Connection::getDeparture
            ).orElse(time);

        } else {

            return tour.usedConnection().get().getDeparture();

        }


    }


    private boolean isInVehicleDispatchHours(Time time) {
        int hour=time.getHour();
        return 7 <= hour && hour <= 18;
    }

    private boolean isInTramDispatchHours(Time time) {
        int hour=time.getHour();
        return 4 <= hour && hour <= 18;
    }

    private boolean isSunday(Time time) {
        return time.weekDay().equals(SUNDAY);
    }

    private boolean isFleetAbsent(Fleet fleet) {
        return fleet.getAvailableVehicles().isEmpty();
    }

    private boolean fleetHasVehicles(DistributionCenter handler) {
        return handler.getFleet().getAvailableVehicles().size() > 0;
    }

    private boolean endsBeforeEndOfDeliveryTime(Time time, PlannedTour t) {
        return time.plus(t.getPlannedDuration()).isBefore(time.startOfDay().plusHours(23));
    }
}
