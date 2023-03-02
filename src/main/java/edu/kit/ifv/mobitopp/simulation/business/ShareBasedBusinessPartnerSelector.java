package edu.kit.ifv.mobitopp.simulation.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.MarketShareProvider;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

/**
 * TA selector for partner relations between businesses and cep service
 * providers ({@link CEPServiceProvider}). A greedy selection algorithm that
 * balances the selected partners to match the expected market shares by
 * considering the capacity of the selected partners and the demand of the
 * businesses.
 *
 * @author ar0305
 */
public class ShareBasedBusinessPartnerSelector implements BusinessPartnerSelector {
	
	private final NumberOfPartnersModel numberModel;
	private final Map<CEPServiceProvider, Double> aggregate;
	private double total;
	private int count;
	private final Function<Business, Integer> demandProvider;
	private final Function<CEPServiceProvider, Double> shareProvider;
	private final Function<CEPServiceProvider, Double> capacityProvider;
	private final DeliveryResults results;
	private final String tag;

	/**
	 * Instantiates a new share based business partner selector using the given
	 * market share provider.
	 * 
	 * Service providers with share 0 are ignored.
	 *
	 * @param numberModel         the number model
	 * @param serviceProviders	  the service providers
	 * @param shareProvider       the share provider
	 * @param demandProvider      the demand provider
	 * @param capacityProvider    the capacity provider
	 * @param results             the results
	 * @param tag                 the tag
	 */
	public ShareBasedBusinessPartnerSelector(NumberOfPartnersModel numberModel,
											  Collection<CEPServiceProvider> serviceProviders, 
											  Function<CEPServiceProvider, Double> shareProvider,
											  Function<Business, Integer> demandProvider, 
											  Function<CEPServiceProvider, Double> capacityProvider,
											  DeliveryResults results, String tag) {

		this.numberModel = numberModel;

		this.total = 0.0;
		this.count = 0;
		this.aggregate = new LinkedHashMap<>();
		serviceProviders.stream()
						.filter(cepsp -> shareProvider.apply(cepsp) > 0)
						.forEach(cepsp -> this.aggregate.put(cepsp, 0.0));

		this.demandProvider = demandProvider;
		this.shareProvider = shareProvider;
		this.capacityProvider = capacityProvider;
		this.results = results;
		this.tag = tag;
	}

//	/**
//	 * Instantiates a new share based business partner selector using the
//	 * distribution centers' market shares for business customers.
//	 *
//	 * @param numberModel         the number model
//	 * @param serviceProviders	  the cep service providers
//	 * @param demandProvider      the demand provider
//	 * @param capacityProvider    the capacity provider
//	 * @param results             the results
//	 * @param tag                 the tag
//	 */
//	public ShareBasedBusinessPartnerSelector(NumberOfPartnersModel numberModel,
//											 Collection<CEPServiceProvider> serviceProviders, 
//											 Function<Business, Integer> demandProvider,
//											 Function<CEPServiceProvider, Double> capacityProvider, 
//											 DeliveryResults results, String tag) {
//		
//		this(numberModel, serviceProviders, DistributionCenter::getShareBusiness, demandProvider, capacityProvider, results, tag);
//	}

	/**
	 * Selects a subset of distribution centers as partners for the given business.
	 * This takes the expected markets shares per {@link CEPServiceProvider}, their
	 * approx. capacity, and the {@link Business businesses} demand into account.
	 *
	 * @param business the business
	 * @return the collection
	 */
	@Override
	public Collection<CEPServiceProvider> select(Business business) {
		int num = numberModel.select(business, business.getNextRandom());
		int amount = demandProvider.apply(business);

		Map<CEPServiceProvider, Double> weights = updateWeights();
		Collection<CEPServiceProvider> drawn = draw(num, weights, business::getNextRandom);
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
	private Map<CEPServiceProvider, Double> updateWeights() {
		Map<CEPServiceProvider, Double> weights = new LinkedHashMap<>(aggregate);

		for (CEPServiceProvider cepsp : weights.keySet()) {
			weights.computeIfPresent(cepsp, (w, prev) -> shareProvider.apply(cepsp) - ((total > 0) ? (prev / total) : 0.0));
		}

		double sum = weights.values().stream().mapToDouble(d -> (d >= 0) ? d : 0).sum();

		for (CEPServiceProvider cepsp : weights.keySet()) {
			weights.computeIfPresent(cepsp, (w, prev) -> (prev > 0) ? (prev / sum) : 0.0001);
		}

		return weights;
	}

	/**
	 * Update aggregate parcel amounts per {@link CEPServiceProvider} by assigning
	 * each partner a relative share proportional to their approximate capacity.
	 *
	 * @param business the business
	 * @param amount   the amount to be added
	 * @param options  the partners which share the given amount/parcel demand
	 */
	private void updateAggregate(Business business, int amount, Collection<CEPServiceProvider> options) {
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
	 * Draw n {@link CEPServiceProvider}s from the full choice weighted by the given weights.
	 *
	 * @param n       the n
	 * @param weights the weights
	 * @param random  the random
	 * @return the collection
	 */
	private Collection<CEPServiceProvider> draw(int n, Map<CEPServiceProvider, Double> weights, DoubleSupplier random) {
		Map<CEPServiceProvider, Double> map = new LinkedHashMap<>(weights);
		Collection<CEPServiceProvider> res = new ArrayList<>();

		while (res.size() < n && !map.isEmpty()) {
			DiscreteRandomVariable<CEPServiceProvider> var = new DiscreteRandomVariable<>(map);
			CEPServiceProvider dc = var.realization(random.getAsDouble());

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

		LinkedHashMap<CEPServiceProvider, Double> weights = computeCurrentWeights();
		System.out.println("  Weights: " + weights);
		System.out.println();

	}

	/**
	 * Compute current weights of all distributions centers.
	 *
	 * @return the linked hash map
	 */
	@Override
	public LinkedHashMap<CEPServiceProvider, Double> computeCurrentWeights() {
		LinkedHashMap<CEPServiceProvider, Double> weights = new LinkedHashMap<>(aggregate);
		aggregate.keySet().forEach(d -> weights.computeIfPresent(d, (dc, prev) -> prev / total));
		return weights;
	}

	/**
	 * Create {@link ShareBasedBusinessPartnerSelector} for shipping/production.
	 *
	 * @param numberModel         the number model
	 * @param serviceProviders	  the service providers
	 * @param shareProvider		  the market share provider
	 * @param results             the results
	 * @return the share based business partner selector with business production market shares
	 */
	public static ShareBasedBusinessPartnerSelector createForProduction(NumberOfPartnersModel numberModel,
															  Collection<CEPServiceProvider> serviceProviders,
															  MarketShareProvider shareProvider,
															  DeliveryResults results) {

		return createFor(numberModel, serviceProviders, shareProvider.getBusinessProductionShare()::get, DemandQuantity::getProduction, results, "production");
	}
	
	
	/**
	 * Create {@link ShareBasedBusinessPartnerSelector} for delivery/consumption.
	 *
	 * @param numberModel         the number model
	 * @param serviceProviders	  the service providers
	 * @param shareProvider		  the market share provider
	 * @param results             the results
	 * @return the share based business partner selector with business consumption market shares
	 */
	public static ShareBasedBusinessPartnerSelector createForConsumption(NumberOfPartnersModel numberModel,
															  Collection<CEPServiceProvider> serviceProviders,
															  MarketShareProvider shareProvider,
															  DeliveryResults results) {

		return createFor(numberModel, serviceProviders, shareProvider.getBusinessConsumptionShare()::get, DemandQuantity::getConsumption, results, "consumption");
	}
	
	/**
	 * Create {@link ShareBasedBusinessPartnerSelector}.
	 *
	 * @param numberModel      the number model
	 * @param serviceProviders the service providers
	 * @param shareProvider    the share provider function
	 * @param demandProvider   the demand provider
	 * @param results          the results
	 * @param tag              the tag
	 * @return the share based business partner selector
	 */
	public static ShareBasedBusinessPartnerSelector createFor(NumberOfPartnersModel numberModel,
															  Collection<CEPServiceProvider> serviceProviders,
															  Function<CEPServiceProvider, Double> shareProvider,
															  Function<DemandQuantity, Integer> demandProvider,
															  DeliveryResults results,
															  String tag) {

		return new ShareBasedBusinessPartnerSelector(numberModel, serviceProviders, shareProvider,
				b -> demandProvider.apply(b.getDemandQuantity()), sp -> (double) sp.getNumVehicles(), results, tag);
	}

}
