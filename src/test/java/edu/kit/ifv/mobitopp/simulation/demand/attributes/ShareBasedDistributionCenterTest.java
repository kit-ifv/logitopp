package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.demand.attributes.ParcelDemandModelStep;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ShareBasedSelector;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;

public class ShareBasedDistributionCenterTest {

	private DistributionCenter centerA;
	private DistributionCenter centerB;
	
	private Map<DistributionCenter, Double> sharesA;
	private Map<DistributionCenter, Double> sharesB;
	private List<DistributionCenter> centers;
	
	
	@BeforeEach
	public void setUp() {
		centerA = mock(DistributionCenter.class);
		centerB = mock(DistributionCenter.class);
		sharesA = Map.of(centerA, 1.0, centerB, 0.0);
		sharesB = Map.of(centerA, 0.0, centerB, 1.0);
		centers = List.of(centerA, centerB);
	}
	
	@Test
	public void equalShares() {
		ParcelDemandModelStep<DistributionCenter> selector =
			new ShareBasedSelector<>(centers);
		
		Random rand = new Random(42);
		List<DistributionCenter> selected = selectNCenters(selector, 10, rand);
		
		assertTrue(selected.contains(centerA));
		assertTrue(selected.contains(centerB));
		
	}
	
	@Test
	public void customSharesFavorA() {
		ParcelDemandModelStep<DistributionCenter> selector =
			new ShareBasedSelector<>(sharesA);
		
		Random rand = new Random(42);
		List<DistributionCenter> selected = selectNCenters(selector, 10, rand);
		
		assertTrue(selected.contains(centerA));
		assertFalse(selected.contains(centerB));
		
	}
	
	@Test
	public void customSharesFavorB() {
		ParcelDemandModelStep<DistributionCenter> selector =
			new ShareBasedSelector<>(sharesB);
		
		Random rand = new Random(42);
		List<DistributionCenter> selected = selectNCenters(selector, 10, rand);
		
		assertFalse(selected.contains(centerA));
		assertTrue(selected.contains(centerB));
		
	}
	
	private List<DistributionCenter> selectNCenters(ParcelDemandModelStep<DistributionCenter> selector, int n, Random rand) {
		List<DistributionCenter> centers = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			centers.add(selector.select(null, null, 1, rand.nextDouble()));
		}
		return centers;
	}
		
}
