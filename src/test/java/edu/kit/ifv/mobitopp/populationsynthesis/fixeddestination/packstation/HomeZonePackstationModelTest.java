package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public class HomeZonePackstationModelTest {

	private PersonBuilder person;
	private Zone homeZone;
	
	
	@BeforeEach
	public void setUp() {
		this.person = mock(PersonBuilder.class);
		this.homeZone = mock(Zone.class);
		
		when(person.homeZone()).thenReturn(homeZone);
	}
	
	@Test
	public void homeZonePackStationModel() {
		Zone selected = new HomeZonePackstationModel().select(person);
		
		assertEquals(homeZone, selected);
	}
}
