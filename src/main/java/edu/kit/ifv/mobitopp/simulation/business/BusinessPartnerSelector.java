package edu.kit.ifv.mobitopp.simulation.business;

import java.util.Collection;
import java.util.LinkedHashMap;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;

public interface BusinessPartnerSelector {

	public Collection<DistributionCenter> select(Business business);

	public void printStatistics();

	public LinkedHashMap<DistributionCenter, Double> computeCurrentWeights();

}