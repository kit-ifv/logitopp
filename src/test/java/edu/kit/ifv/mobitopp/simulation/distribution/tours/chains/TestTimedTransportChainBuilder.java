package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TimedTransportChainBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.chains.TransportChain;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.CostFunction;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.StaticTransferTimeModel;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.chains.TransferTimeModel;
import edu.kit.ifv.mobitopp.time.Time;

public class TestTimedTransportChainBuilder {
	private CostFunction cost;
	private final TransferTimeModel transfer = new StaticTransferTimeModel();
	
	private TransportChain chainSimple;
	private List<DistributionCenter> hubsSimple;
	
	private TransportChain chainComplex;
	private List<DistributionCenter> hubsComplex;
	
	private TransportChain chainNoTram;
	private List<DistributionCenter> hubsNoTram;
	
	private Time departure;
	private Time departure2;

	@BeforeEach
	public void setup() {
		cost = mock(CostFunction.class);
		when(cost.estimateCost(any(),any(), any(), any(), anyDouble(), anyDouble(), anyDouble())).thenReturn(4.2d);
		when(cost.estimateLastMileCost(any(),any(), any())).thenReturn(13.37d);

		DistributionCenter hub1 = mock(DistributionCenter.class);
		when(hub1.getVehicleType()).thenReturn(VehicleType.TRUCK);
		when(hub1.toString()).thenReturn("A1");
		
		DistributionCenter hub2 = mock(DistributionCenter.class);
		when(hub2.toString()).thenReturn("B1");
		
		DistributionCenter hub3 = mock(DistributionCenter.class);
		when(hub3.getVehicleType()).thenReturn(VehicleType.BIKE);
		when(hub3.toString()).thenReturn("C1");
		
		DistributionCenter hub4 = mock(DistributionCenter.class);
		when(hub4.getVehicleType()).thenReturn(VehicleType.TRAM);
		when(hub4.toString()).thenReturn("B2");
		
		DistributionCenter hub5 = mock(DistributionCenter.class);
		when(hub5.getVehicleType()).thenReturn(VehicleType.TRUCK);
		when(hub5.toString()).thenReturn("A2");
		
		hubsSimple = List.of(hub1, hub2, hub3);
		chainSimple = new TransportChain(hubsSimple, true);
		
		hubsComplex = List.of(hub1, hub5, hub2, hub4, hub3);
		chainComplex = new TransportChain(hubsComplex, true);
		
		hubsNoTram = List.of(hub1, hub5, hub3);
		chainNoTram = new TransportChain(hubsNoTram, true);
		
		departure = Time.start.plusHours(9).plusMinutes(42);
		departure2 = Time.start.plusHours(10).plusMinutes(2);
	}
	
	@Test
	public void buildManual() {
		List<DistributionCenter> hubs = hubsSimple;
	
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainSimple, cost, transfer);

		TimedTransportChain timedChain = 
			builder.setDuration(hubs.get(0), 8, 2, 1)
				   .fixedDepartureAt(hubs.get(1), departure, 8, 4, 1)
				   .build().get();
				
		assertEquals(departure.minusMinutes(10), 	timedChain.getDeparture(hubs.get(0)));
		assertEquals(departure, 					timedChain.getDeparture(hubs.get(1)));
		assertEquals(departure.plusMinutes(12),		timedChain.getDeparture(hubs.get(2)));
	}
	
	@Test
	public void buildManualComplex() {
		List<DistributionCenter> hubs = hubsComplex;
		
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainComplex, cost, transfer);
		
		
		TimedTransportChain timedChain = 
			builder.setDuration(hubs.get(0), 8, 2, 1)
				   .setDuration(hubs.get(1), 5, 3,1 )
				   .fixedDepartureAt(hubs.get(2), departure, 8, 4, 1)
				   .fixedDepartureAt(hubs.get(3), departure2, 18, 6, 1)
				   .build().get();

		assertEquals(departure.minusMinutes(18), 	timedChain.getDeparture(hubs.get(0)));
		assertEquals(departure.minusMinutes(8),		timedChain.getDeparture(hubs.get(1)));
		assertEquals(departure,						timedChain.getDeparture(hubs.get(2)));
		assertEquals(departure2,					timedChain.getDeparture(hubs.get(3)));
		assertEquals(departure2.plusMinutes(24),	timedChain.getDeparture(hubs.get(4)));
	}

	@Test
	public void buildInvalid() {
		List<DistributionCenter> hubs = hubsComplex;

		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainComplex, cost, transfer);


		TimedTransportChain timedChain =
				builder.setDuration(hubs.get(0), 8, 2, 1)
						.setDuration(hubs.get(1), 5, 3,1 )
						.fixedDepartureAt(hubs.get(2), departure, 8, 4, 1)
						.fixedDepartureAt(hubs.get(3), departure2, 18, 6, 1)
						.build().get();

		assertEquals(departure.minusMinutes(18), 	timedChain.getDeparture(hubs.get(0)));
		assertEquals(departure.minusMinutes(8),		timedChain.getDeparture(hubs.get(1)));
		assertEquals(departure,						timedChain.getDeparture(hubs.get(2)));
		assertEquals(departure2,					timedChain.getDeparture(hubs.get(3)));
		assertEquals(departure2.plusMinutes(24),	timedChain.getDeparture(hubs.get(4)));
	}
	
	@Test
	public void buildMissingStart() {
		List<DistributionCenter> hubs = hubsNoTram;
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainNoTram, cost, transfer);
		
		builder.setDuration(hubs.get(0), 8, 2,1)
			   .setDuration(hubs.get(1), 5, 3, 1);
		
		assertThrows(IllegalStateException.class,
				builder::build
		);
	}

	@Test
	public void buildDefaultStart() {
		List<DistributionCenter> hubs = hubsNoTram;
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainNoTram, cost, transfer);
		
		TimedTransportChain timedChain = 
			builder.setDuration(hubs.get(0), 8, 2, 1)
				   .setDuration(hubs.get(1), 5, 3, 1)
				   .defaultDeparture(departure2)
				   .build().get();
		
		assertEquals(departure2, 				timedChain.getDeparture(hubs.get(0)));
		assertEquals(departure2.plusMinutes(10),timedChain.getDeparture(hubs.get(1)));
		assertEquals(departure2.plusMinutes(18),timedChain.getDeparture(hubs.get(2)));
	}
	
	@Test
	public void buildDefaultStartAlreadySet() {
		List<DistributionCenter> hubs = hubsNoTram;
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainNoTram, cost, transfer);
		
		TimedTransportChain timedChain = 
			builder.setDuration(hubs.get(0), 8, 2, 1)
				   .setDuration(hubs.get(1), 5, 3, 1)
				   .defaultDeparture(departure2)
				   .defaultDeparture(departure)
				   .build().get();
		
		assertEquals(departure2, 				timedChain.getDeparture(hubs.get(0)));
		assertEquals(departure2.plusMinutes(10),timedChain.getDeparture(hubs.get(1)));
		assertEquals(departure2.plusMinutes(18),timedChain.getDeparture(hubs.get(2)));
	}
	
	@Test
	public void missingPrefixDuration() {
		List<DistributionCenter> hubs = hubsComplex;
		
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainComplex, cost, transfer)
			.setDuration(hubs.get(0), 8, 2, 1);
		
		assertThrows(IllegalStateException.class,
			() -> {
				builder.fixedDepartureAt(hubs.get(2), departure, 8, 4, 1);
			}
		);
	}
	
	@Test
	public void invalidDepartureInPast() {
		List<DistributionCenter> hubs = hubsComplex;
		
		TimedTransportChainBuilder builder = new TimedTransportChainBuilder(chainComplex, cost, transfer);

		builder.setDuration(hubs.get(0), 8, 2, 1)
			   .setDuration(hubs.get(1), 5, 3, 1)
			   .fixedDepartureAt(hubs.get(2), departure2, 8, 4, 1);
				   
	    assertThrows(IllegalArgumentException.class,
			() -> {
				builder.fixedDepartureAt(hubs.get(3), departure, 18, 6, 1);
			}
		);
	}
}
