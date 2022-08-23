package edu.kit.ifv.mobitopp.simulation.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class ShareBasedBusniessPartnerSelector {

	private final NumberOfPartnersModel numberModel;
	
	private final Map<DistributionCenter, Double> aggregate;
	private double total;
	private final Function<Business, Integer> demandProvider;
	private final Function<DistributionCenter, Double> shareProvider;
	private final Function<DistributionCenter, Double> capacityProvider;

	public ShareBasedBusniessPartnerSelector(NumberOfPartnersModel numberModel,
			Collection<DistributionCenter> distributionCenters, Function<DistributionCenter, Double> shareProvider,
			Function<Business, Integer> demandProvider, Function<DistributionCenter, Double> capacityProvider) {
		
		this.numberModel = numberModel;
		
		this.total = 0.0;
		this.aggregate = new LinkedHashMap<>();
		distributionCenters.forEach(d -> this.aggregate.put(d, 0.0));
		
		this.demandProvider = demandProvider;
		this.shareProvider = shareProvider;
		this.capacityProvider = capacityProvider;
	}

	public Collection<DistributionCenter> select(Business business) {
		int num = numberModel.select(business, business.getNextRandom());
		int amount = demandProvider.apply(business);
		
		Map<DistributionCenter, Double> weights = computeWeights();
		Collection<DistributionCenter> drawn = draw(num, weights, business::getNextRandom);
		updateAggregate(amount, drawn);
	
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
	
	private void updateAggregate(double amount, Collection<DistributionCenter> options) {
		double totalCap = options.stream().mapToDouble(d -> capacityProvider.apply(d)).sum();
		
		options.forEach(dc -> {
			double inc = capacityProvider.apply(dc) / totalCap;
			aggregate.put(dc, aggregate.get(dc) + inc*amount);
		});
		
		total += amount;
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

}
