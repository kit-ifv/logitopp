package edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;
import static java.util.function.Function.identity;

import java.util.Collection;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.agents.ParcelAgent;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

/**
 * The Class ShareBasedSelector is a generic selector which selects
 * values/items based on their share.
 *
 * @param <T> the generic type of the alternatives to be selected
 */
public class ShareBasedSelector<A extends ParcelAgent, P extends ParcelBuilder<A>, T> implements ParcelDemandModelStep<A, P, T> {

	private final Map<T, Double> probabilities;

	/**
	 * Instantiates a new {@link ShareBasedSelector} for the given values.
	 * Each value has the same share (probability) of /n where n = values.size().
	 *
	 * @param values the values (options) to be selected
	 */
	public ShareBasedSelector(Collection<T> values) {
		double equalSahre = 1.0d / values.size();
		this.probabilities = values.stream().collect(toLinkedMap(identity(), v -> equalSahre));
	}

	/**
	 * Instantiates a new {@link ShareBasedSelector} for the key values of the given map.
	 * The map's double values represent the shares of their respective key.
	 *
	 * @param shares the shares
	 */
	public ShareBasedSelector(Map<T, Double> shares) {
		this.probabilities = computeProbabilities(shares);
	}

	/**
	 * Compute probabilities for each value/item by normalizing the sum of shares to 1.
	 *
	 * @param shares the shares
	 * @return the map
	 */
	private Map<T, Double> computeProbabilities(Map<T, Double> shares) {

		double sumOfShares = shares.values().stream().mapToDouble(Double::doubleValue).sum();

		Map<T, Double> probabilitiesMap = shares
			.entrySet()
			.stream()
			.collect(toLinkedMap(Map.Entry::getKey, e -> e.getValue() / sumOfShares));

		return probabilitiesMap;
	}

	/**
	 * Selects one of the values/items based on their shares.
	 *
	 * @param randomNumber a random number
	 * @return the selected value/item
	 */
	public T select(double randomNumber) {

		DiscreteRandomVariable<T> centerDistribution = new DiscreteRandomVariable<>(probabilities);

		return centerDistribution.realization(randomNumber);
	}

	
	@Override
	public T select(P parcel, Collection<P> otherParcels,
			int numOfParcels, double randomNumber) {
		return this.select(randomNumber);
	}

}
