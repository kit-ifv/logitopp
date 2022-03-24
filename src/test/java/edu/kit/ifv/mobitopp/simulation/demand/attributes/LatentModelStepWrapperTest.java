package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ParcelAgent;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import lombok.Getter;
import lombok.Setter;

public class LatentModelStepWrapperTest {
	
	private ValueProvider<String> provider;
	private Element element;
	private LatentModelStepWarpper<?, ?, String> step;
	
	class Element {
		@Getter @Setter private String name;
		
		public Element(String name) {
			this.name = name;
		}

	}
	
	@BeforeEach
	public void setUp() {
		this.element = new Element("e");
		this.provider = new LatentValueProvider<>(() -> element.getName());
		
		ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String> s =
				new ParcelDemandModelStep<ParcelAgent, ParcelBuilder<ParcelAgent>, String>() {
			
					@Override
					public String select(ParcelBuilder<ParcelAgent> parcel,
							Collection<ParcelBuilder<ParcelAgent>> otherParcels, int numOfParcels,
							double randomNumber) {

						return element.getName();
					}
				};
				
		this.step = new LatentModelStepWarpper<>(s);
				
		this.step.set(null, null, 1, 1, (p,v) -> this.provider=v);
	}
	
	@Test
	public void getValue() {	
		assertEquals("e", this.provider.getValue());
	}
	
	@Test
	public void getLatentValue() {
		this.element.setName("z");
		assertEquals("z", this.provider.getValue());
		assertNotEquals("e", this.provider.getValue());
	}
	
	@Test
	public void getCashedLatentValue() {
		this.element.setName("z");
		assertEquals("z", this.provider.getValue());
		this.element.setName("!");
		assertEquals("z", this.provider.getValue());
		assertNotEquals("e", this.provider.getValue());
		assertNotEquals("!", this.provider.getValue());
	}
	
	@Test
	public void initialIsDetermined() {
		assertEquals(false, this.provider.isDetermined());
	}
	
	@Test
	public void isDeterminedAfterGet() {
		this.provider.getValue();
		assertEquals(true, this.provider.isDetermined());
	}

}
