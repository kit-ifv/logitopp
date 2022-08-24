package edu.kit.ifv.mobitopp.simulation.business;

import java.util.Collection;
import java.util.LinkedHashMap;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;

public interface BusinessPartnerSelector {

	Collection<DistributionCenter> select(Business business);

	void printStatistics();

	LinkedHashMap<DistributionCenter, Double> computeCurrentWeights();

}