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

public class ShareBasedBusinessPartnerSelector {

	private final NumberOfPartnersModel numberModel;
	
	private final Map<DistributionCenter, Double> aggregate;
	private double total;
	private int count;
	private final Function<Business, Integer> demandProvider;
	private final Function<DistributionCenter, Double> shareProvider;
	private final Function<DistributionCenter, Double> capacityProvider;
	
	private final DeliveryResults results;

	private final String tag;

	public ShareBasedBusinessPartnerSelector(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, Function<DistributionCenter, Double> shareProvider,
			Function<Business, Integer> demandProvider, Function<DistributionCenter, Double> capacityProvider, DeliveryResults results, String tag) {
		
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

	public Collection<DistributionCenter> select(Business business) {
		int num = numberModel.select(business, business.getNextRandom());
		int amount = demandProvider.apply(business);
		
		Map<DistributionCenter, Double> weights = computeWeights();
		Collection<DistributionCenter> drawn = draw(num, weights, business::getNextRandom);
		updateAggregate(business, amount, drawn);
	
		return drawn;
	}
	
	private Map<DistributionCenter, Double> computeWeights() {
		Map<DistributionCenter, Double> weights = new LinkedHashMap<>(aggregate);		
		
		for (DistributionCenter dc : weights.keySet()) {
			weights.computeIfPresent(dc, (w, prev) -> shareProvider.apply(dc) - ((total > 0) ? (prev /total) : 0.0) );
		}
		
		double min = weights.values().stream().mapToDouble(d -> d).min().getAsDouble();
		if (min < 0) {
			for (DistributionCenter dc : weights.keySet()) {
				weights.computeIfPresent(dc, (w, prev) -> prev + Math.abs(min));
			}
		}
		
		return weights;
	}
	
	private void updateAggregate(Business business, int amount, Collection<DistributionCenter> options) {
		double totalCap = options.stream().mapToDouble(d -> capacityProvider.apply(d)).sum();
		
		options.forEach(dc -> {
			double inc = capacityProvider.apply(dc) / totalCap;
			aggregate.put(dc, aggregate.get(dc) + inc*amount);
			
			results.logBusinessPartner(business, dc, tag, amount, inc, inc*amount, options.size());
		});
		
		total += amount;
		count += 1;
	}
	
	private Collection<DistributionCenter> draw(int n, Map<DistributionCenter, Double> weights, DoubleSupplier random) {
		Map<DistributionCenter, Double> map = new LinkedHashMap<>(weights);
		Collection<DistributionCenter> res = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			DiscreteRandomVariable<DistributionCenter> var = new DiscreteRandomVariable<>(map);
			DistributionCenter dc = var.realization(random.getAsDouble());
			
			res.add(dc);
			map.remove(dc);
		}	
		
		return res;
	}
	
	public void printStatistics() {
		System.out.println("Partner Selector (" + count + " bsnss):");
		System.out.println("  Aggregate(" + total +"): " + aggregate);
		
		LinkedHashMap<DistributionCenter, Double> weights = computeCurrentWeights();
		System.out.println("  Weights: " + weights);
		
	}

	public LinkedHashMap<DistributionCenter, Double> computeCurrentWeights() {
		LinkedHashMap<DistributionCenter, Double> weights = new LinkedHashMap<>(aggregate);
		aggregate.keySet().forEach(d -> weights.computeIfPresent(d, (dc, prev) -> prev/total));
		return weights;
	}

	public static ShareBasedBusinessPartnerSelector forShipping(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
		
		return new ShareBasedBusinessPartnerSelector(numberModel, distributionCenters, d -> d.getShareDelivery(),
				b -> b.getDemandQuantity().getProduction(), d -> (double) d.getNumEmployees(), results, "shipping");
	}
	
	public static ShareBasedBusinessPartnerSelector forDelivery(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, DeliveryResults results) {
		
		return new ShareBasedBusinessPartnerSelector(numberModel, distributionCenters, d -> d.getShareDelivery(), //TODO separate relative share for prod and cons
				b -> b.getDemandQuantity().getConsumption(), d -> (double) d.getNumEmployees(), results, "delivery");
	}
	
}
