package edu.kit.ifv.mobitopp.simulation.business;

import java.util.Collection;
import java.util.LinkedHashMap;

import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;

public interface BusinessPartnerSelector {

	public Collection<CEPServiceProvider> select(Business business);

	public void printStatistics();

	public LinkedHashMap<CEPServiceProvider, Double> computeCurrentWeights();

}