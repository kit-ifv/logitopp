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

            case TRUCK: return timeTruck(tour, time);
            case BIKE: return timeBike(time);

            case TRAM:
                return tramDispatchTime(tour, origin, time);

            case OTHER:
            default:
                return time;
        }

    }

    private Time timeTruck(PlannedTour tour, Time time) {
        if (tour.isReturning()) { return time; }

        return time.startOfDay().plusHours(7);
    }

    private Time timeBike(Time time) {
        return time.startOfDay().plusHours(7);
    }

    public boolean dispatchHours(PlannedTour tour, DistributionCenter origin, Time time) {
        DistributionCenter handler = (tour.isReturning()) ? tour.nextHub().get() : origin;

        VehicleType vehicle = handler.getVehicleType();

        switch (vehicle) {
            case TRUCK: return hoursTruck(tour, time);
            case BIKE: return hoursBike(tour, time);

            case TRAM:
                return true;//isInTramDispatchHours(time); //&& endsBeforeEndOfDeliveryTime(time, tour);

            case OTHER:
            default:
                return true;
        }
    }

    private boolean hoursTruck(PlannedTour tour, Time time) {
        if (tour.isReturning()) { return true; }

        return isInTruckDispatchHours(time) && !isSunday(time);// && endsBeforeEndOfDeliveryTime(time, tour);
    }

    private boolean hoursBike(PlannedTour tour, Time time) {
        return isInBikeDispatchHours(time) && !isSunday(time);// && endsBeforeEndOfDeliveryTime(time, tour);
    }


    public Optional<DeliveryVehicle> getVehicleForTour(PlannedTour tour, DistributionCenter origin, Time time) {
        DistributionCenter dc = (tour.isReturning()) ? tour.nextHub().get() : origin;
        VehicleType mode = dc.getVehicleType();

        if (mode.equals(VehicleType.TRAM)) {

            Optional<Connection> connection = tour.usedConnection();


            DeliveryVehicle veh = connection.flatMap(c ->
                    dc.getFleet().getVehicles().stream()
                            .filter(v -> Objects.equals(v.getTag(), c.getTag()))
                            .findFirst()
            ).orElse(
                    new DeliveryVehicle(mode, 1, dc, 2)
            );

            return Optional.of(veh);
        }

        if (tour.isReturning()) {
            return Optional.of(
                    new DeliveryVehicle(mode, dc.getFleet().getVehicleVolume(), dc, dc.getFleet().getVehicleParcelCount())
            );
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


    private boolean isInBikeDispatchHours(Time time) {
        int hour=time.getHour();
        return 7 <= hour && hour <= 20;
    }

    private boolean isInTruckDispatchHours(Time time) {
        int hour=time.getHour();
        return 7 <= hour && hour <= 19;
    }

/*    private boolean isInTramDispatchHours(PlannedTour tour, Time time) {
        int hour=time.getHour();
        return hour <= ;
    }*/

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
