package edu.kit.ifv.mobitopp.simulation.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.util.random.BoxPlotDistribution;

public class TestShareBasedBusinessPartnerSelector {
	
	private static final int MAX_DEMAND = 10;
	
	private Collection<DistributionCenter> distributionCenters;
	private Function<DistributionCenter, Double> shareProvider;
	private Function<DistributionCenter, Double> capacityProvider;
	private Function<Business, Integer> demandProvider;

	private Business business;

	private Random rand;

	@BeforeEach
	public void setUp() throws NoSuchFieldException, SecurityException {
		DistributionCenter d1 = mock(DistributionCenter.class);
		when(d1.toString()).thenReturn("A");
		when(d1.getShareDelivery()).thenReturn(0.5);
		when(d1.getShareShipping()).thenReturn(0.2);
		when(d1.getNumEmployees()).thenReturn(15);
		
		DistributionCenter d2 = mock(DistributionCenter.class);
		when(d2.toString()).thenReturn("B");
		when(d2.getShareDelivery()).thenReturn(0.3);
		when(d2.getShareShipping()).thenReturn(0.5);
		when(d2.getNumEmployees()).thenReturn(10);
		
		DistributionCenter d3 = mock(DistributionCenter.class);
		when(d3.toString()).thenReturn("C");
		when(d3.getShareDelivery()).thenReturn(0.2);
		when(d3.getShareShipping()).thenReturn(0.3);
		when(d3.getNumEmployees()).thenReturn(12);
		
		distributionCenters = new ArrayList<>(List.of(d1, d2, d3));
		shareProvider = d -> d.getShareDelivery();
		capacityProvider = d -> (double) d.getNumEmployees();
		
		this.rand = new Random(42);
		demandProvider = b -> demand(rand);

		business = new Business(1L, "", Branch.A, BuildingType.HOSPITAL, 12, 42.0, Map.of(), null, null, null, rand);		
	}
	
	@Test
	public void select() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		BusinessPartnerSelector selector = new ShareBasedBusinessPartnerSelector(numberModel, distributionCenters, shareProvider, demandProvider, capacityProvider, mock(DeliveryResults.class), "Test");
	
		for (int i = 0; i < 1000; i++) {
			setRandomDemand();
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<DistributionCenter, Double> weights = selector.computeCurrentWeights();
		for (DistributionCenter dc : distributionCenters) {
			assertEquals(dc.getShareDelivery(), weights.get(dc), 0.01);
		}
				
	}
	
	@Test
	public void selectMoreThanAvailable() {
		NumberOfPartnersModel numberModel = (b,r) -> 4;
		
		BusinessPartnerSelector selector = new ShareBasedBusinessPartnerSelector(numberModel, distributionCenters, shareProvider, demandProvider, capacityProvider, mock(DeliveryResults.class), "Test");
		
		setRandomDemand();
		Collection<DistributionCenter> res = selector.select(business);

		assertEquals(3, res.size());
		
	}
	
	
	@Test
	public void selectForShipping() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		BusinessPartnerSelector selector =  ShareBasedBusinessPartnerSelector.forShipping(numberModel, distributionCenters,  mock(DeliveryResults.class));

		for (int i = 0; i < 10000; i++) {
			setRandomDemand();
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<DistributionCenter, Double> weights = selector.computeCurrentWeights();
		for (DistributionCenter dc : distributionCenters) {
			assertEquals(dc.getShareShipping(), weights.get(dc), 0.01);
		}
				
	}
	
	@Test
	public void selectForDelivery() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		BusinessPartnerSelector selector =  ShareBasedBusinessPartnerSelector.forDelivery(numberModel, distributionCenters,  mock(DeliveryResults.class));

		for (int i = 0; i < 1000; i++) {
			setRandomDemand();
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<DistributionCenter, Double> weights = selector.computeCurrentWeights();
		for (DistributionCenter dc : distributionCenters) {
			assertEquals(dc.getShareDelivery(), weights.get(dc), 0.01);
		}
				
	}
	
	private void setRandomDemand() {
		this.business.getDemandQuantity().setProduction(demand(rand));
		this.business.getDemandQuantity().setConsumption(demand(rand));
	}
	
	private int demand(Random random) {
		return 1 + random.nextInt(MAX_DEMAND);
	}
	
}
