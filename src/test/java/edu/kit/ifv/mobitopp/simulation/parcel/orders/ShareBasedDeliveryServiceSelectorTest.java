package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.parcels.orders.DeliveryServiceSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ShareBasedDeliveryServiceSelector;

public class ShareBasedDeliveryServiceSelectorTest {

	private String serviceA;
	private String serviceB;
	
	private Map<String, Double> sharesA;
	private Map<String, Double> sharesB;
	private List<String> centers;
	
	
	@BeforeEach
	public void setUp() {
		serviceA = "ServiceA";
		serviceB = "ServiceB";
		sharesA = Map.of(serviceA, 1.0, serviceB, 0.0);
		sharesB = Map.of(serviceA, 0.0, serviceB, 1.0);
		centers = List.of(serviceA, serviceB);
	}
	
	@Test
	public void equalShares() {
		DeliveryServiceSelector selector =
			new ShareBasedDeliveryServiceSelector(centers);
		
		Random rand = new Random(42);
		List<String> selected = selectNCenters(selector, 10, rand);
		
		assertTrue(selected.contains(serviceA));
		assertTrue(selected.contains(serviceB));
		
	}
	
	@Test
	public void customSharesFavorA() {
		DeliveryServiceSelector selector =
			new ShareBasedDeliveryServiceSelector(sharesA);
		
		Random rand = new Random(42);
		List<String> selected = selectNCenters(selector, 10, rand);
		
		assertTrue(selected.contains(serviceA));
		assertFalse(selected.contains(serviceB));
		
	}
	
	@Test
	public void customSharesFavorB() {
		DeliveryServiceSelector selector =
			new ShareBasedDeliveryServiceSelector(sharesB);
		
		Random rand = new Random(42);
		List<String> selected = selectNCenters(selector, 10, rand);
		
		assertFalse(selected.contains(serviceA));
		assertTrue(selected.contains(serviceB));
		
	}
	
	private List<String> selectNCenters(DeliveryServiceSelector selector, int n, Random rand) {
		List<String> centers = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			centers.add(selector.select(null, 1, null, null, null, rand.nextDouble()));
		}
		return centers;
	}
		
}
