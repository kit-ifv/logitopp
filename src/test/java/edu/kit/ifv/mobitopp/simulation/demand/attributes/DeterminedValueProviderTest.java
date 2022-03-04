package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeterminedValueProviderTest {

	private DeterminedValueProvider<Element> provider;
	private Element value;
	
	class Element {
		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}
	}
	
	@BeforeEach
	public void setUp() {
		this.value = new Element();
		this.provider = new DeterminedValueProvider<>(value);
		
	}
	
	@Test
	public void getValue() {
		assertEquals(this.value, this.provider.getValue());
		assertNotEquals(new Element(), this.provider.getValue());
	}
	
	@Test
	public void isDetermined() {
		assertEquals(true, this.provider.isDetermined());
	}
	
}
