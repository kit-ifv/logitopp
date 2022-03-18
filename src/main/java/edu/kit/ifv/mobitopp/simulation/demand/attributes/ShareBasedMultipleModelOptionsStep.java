package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class ShareBasedMultipleModelOptionsStep<A extends ParcelAgent, P extends ParcelBuilder<A>, T> extends MultipleModelOptionsStep<A, P, T> {

	private final Map<ParcelDemandModelStep<A, P, T>, Double> probabilities;
	
	public ShareBasedMultipleModelOptionsStep(Map<ParcelDemandModelStep<A, P, T>, Double> choiceSetShares) {
		super(choiceSetShares.keySet());
		
		double sumOfShares = choiceSetShares.values().stream().mapToDouble(Double::doubleValue).sum();
		
		this.probabilities = choiceSetShares
				.entrySet()
				.stream()
				.collect(toLinkedMap(Map.Entry::getKey, e -> e.getValue() / sumOfShares));		
	}
	
	@Override
	protected ParcelDemandModelStep<A, P, T> selectModelStep(Collection<ParcelDemandModelStep<A, P, T>> steps, double randomNumber) {
		DiscreteRandomVariable<ParcelDemandModelStep<A, P, T>> distribution = new DiscreteRandomVariable<>(probabilities);

		return distribution.realization(randomNumber);
	}

}
