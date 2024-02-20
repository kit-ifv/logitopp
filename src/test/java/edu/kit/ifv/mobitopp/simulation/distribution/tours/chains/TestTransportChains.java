package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.BIKE;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.TRAM;
import static edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType.TRUCK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.util.collections.Pair;

public class TestTransportChains {
	
	private TransportChain chain;
	private TransportChain singleLegChain;
	private TransportChain twoLegChain;
	private List<DistributionCenter> hubs;
	
	@BeforeEach
	public void setUp() {
		DistributionCenter hub1 = mock(DistributionCenter.class);
		when(hub1.getVehicleType()).thenReturn(TRUCK);
		when(hub1.toString()).thenReturn("A1");
		
		DistributionCenter hub2 = mock(DistributionCenter.class);
		when(hub2.getVehicleType()).thenReturn(TRAM);
		when(hub2.toString()).thenReturn("A2");
		
		DistributionCenter hub3 = mock(DistributionCenter.class);
		when(hub3.getVehicleType()).thenReturn(TRUCK);
		when(hub3.toString()).thenReturn("B1");
		
		DistributionCenter hub4 = mock(DistributionCenter.class);
		when(hub4.getVehicleType()).thenReturn(TRAM);
		when(hub4.toString()).thenReturn("B2");
		
		DistributionCenter hub5 = mock(DistributionCenter.class);
		when(hub5.getVehicleType()).thenReturn(BIKE);
		when(hub5.toString()).thenReturn("C1");
		
		
		
		hubs = List.of(hub1, hub2, hub3, hub4, hub5);
		
		chain = new TransportChain(List.of(hub1, hub2, hub3, hub4, hub5), false);
		singleLegChain = new TransportChain(List.of(hub1), false);
		twoLegChain = new TransportChain(List.of(hub4, hub5), false);
	}
	
	@Test
	public void getFirst() {
		assertEquals(hubs.get(0), chain.first());
		assertEquals(hubs.get(0), singleLegChain.first());
		assertEquals(hubs.get(3), twoLegChain.first());
	}
	
	@Test
	public void getLast() {
		assertEquals(hubs.get(4), chain.last());
		assertEquals(hubs.get(0), singleLegChain.last());
		assertEquals(hubs.get(4), twoLegChain.last());
	}
	
	@Test
	public void getLastMileVehicle() {
		assertEquals(VehicleType.BIKE, chain.lastMileVehicle());
		assertEquals(VehicleType.TRUCK, singleLegChain.lastMileVehicle());
		assertEquals(VehicleType.BIKE, twoLegChain.lastMileVehicle());
	}
	
	@Test
	public void getTailHubs() {
		assertEquals(hubs.subList(1, 5), chain.tail());
		assertEquals(List.of(), singleLegChain.tail());
		assertEquals(List.of(hubs.get(4)), twoLegChain.tail());
	}
	
	@Test
	public void getIntermedHubs() {
		assertEquals(hubs.subList(1, 4), chain.intermediate());
		assertEquals(List.of(), singleLegChain.intermediate());
		assertEquals(List.of(), twoLegChain.intermediate());
	}

	@Test
	public void getVehicleSequence() {
		assertEquals(List.of(TRUCK, TRAM, TRUCK, TRAM, BIKE), chain.getVehicleTypes());
		assertEquals(List.of(TRUCK), singleLegChain.getVehicleTypes());
		assertEquals(List.of(TRAM, BIKE), twoLegChain.getVehicleTypes());
	}
	
	@Test
	public void getVehicleLegs() {
		assertEquals(List.of(leg(0), leg(2)), chain.legsOfType(TRUCK));
		assertEquals(List.of(), singleLegChain.legsOfType(TRUCK));
		assertEquals(List.of(), twoLegChain.legsOfType(TRUCK));
		
		assertEquals(List.of(leg(1), leg(3)), chain.legsOfType(TRAM));
		assertEquals(List.of(), singleLegChain.legsOfType(TRAM));
		assertEquals(List.of(leg(3)), twoLegChain.legsOfType(TRAM));
		
		assertEquals(List.of(), chain.legsOfType(BIKE));
		assertEquals(List.of(), singleLegChain.legsOfType(BIKE));
		assertEquals(List.of(), twoLegChain.legsOfType(BIKE));
	}
	
	private Pair<DistributionCenter, DistributionCenter> leg(int i) {
		return new Pair<>(hubs.get(i), hubs.get(i+1));
	}
	
	@Test
	public void getNextHub() {
		assertEquals(hubs.get(1), chain.nextHubAfter(hubs.get(0)).get() );
		assertEquals(hubs.get(2), chain.nextHubAfter(hubs.get(1)).get() );
		assertEquals(hubs.get(3), chain.nextHubAfter(hubs.get(2)).get() );
		assertEquals(hubs.get(4), chain.nextHubAfter(hubs.get(3)).get() );
		assertTrue(chain.nextHubAfter(hubs.get(4)).isEmpty());
		
		assertTrue(singleLegChain.nextHubAfter(hubs.get(0)).isEmpty());
		
		assertEquals(hubs.get(4), twoLegChain.nextHubAfter(hubs.get(3)).get() );
		assertTrue(twoLegChain.nextHubAfter(hubs.get(4)).isEmpty());
	}
	
	@Test
	public void illegalNextHub() {		   
	    assertThrows(IllegalArgumentException.class,
			() -> {
				singleLegChain.nextHubAfter(hubs.get(4));
			}
		);
	    
	    assertThrows(IllegalArgumentException.class,
			() -> {
				twoLegChain.nextHubAfter(hubs.get(0));
			}
		);

	}
	
	@Test
	public void getAsString() {
		assertEquals("A1, A2, B1, B2, C1", chain.toString());
		assertEquals("A1", singleLegChain.toString());
		assertEquals("B2, C1", twoLegChain.toString());
	}
}
