package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;

public class ParcelProductionQuantityModelTest {
	
	@Test
	public void consideringCurrentProductionQuantity() {

		ParcelAgent agent = mock(ParcelAgent.class);
		when(agent.getRemainingProductionQuantity()).thenReturn(9);
		
		
		int result = new ParcelProductionQuantityModel<>().select(agent, 0.42);
		
		verify(agent, times(1)).getRemainingProductionQuantity();
		
		assertEquals(9, result);
	}

}
