package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.region.RegionalReach;

public class TransportChainFactory {
	
	public static Collection<TransportChain> buildDeliveryChains(DistributionCenter hub) {
		return buildChains(hub, List.of(), TransportChain::inDeliveryDirection, RegionalReach::getRelatedDeliveryHubs)
				.stream()
				.filter(c -> c.last().getOrganization().equals(hub.getOrganization()) || c.last().getOrganization().equals("ALL")).collect(Collectors.toList());
	}
	
	public static Collection<TransportChain> buildPickUpChains(DistributionCenter hub) {
		return buildChains(hub, List.of(), TransportChain::inPickUpDirection, RegionalReach::getRelatedPickUpHubs)
				.stream()
				.filter(c -> c.first().getOrganization().equals(hub.getOrganization()) || c.first().getOrganization().equals("ALL")).collect(Collectors.toList());
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
				.filter(c -> !previous.contains(c))
				.flatMap(c -> buildChains(c, sequence, builder, childMap).stream())
				.forEach(chains::add);
		
		return chains;
	}

}
