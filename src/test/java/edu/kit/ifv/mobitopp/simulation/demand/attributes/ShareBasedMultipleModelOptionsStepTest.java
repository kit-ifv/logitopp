package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;

public class ShareBasedMultipleModelOptionsStepTest extends MultipleModelOptionsStepTest {
		
	@Test
	public void getValues() {
		for (int i = 0; i < 100; i++) {
			
			options.set(null, null, 1, randValue(), (p,v) -> res1 = v);
			
			if (res1.isDetermined()) {
				validateInstant("instant", res1);
			} else {
				validateLatent("latent", res1);
			}
			
		}
	}
	
	
	@Test
	public void getLatentValue() {
		for (int i = 0; i < 100; i++) {
			this.element.setName("val");
			
			options.set(null, null, 1, randValue(), (p,v) -> res1 = v);

			if (res1.isDetermined()) {
				validateInstant("instant", res1);
			} else {
				validateLatent("val", res1);
			}
			
			
		}
	}
	
	@Test
	public void getCachedLatentValue() {
		for (int i = 0; i < 100; i++) {
			this.element.setName("val");
			
			options.set(null, null, 1, randValue(), (p,v) -> res1 = v);

			if (res1.isDetermined()) {
				validateInstant("instant", res1);
			} else {
				validateLatent("val", res1);
				
				this.element.setName("changed");				
				assertNotEquals("changed", res1.getValue());
			}
			
		}
	}
	
	
	@Test
	public void getRepeatedLatentValue() {
		for (int i = 0; i < 100; i++) {
			this.element.setName("val");
			
			options.set(null, null, 1, randValue(), (p,v) -> res1 = v);

			if (res1.isDetermined()) {
				validateInstant("instant", res1);
			} else {
				validateLatent("val", res1);
				
				this.element.setName("changed");				
				assertNotEquals("changed", res1.getValue());
			}

			
			this.element.setName("val2");
			
			options.set(null, null, 1, randValue(), (p,v) -> res1 = v);

			if (res1.isDetermined()) {
				validateInstant("instant", res1);
			} else {
				validateLatent("val2", res1);
				
				this.element.setName("changed");				
				assertNotEquals("changed", res1.getValue());
			}
			
			
		}
	}
	
	private void validateInstant(String expected, ValueProvider<String> provider) {
		assertTrue(provider.isDetermined());
		assertEquals(expected, provider.getValue());
	}
	
	private void validateLatent(String expected, ValueProvider<String> provider) {
		assertFalse(provider.isDetermined());
		assertEquals(expected, provider.getValue());
	}
	
	
	private Random rand = new Random(42);

	protected double randValue() {
		return rand.nextDouble();
	}
	
	
	protected MultipleModelOptionsStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> createOptions() {
		return new ShareBasedMultipleModelOptionsStep<>(Map.of(instantStep, 0.5, latentStep, 0.5));
	}
}
