package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;

public class ParcelProductionQuantityModelTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void consideringCurrentProductionQuantity() {
		ParcelQuantityModel<ParcelAgent> other = mock(ParcelQuantityModel.class);
		when(other.select(any(), anyDouble())).thenReturn(14);		
		
		ParcelAgent agent = mock(ParcelAgent.class);
		when(agent.getRemainingProductionQuantity()).thenReturn(9);
		
		
		int result = new ParcelProductionQuantityModel<>(other).select(agent, 0.42);
		
		verify(agent, times(1)).setPlannedProductionQuantity(14);
		verify(agent, times(1)).getRemainingProductionQuantity();
		verify(other, times(1)).select(agent, 0.42);
		
		assertEquals(9, result);
	}

}
