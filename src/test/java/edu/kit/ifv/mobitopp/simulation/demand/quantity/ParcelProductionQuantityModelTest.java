package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.demand.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.ParcelAgent;

public class ParcelProductionQuantityModelTest {
	
	@Test
	public void consideringCurrentProductionQuantity() {
		DemandQuantity dq = mock(DemandQuantity.class);
		when(dq.getProduction()).thenReturn(9);
		
		ParcelAgent agent = mock(ParcelAgent.class);
		when(agent.getDemandQuantity()).thenReturn(dq);
		
		
		int result = new ParcelProductionQuantityModel<>().select(agent, 0.42);
		
		verify(agent, times(1)).getDemandQuantity();
		verify(dq, times(1)).getProduction();
		
		assertEquals(9, result);
	}

}
