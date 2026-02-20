package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BundleAwareDaySelector<A extends ParcelAgent, P extends ParcelBuilder<A>> extends CopyModelStep<A, P, Time> implements ParcelDemandModelStep<A, P, Time> {

    private final Map<DayOfWeek, Integer> totalByDay;
    private int totalCount = 0;
    private double currentError;
    private final Map<DayOfWeek, Double> expectedShare;

    public BundleAwareDaySelector(Map<DayOfWeek, Double> expectedShare) {
        this.expectedShare = expectedShare;

        totalByDay = new HashMap<>();
        for (DayOfWeek d : expectedShare.keySet()) {
            totalByDay.put(d, 0);
        }

        currentError = computeSquareError(0, DayOfWeek.MONDAY);
    }

    @Override
    public Time select(P parcel, Collection<P> otherParcels, int numOfParcels, double randomNumber) {
        Map<DayOfWeek, Double> weight = new HashMap<>();

        for (DayOfWeek d : expectedShare.keySet()) {
            double error = computeSquareError(numOfParcels, d);

            double improvement = Math.max(1e-6, currentError  - error);

            weight.put(d, improvement);
        }

        DayOfWeek selected = new DiscreteRandomVariable<DayOfWeek>(weight).realization(randomNumber);

        totalCount += numOfParcels;
        totalByDay.put(selected, totalByDay.get(selected) + numOfParcels);
        currentError = computeSquareError(0, selected);

        return SimpleTime.start.plusDays(selected.getTypeAsInt());
    }

    private double computeSquareError(int increment, DayOfWeek d) {

        double squareError = 0.0;
        for (DayOfWeek k : expectedShare.keySet()) {
            double actualShare;
            if (k == d) {
                if (totalCount + increment == 0) {
                    actualShare = 0.0;
                } else {
                    actualShare = (totalByDay.get(k).doubleValue() + increment) / (totalCount + increment * 1.0);
                }
            } else {
                if (totalCount == 0) {
                    actualShare = 0.0;
                } else {
                    actualShare = totalByDay.get(k).doubleValue() / (totalCount * 1.0);
                }
            }

            squareError += Math.pow(expectedShare.get(k) - actualShare, 2.0);
        }

        return squareError;
    }

}
