package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestination.packstation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public class HomeZonePackstationModelTest {

	private Person person;
	private Zone homeZone;
	private Location centroid;

	
	@BeforeEach
	public void setUp() {
		this.person = mock(Person.class);
		this.homeZone = mock(Zone.class);
		this.centroid = mock(Location.class);

		when(person.homeZone()).thenReturn(homeZone);
		when(homeZone.centroidLocation()).thenReturn(centroid);
	}
	
	@Test
	public void homeZonePackStationModel() {
		ZoneAndLocation selected = new HomeZonePackstationModel().select(person);
		
		assertEquals(homeZone, selected.zone());
		assertEquals(centroid, selected.location());
	}
}
