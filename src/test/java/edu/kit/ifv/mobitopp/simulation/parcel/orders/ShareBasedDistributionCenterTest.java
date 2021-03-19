package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.DistributionCenterSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ShareBasedDistributionCenterSelector;

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
		DistributionCenterSelector selector =
			new ShareBasedDistributionCenterSelector(centers);
		
		Random rand = new Random(42);
		List<DistributionCenter> selected = selectNCenters(selector, 10, rand);
		
		assertTrue(selected.contains(centerA));
		assertTrue(selected.contains(centerB));
		
	}
	
	@Test
	public void customSharesFavorA() {
		DistributionCenterSelector selector =
			new ShareBasedDistributionCenterSelector(sharesA);
		
		Random rand = new Random(42);
		List<DistributionCenter> selected = selectNCenters(selector, 10, rand);
		
		assertTrue(selected.contains(centerA));
		assertFalse(selected.contains(centerB));
		
	}
	
	@Test
	public void customSharesFavorB() {
		DistributionCenterSelector selector =
			new ShareBasedDistributionCenterSelector(sharesB);
		
		Random rand = new Random(42);
		List<DistributionCenter> selected = selectNCenters(selector, 10, rand);
		
		assertFalse(selected.contains(centerA));
		assertTrue(selected.contains(centerB));
		
	}
	
	private List<DistributionCenter> selectNCenters(DistributionCenterSelector selector, int n, Random rand) {
		List<DistributionCenter> centers = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			centers.add(selector.select(null, 1, null, null, null, null, rand.nextDouble()));
		}
		return centers;
	}
		
}
