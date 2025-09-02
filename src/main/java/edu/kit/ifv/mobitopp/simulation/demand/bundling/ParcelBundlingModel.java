package edu.kit.ifv.mobitopp.simulation.demand.bundling;

import java.util.List;

public interface ParcelBundlingModel<A> {

    public List<Integer> selectBundling(A agent, int quantity, double randomNumber);

}
