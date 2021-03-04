package edu.kit.ifv.mobitopp.simulation.parcels.orders;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;
import static java.util.function.Function.identity;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class ShareBasedSelector<T> {

	private final Map<T, Double> probabilities;

	public ShareBasedSelector(Collection<T> values) {
		double equalSahre = 1.0d / values.size();
		this.probabilities = values.stream().collect(toLinkedMap(identity(), v -> equalSahre));
	}

	public ShareBasedSelector(Map<T, Double> shares) {
		this.probabilities = computeProbabilities(shares);
	}

	private Map<T, Double> computeProbabilities(Map<T, Double> shares) {

		double sumOfShares = shares.values().stream().mapToDouble(Double::doubleValue).sum();

		Map<T, Double> probabilitiesMap = shares
			.entrySet()
			.stream()
			.collect(toLinkedMap(Map.Entry::getKey, e -> e.getValue() / sumOfShares));

		return probabilitiesMap;
	}

	public T select(double randomNumber) {

		DiscreteRandomVariable<T> centerDistribution = new DiscreteRandomVariable<>(probabilities);

		return centerDistribution.realization(randomNumber);
	}

}
