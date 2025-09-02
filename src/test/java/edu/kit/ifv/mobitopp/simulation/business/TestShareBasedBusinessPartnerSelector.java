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

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.business.partners.BusinessPartnerSelector;
import edu.kit.ifv.mobitopp.simulation.business.partners.DistributionBasedNumberOfPartnersModel;
import edu.kit.ifv.mobitopp.simulation.business.partners.NumberOfPartnersModel;
import edu.kit.ifv.mobitopp.simulation.business.partners.ShareBasedBusinessPartnerSelector;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.MarketShareProvider;
import edu.kit.ifv.mobitopp.util.random.BoxPlotDistribution;

public class TestShareBasedBusinessPartnerSelector {
	
	private static final int MAX_DEMAND = 10;
	
	private Collection<CEPServiceProvider> serviceProviders;
	private Function<CEPServiceProvider, Double> capacityProvider;
	private Function<Business, Integer> demandProvider;
	
	private Map<CEPServiceProvider, Double> sharesProduction;
	private Map<CEPServiceProvider, Double> sharesConsumption;
	private MarketShareProvider shareProvider;

	private Business business;

	private Random rand;

	@BeforeEach
	public void setUp() throws NoSuchFieldException, SecurityException {
		this.sharesConsumption = new LinkedHashMap<>();
		this.sharesProduction = new LinkedHashMap<>();
		
		CEPServiceProvider d1 = mock(CEPServiceProvider.class);
		when(d1.toString()).thenReturn("A");
		sharesConsumption.put(d1, 0.5);
		sharesProduction.put(d1, 0.2);
		when(d1.getNumVehicles()).thenReturn(15);
		
		CEPServiceProvider d2 = mock(CEPServiceProvider.class);
		when(d2.toString()).thenReturn("B");
		sharesConsumption.put(d2, 0.3);
		sharesProduction.put(d2, 0.5);
		when(d2.getNumVehicles()).thenReturn(10);
		
		CEPServiceProvider d3 = mock(CEPServiceProvider.class);
		when(d3.toString()).thenReturn("C");
		sharesConsumption.put(d3, 0.2);
		sharesProduction.put(d3, 0.3);
		when(d3.getNumVehicles()).thenReturn(12);
		
		serviceProviders = new ArrayList<>(List.of(d1, d2, d3));
		capacityProvider = d -> (double) d.getNumVehicles();
		
		this.rand = new Random(42);
		demandProvider = b -> demand(rand);

		business = new Business(1L, Branch.A.getSector(), Map.of(), null, null, rand);
		
		shareProvider = mock(MarketShareProvider.class);
		when(shareProvider.getBusinessConsumptionShare()).thenReturn(sharesConsumption);
		when(shareProvider.getBusinessProductionShare()).thenReturn(sharesProduction);
	}
	
	@Test
	public void select() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		BusinessPartnerSelector selector = new ShareBasedBusinessPartnerSelector(numberModel, serviceProviders, sharesProduction::get, demandProvider, capacityProvider, mock(DeliveryResults.class), "Test");
	
		for (int i = 0; i < 1000; i++) {
			setRandomDemand();
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<CEPServiceProvider, Double> weights = selector.computeCurrentWeights();
		for (CEPServiceProvider cepsp : serviceProviders) {
			assertEquals(sharesProduction.get(cepsp), weights.get(cepsp), 0.01);
		}
				
	}
	
	@Test
	public void selectMoreThanAvailable() {
		NumberOfPartnersModel numberModel = (b,r) -> 4;
		
		BusinessPartnerSelector selector = new ShareBasedBusinessPartnerSelector(numberModel, serviceProviders, sharesProduction::get, demandProvider, capacityProvider, mock(DeliveryResults.class), "Test");
		
		setRandomDemand();
		Collection<CEPServiceProvider> res = selector.select(business);

		assertEquals(3, res.size());
		
	}
	
	
	@Test
	public void selectForShipping() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		BusinessPartnerSelector selector =  ShareBasedBusinessPartnerSelector.createForProduction(numberModel, serviceProviders, shareProvider, mock(DeliveryResults.class));

		for (int i = 0; i < 10000; i++) {
			setRandomDemand();
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<CEPServiceProvider, Double> weights = selector.computeCurrentWeights();
		for (CEPServiceProvider cepsp : serviceProviders) {
			assertEquals(sharesProduction.get(cepsp), weights.get(cepsp), 0.01);
		}
				
	}
	
	@Test
	public void selectForDelivery() {
		NumberOfPartnersModel numberModel = new DistributionBasedNumberOfPartnersModel(b -> new BoxPlotDistribution(1, 1, 2, 2, 3));
		
		BusinessPartnerSelector selector =  ShareBasedBusinessPartnerSelector.createForConsumption(numberModel, serviceProviders, shareProvider, mock(DeliveryResults.class));

		for (int i = 0; i < 1000; i++) {
			setRandomDemand();
			selector.select(business);
		}
		
		selector.printStatistics();
		
		LinkedHashMap<CEPServiceProvider, Double> weights = selector.computeCurrentWeights();
		for (CEPServiceProvider cepsp : serviceProviders) {
			assertEquals(sharesConsumption.get(cepsp), weights.get(cepsp), 0.01);
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
