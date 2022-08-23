package edu.kit.ifv.mobitopp.simulation.business;

import java.util.function.Function;

import edu.kit.ifv.mobitopp.util.random.BoxPlotDistribution;

public class DistributionBasedNumberOfPartnersModel implements NumberOfPartnersModel {

	private final Function<Business, BoxPlotDistribution> distributionProvider;
	
	public DistributionBasedNumberOfPartnersModel(Function<Business, BoxPlotDistribution> distributionProvider) {
		this.distributionProvider = distributionProvider;
	}
	
	@Override
	public int select(Business business, double randomNumber) {
		BoxPlotDistribution distribution = distributionProvider.apply(business);
		
		return (int) Math.round(distribution.draw(randomNumber));
	}

}
