package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.region.RegionalReach;

public class TransportChainFactory {
	
	public static Collection<TransportChain> buildDeliveryChains(DistributionCenter hub) {
		return buildChains(hub, List.of(), TransportChain::inDeliveryDirection, RegionalReach::getRelatedDeliveryHubs);
	}
	
	public static Collection<TransportChain> buildPickUpChains(DistributionCenter hub) {
		return buildChains(hub, List.of(), TransportChain::inPickUpDirection, RegionalReach::getRelatedPickUpHubs);
	}
	
	private static Collection<TransportChain> buildChains(DistributionCenter hub, 
																 List<DistributionCenter> previous,
																 Function<List<DistributionCenter>, TransportChain> builder,
																 Function<RegionalReach, Collection<DistributionCenter>> childMap) {
		
		Collection<TransportChain> chains = new ArrayList<>();
		List<DistributionCenter> sequence = new ArrayList<>(previous);
		sequence.add(hub);
		
		if (hub.getRegionalStructure().getServiceArea().exists()) {
			chains.add(builder.apply(sequence));
		}
		
		childMap.apply(hub.getRegionalStructure())
				.stream()
				.flatMap(c -> buildChains(c, sequence, builder, childMap).stream())
				.forEach(chains::add);
		
		return chains;
	}

}
