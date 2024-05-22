package edu.kit.ifv.mobitopp.util.routing.tsp;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.routing.ModeTravelTimes;
import edu.kit.ifv.mobitopp.util.routing.Tour;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

public class NullTspSolver<E> implements TspSolver<E> {

    @Getter
    private final ModeTravelTimes<E> travelTimes;

    public NullTspSolver(ModeTravelTimes<E> travelTimes) {
        this.travelTimes = travelTimes;
    }


    @Override
    public Tour<E> findTour(Collection<E> elements, StandardMode mode) {
        ArrayList<E> list = new ArrayList<>(elements);
        float duration = 0;

        if (!elements.isEmpty()) {
            E from = list.get(0);
            for(E to: list.subList(1, list.size())) {
                duration += travelTimes.getTravelTime(mode, from, to);
                from = to;
            }
            E start = list.get(0);
            duration += travelTimes.getTravelTime(mode, from, start); // access/egress duration
        }




        return new Tour<>(list, duration, travelTimes, mode);
    }


}
