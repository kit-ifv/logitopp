package edu.kit.ifv.mobitopp.util.routing.tsp;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.routing.ModeTravelTimes;
import edu.kit.ifv.mobitopp.util.routing.Tour;

import java.util.Collection;

public interface TspSolver<E> {

    public Tour<E> findTour(Collection<E> elements, StandardMode mode);

    public ModeTravelTimes<E> getTravelTimes();

}
