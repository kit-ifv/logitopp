package edu.kit.ifv.mobitopp.simulation.demand.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;

public class LatentValueProviderTest {
	
	private LatentValueProvider<String> provider;
	private Element value;
	
	class Element {
		@Getter @Setter private String name;
		
		public Element(String name) {
			this.name = name;
		}

	}
	
	@BeforeEach
	public void setUp() {
		this.value = new Element("e");
		this.provider = new LatentValueProvider<>(() -> value.getName());
		
	}
	
	@Test
	public void getValue() {
		assertEquals("e", this.provider.getValue());
	}
	
	@Test
	public void getLatentValue() {
		this.value.setName("z");
		assertEquals("z", this.provider.getValue());
		assertNotEquals("e", this.provider.getValue());
	}
	
	@Test
	public void getCashedLatentValue() {
		this.value.setName("z");
		assertEquals("z", this.provider.getValue());
		this.value.setName("!");
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
