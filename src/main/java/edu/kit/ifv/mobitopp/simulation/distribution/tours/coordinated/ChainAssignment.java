package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;

public class ChainAssignment {
	private final Map<DistributionCenter, List<TimedTransportChain>> chains;
	private final Map<DistributionCenter, List<TransportPreferences>> preferences;
	
	public ChainAssignment(Collection<DistributionCenter> distributionCenters) {
		this.chains = new LinkedHashMap<>();
		this.preferences = new LinkedHashMap<>();
		
		distributionCenters.forEach(dc -> {
			chains.put(dc, new ArrayList<>());
			preferences.put(dc, new ArrayList<>());
		});
	}
	
	public void assign(DistributionCenter dc, List<TimedTransportChain> chains) {
		this.chains.get(dc).addAll(chains);
	}
	
	public void assignAll(Map<DistributionCenter, List<TimedTransportChain>> chainAssignment) {
		chains.forEach((dc, chains) -> this.assign(dc, chains));
	}
	
	public void regiter(DistributionCenter dc, List<TransportPreferences> preferences) {
		this.preferences.get(dc).addAll(preferences);
	}
	
	public List<TransportPreferences> getPreferences(DistributionCenter dc) {
		return this.preferences.get(dc);
	}
	
	public List<TimedTransportChain> getChains(DistributionCenter dc) {
		return this.chains.get(dc);
	}

}
