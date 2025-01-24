package edu.kit.ifv.mobitopp.simulation.demand.bundling;

import java.util.Collections;
import java.util.List;

public class NoBundlingModel<A> implements ParcelBundlingModel<A> {
    @Override
    public List<Integer> selectBundling(A agent, int quantity, double randomNumber) {
        return Collections.nCopies(quantity, 1);
    }
}
