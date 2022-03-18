package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;

public class CopyProviderModelStepTest {
	
	private ValueProvider<String> source;
	private ValueProvider<String> destination;
	private CopyProviderModelStep<?, ?, String> step;
	
	class Element {
		@Getter @Setter private String name;
		
		public Element(String name) {
			this.name = name;
		}

	}
	
	@Test
	public void unsupportedSelectOperation() {
		assertThrows(UnsupportedOperationException.class,
			() -> {
				new CopyProviderModelStep<>(null).select(null, null, 0, 0);
			}
		);
	}
	
	@Test
	public void copyDeterminedProvider() {
		this.source = new InstantValueProvider<>("start");
		assertTrue(this.source.isDetermined());
		
		this.step = new CopyProviderModelStep<>(p -> this.source);
		this.step.set(null, null, 0, 0, (p,v) -> this.destination = v);
		
		assertTrue(this.destination.isDetermined());
		assertEquals("start", this.destination.getValue());
		assertTrue(this.destination instanceof InstantValueProvider);
		assertNotEquals(this.source, this.destination);		
	}
	
	@Test 
	public void copyDeterminedLatentProvider() {
		Element value = new Element("Start");
		this.source = new LatentValueProvider<>(() -> value.getName());

		value.setName("changed");
		
		assertFalse(this.source.isDetermined());
		this.source.getValue();
		assertTrue(this.source.isDetermined());
		
		value.setName("another change");
		
		this.step = new CopyProviderModelStep<>(p -> this.source);
		this.step.set(null, null, 0, 0, (p,v) -> this.destination = v);
		
		assertTrue(this.destination.isDetermined());
		assertEquals("changed", this.destination.getValue());
		assertTrue(this.destination instanceof InstantValueProvider);
		assertNotEquals(this.source, this.destination);	
	}

	
	@Test 
	public void copyUndeterminedLatentProvider() {
		Element value = new Element("Start");
		this.source = new LatentValueProvider<>(() -> value.getName());

		value.setName("changed");
		assertFalse(this.source.isDetermined());

		this.step = new CopyProviderModelStep<>(p -> this.source);
		this.step.set(null, null, 0, 0, (p,v) -> this.destination = v);
		
		value.setName("another change");
		
		assertFalse(this.destination.isDetermined());
		assertEquals("another change", this.destination.getValue());
		assertTrue(this.destination.isDetermined());
		assertTrue(this.destination instanceof LatentValueProvider);
		assertNotEquals(this.source, this.destination);
		
		value.setName("third change");
		assertNotEquals("third change", this.destination.getValue());
		
	}
}
