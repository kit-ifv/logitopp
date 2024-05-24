package edu.kit.ifv.mobitopp.util.routing.tsp;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;
import edu.kit.ifv.mobitopp.util.routing.ModeTravelTimes;
import edu.kit.ifv.mobitopp.util.routing.Tour;
import edu.kit.ifv.mobitopp.util.routing.TravelTimeProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AggregatedTspSolver<E, A> implements TspSolver<E> {

    private final TspSolver<A> aggregateSolver;
    private final TspSolver<E> detailSolver;
    private final Function<E, A> aggregation;

    public AggregatedTspSolver(TspSolver<A> aggregateSolver, TravelTimeProvider<E> travelTime, Function<E, A> aggregation, Function<E, Float> stopDurationModel) {
        ModeTravelTimes<E> travelTimes = new ModeTravelTimes<>(
                () -> travelTime,
                stopDurationModel);

        this.aggregateSolver = aggregateSolver;
        this.detailSolver = new NullTspSolver<>(travelTimes);
        this.aggregation = aggregation;
    }

    public AggregatedTspSolver(TspSolver<A> aggregateSolver, TspSolver<E> detailSolver, Function<E, A> aggregation) {
        this.aggregateSolver = aggregateSolver;
        this.detailSolver = detailSolver;
        this.aggregation = aggregation;
    }


    @Override
    public Tour<E> findTour(Collection<E> elements, StandardMode mode) {

        //aggregate
        Collection<List<E>> groups = CollectionsUtil.groupBy(
                new ArrayList<>(elements),
                (e1, e2) -> aggregation.apply(e1) == aggregation.apply(e2)
        );

        Map<A, List<E>> aggregateMap = groups.stream()
                .filter(l -> !l.isEmpty())
                .collect(Collectors.toMap(
                        l -> aggregation.apply(l.get(0)),
                        l -> l
                ));

        List<E> result = new ArrayList<>();
        Tour<A> macroOrder = aggregateSolver.findTour(aggregateMap.keySet(), mode);
        float duration = 0;

        for (A aggregate: macroOrder.getElements()) {
            List<E> subElements = aggregateMap.get(aggregate);
            Tour<E> subTour = detailSolver.findTour(subElements, mode);

            duration += subTour.getTravelTime();
            if (!result.isEmpty()) {
                E last = result.get(result.size() - 1);
                E next = subTour.getElements().get(0);
                duration += getTravelTimes().getTravelTime(mode, last, next);
            }
            result.addAll(subTour.getElements());
        }

        return new Tour<E>(result, duration, getTravelTimes(), mode);
    }

    @Override
    public ModeTravelTimes<E> getTravelTimes() {
        return detailSolver.getTravelTimes();
    }
}
