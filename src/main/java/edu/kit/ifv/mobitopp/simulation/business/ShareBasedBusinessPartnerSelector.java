package edu.kit.ifv.mobitopp.simulation.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

/**
 * TA selector for partner relations between businesses and cep service
 * providers ({@link DistributionCenter}). A greedy selection algorithm that
 * balances the selected partners to match the expected market shares by
 * considering the capacti of the selected partners and the demand of the
 * businesses.
 *
 * @author ar0305
 */
public class ShareBasedBusinessPartnerSelector implements BusinessPartnerSelector {

	private final NumberOfPartnersModel numberModel;
	private final Map<DistributionCenter, Double> aggregate;
	private double total;
	private int count;
	private final Function<Business, Integer> demandProvider;
	private final Function<DistributionCenter, Double> shareProvider;
	private final Function<DistributionCenter, Double> capacityProvider;
	private final DeliveryResults results;
	private final String tag;

	/**
	 * Instantiates a new share based business partner selector using the given
	 * market share provider.
	 *
	 * @param numberModel         the number model
	 * @param distributionCenters the distribution centers
	 * @param shareProvider       the share provider
	 * @param demandProvider      the demand provider
	 * @param capacityProvider    the capacity provider
	 * @param results             the results
	 * @param tag                 the tag
	 */
	private ShareBasedBusinessPartnerSelector(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, Function<DistributionCenter, Double> shareProvider,
			Function<Business, Integer> demandProvider, Function<DistributionCenter, Double> capacityProvider,
			DeliveryResults results, String tag) {

		this.numberModel = numberModel;

		this.total = 0.0;
		this.count = 0;
		this.aggregate = new LinkedHashMap<>();
		distributionCenters.forEach(d -> this.aggregate.put(d, 0.0));

		this.demandProvider = demandProvider;
		this.shareProvider = shareProvider;
		this.capacityProvider = capacityProvider;
		this.results = results;
		this.tag = tag;
	}

	/**
	 * Instantiates a new share based business partner selector using the
	 * distribution centers' market shares for business customers.
	 *
	 * @param numberModel         the number model
	 * @param distributionCenters the distribution centers
	 * @param demandProvider      the demand provider
	 * @param capacityProvider    the capacity provider
	 * @param results             the results
	 * @param tag                 the tag
	 */
	public ShareBasedBusinessPartnerSelector(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, Function<Business, Integer> demandProvider,
			Function<DistributionCenter, Double> capacityProvider, DeliveryResults results, String tag) {
		this(numberModel, distributionCenters, DistributionCenter::getShareBusiness, demandProvider, capacityProvider,
				results, tag);
	}

	/**
	 * Selects a subset of distribution centers as partners for the given business.
	 * This takes the expected markets shares per {@link DistributionCenter}, their
	 * approx. capacity, and the {@link Business businesses} demand into account.
	 *
	 * @param business the business
	 * @return the collection
	 */
	@Override
	public Collection<DistributionCenter> select(Business business) {
		int num = numberModel.select(business, business.getNextRandom());
		int amount = demandProvider.apply(business);

		Map<DistributionCenter, Double> weights = updateWeights();
		Collection<DistributionCenter> drawn = draw(num, weights, business::getNextRandom);
		updateAggregate(business, amount, drawn);

		return drawn;
	}

	/**
	 * Updates weights for each distribution center. It computes the difference
	 * between the expected market share and the current relative share of parcels
	 * assigned to each distribution center. If the minimum difference is negative,
	 * all weights are increased by the absolute value of that difference.
	 * 
	 * If no parcels have been assigned yet, the market shares are used as weights.
	 *
	 * @return the map
	 */
	private Map<DistributionCenter, Double> updateWeights() {
		Map<DistributionCenter, Double> weights = new LinkedHashMap<>(aggregate);

		for (DistributionCenter dc : weights.keySet()) {
			weights.computeIfPresent(dc, (w, prev) -> shareProvider.apply(dc) - ((total > 0) ? (prev / total) : 0.0));
		}

		double sum = weights.values().stream().mapToDouble(d -> (d >= 0) ? d : 0).sum();

		for (DistributionCenter dc : weights.keySet()) {
			weights.computeIfPresent(dc, (w, prev) -> (prev > 0) ? (prev / sum) : 0.0001);
		}

		return weights;
	}

	/**
	 * Update aggregate parcel amounts per {@link DistributionCenter} by assigning
	 * each partner a relative share proportional to their approximate capacity.
	 *
	 * @param business the business
	 * @param amount   the amount to be added
	 * @param options  the partners which share the given amount/parcel demand
	 */
	private void updateAggregate(Business business, int amount, Collection<DistributionCenter> options) {
		double totalCap = options.stream().mapToDouble(d -> capacityProvider.apply(d)).sum();

		options.forEach(dc -> {
			double inc = capacityProvider.apply(dc) / totalCap;
			aggregate.put(dc, aggregate.get(dc) + inc * amount);

			results.logBusinessPartner(business, dc, tag, amount, inc, inc * amount, options.size());
		});

		total += amount;
		count += 1;
	}

	/**
	 * Draw n {@link DistributionCenter}s from the full choice weighted by the given weights.
	 *
	 * @param n       the n
	 * @param weights the weights
	 * @param random  the random
	 * @return the collection
	 */
	private Collection<DistributionCenter> draw(int n, Map<DistributionCenter, Double> weights, DoubleSupplier random) {
		Map<DistributionCenter, Double> map = new LinkedHashMap<>(weights);
		Collection<DistributionCenter> res = new ArrayList<>();

		while (res.size() < n && !map.isEmpty()) {
			DiscreteRandomVariable<DistributionCenter> var = new DiscreteRandomVariable<>(map);
			DistributionCenter dc = var.realization(random.getAsDouble());

			res.add(dc);
			map.remove(dc);
		}

		return res;
	}

	/**
	 * Prints the partner-selection statistics.
	 */
	@Override
	public void printStatistics() {
		System.out.println("Partner Selector (" + tag + "; " + count + " bsnss):");
		System.out.println("  Aggregate(" + total + "): " + aggregate);

		LinkedHashMap<DistributionCenter, Double> weights = computeCurrentWeights();
		System.out.println("  Weights: " + weights);
		System.out.println();

	}

	/**
	 * Compute current weights of all distributions centers.
	 *
	 * @return the linked hash map
	 */
	@Override
	public LinkedHashMap<DistributionCenter, Double> computeCurrentWeights() {
		LinkedHashMap<DistributionCenter, Double> weights = new LinkedHashMap<>(aggregate);
		aggregate.keySet().forEach(d -> weights.computeIfPresent(d, (dc, prev) -> prev / total));
		return weights;
	}

	/**
	 * For shipping.
	 *
	 * @param numberModel         the number model
	 * @param distributionCenters the distribution centers
	 * @param results             the results
	 * @return the share based business partner selector
	 */
	public static ShareBasedBusinessPartnerSelector createFor(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, DeliveryResults results) {

		return new ShareBasedBusinessPartnerSelector(numberModel, distributionCenters, d -> d.getShareBusiness(),
				b -> b.getDemandQuantity().getProduction(), d -> (double) d.getNumEmployees(), results, "shipping");
	}

}
