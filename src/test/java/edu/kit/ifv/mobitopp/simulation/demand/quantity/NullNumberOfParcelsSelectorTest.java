package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NullNumberOfParcelsSelectorTest {

	@Test
	public void select() {
		int result = new NullNumerOfParcelsSelector<>().select("hello", 4.2);
		assertEquals(result, 0);
		
		result = new NullNumerOfParcelsSelector<>().select("world", 1.7);
		assertEquals(result, 0);
		
		result = new NullNumerOfParcelsSelector<>().select("!", 0.9);
		assertEquals(result, 0);
	}
}
