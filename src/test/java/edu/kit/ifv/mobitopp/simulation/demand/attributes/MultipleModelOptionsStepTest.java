package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import lombok.Getter;
import lombok.Setter;

public class MultipleModelOptionsStepTest {
	
	class Element {
		@Getter @Setter private String name;
		
		public Element(String name) {
			this.name = name;
		}

	}
	
	protected Element element;
	protected ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> instantStep;
	protected ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> latentStep;
	protected MultipleModelOptionsStep<?,?,String> options;
	
	protected ValueProvider<String> res1;
	protected ValueProvider<String> res2;

	
	@BeforeEach
	private void setup() {
		instantStep = createInstant();
		latentStep = createLatent();
		options = createOptions();
		
		element = new Element("latent");
		
		options.set(null, List.of(), 1, randValue(), (p, v) -> res1 = v);
		options.set(null, List.of(), 1, randValue(), (p,v) -> res2 = v);
	}
	
	@Test
	public void getValues() {
		assertTrue(this.res1.isDetermined());
		assertEquals("instant", this.res1.getValue());
		assertFalse(this.res2.isDetermined());
		assertEquals("latent",  this.res2.getValue());
	}
	
	
	@Test
	public void getLatentValue() {
		this.element.setName("changed");
		assertFalse(this.res2.isDetermined());
		assertEquals("changed", this.res2.getValue());
		assertNotEquals("latent", this.res2.getValue());
	}
	
	@Test
	public void getCachedLatentValue() {
		this.element.setName("changed");
		assertEquals("changed", this.res2.getValue());
		this.element.setName("!");
		assertEquals("changed", this.res2.getValue());
		assertNotEquals("latent", this.res2.getValue());
		assertNotEquals("!", this.res2.getValue());
	}
	
	@Test
	public void getRepeatedLatentValue() {
		this.element.setName("changed");
		assertEquals("changed", this.res2.getValue());
		this.element.setName("!");
		assertEquals("changed", this.res2.getValue());
		assertNotEquals("latent", this.res2.getValue());
		assertNotEquals("!", this.res2.getValue());
		
		options.set(null, List.of(), 2, 2, (p,v) -> res1 = v);
		options.set(null, List.of(), 2, 2, (p,v) -> res2 = v);
		
		assertTrue(this.res1.isDetermined());
		assertFalse(this.res2.isDetermined());
		assertEquals("instant", this.res1.getValue());
		assertEquals("!", this.res2.getValue());
		
		this.element.setName("changed");
		assertEquals("!", this.res2.getValue());
		assertNotEquals("latent", this.res2.getValue());
		assertNotEquals("changed", this.res2.getValue());
	}
	
	@Test
	public void unsupportedSelect() {
		assertThrows(UnsupportedOperationException.class,
				() -> {
						this.options.select(null, List.of(), 0, randValue());
					}
			);
	}
	
	protected double randValue() {
		return 0;
	}

	@Test
	public void determinePreSimulation() {
		assertThrows(UnsupportedOperationException.class,
				() -> {
						this.options.determinePreSimulation(null, List.of(), 0, randValue());
					}
			);
	}
	
	
	
	private ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> createInstant() {
		return new ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String>() {
		
			@Override
			public String select(ParcelBuilder<ParcelAgent> parcel,
					Collection<ParcelBuilder<ParcelAgent>> otherParcels, int numOfParcels,
					double randomNumber) {
	
				return "instant";
			}
		};
	}
	

	private ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> createLatent() {
		return new ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String>() {
		
			@Override
			public boolean determinePreSimulation(ParcelBuilder<ParcelAgent> parcel,
					Collection<ParcelBuilder<ParcelAgent>> otherParcels, int numOfParcels, double randomNumber) {
				return false;
			}
			
			@Override
			public String select(ParcelBuilder<ParcelAgent> parcel,
					Collection<ParcelBuilder<ParcelAgent>> otherParcels, int numOfParcels,
					double randomNumber) {

				return element.getName();
			}
		};
	}

	protected MultipleModelOptionsStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> createOptions() {
		return new MultipleModelOptionsStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String>(instantStep, latentStep) {
		
			private int  i = -1;

			@Override
			protected ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> selectModelStep(
					Collection<ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String>> steps,
					double randomNumber) {
				
				i = (i+1) % steps.size();
				
				return new ArrayList<>(steps).get(i);
			}
		};
	}

}
