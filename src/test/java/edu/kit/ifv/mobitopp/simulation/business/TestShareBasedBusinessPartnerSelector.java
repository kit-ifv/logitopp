package edu.kit.ifv.mobitopp.simulation.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.util.random.BoxPlotDistribution;

public class TestShareBasedBusinessPartnerSelector {
	
	private static final int MAX_DEMAND = 10;
	
	private Collection<DistributionCenter> distributionCenters;
	private Function<DistributionCenter, Double> shareProvider;
	private Function<DistributionCenter, Double> capacityProvider;
	private Function<Business, Integer> demandProvider;

	private Business business;

	@BeforeEach
	public void setUp() {
		DistributionCenter d1 = mock(DistributionCenter.class);
		when(d1.toString()).thenReturn("A");
		when(d1.getRelativeShare()).thenReturn(0.5);
		when(d1.getNumEmployees()).thenReturn(15);
		
		DistributionCenter d2 = mock(DistributionCenter.class);
		when(d2.toString()).thenReturn("B");
		when(d2.getRelativeShare()).thenReturn(0.3);
		when(d2.getNumEmployees()).thenReturn(10);
		
		DistributionCenter d3 = mock(DistributionCenter.class);
		when(d3.toString()).thenReturn("C");
		when(d3.getRelativeShare()).thenReturn(0.2);
		when(d3.getNumEmployees()).thenReturn(12);
		
		distributionCenters = new ArrayList<>(List.of(d1, d2, d3));
		shareProvider = d -> d.getRelativeShare();
		capacityProvider = d -> (double) d.getNumEmployees();
		
		Random rand = new Random(42);
		demandProvider = b -> demand(rand);

		business = mock(Business.class);
		when(business.getNextRandom()).thenReturn(rand.nextDouble());
	
	}
	
	@Test
	public void select() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		ShareBasedBusinessPartnerSelector selector = new ShareBasedBusinessPartnerSelector(numberModel, distributionCenters, shareProvider, demandProvider, capacityProvider, mock(DeliveryResults.class), "Test");
		
		for (int i = 0; i < 1000; i++) {
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<DistributionCenter, Double> weights = selector.computeCurrentWeights();
		for (DistributionCenter dc : distributionCenters) {
			assertEquals(dc.getRelativeShare(), weights.get(dc), 0.001);
		}
				
	}
	
	
	
	private int demand(Random random) {
		return 1 + random.nextInt(MAX_DEMAND);
	}
	
}
