package edu.kit.ifv.mobitopp.simulation.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.Collection;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class BusinessParserTest {

	private BusinessParser parser;

	@BeforeEach
	public void setUp() {

		Zone zone = mock(Zone.class);
		ZoneRepository repo = mock(ZoneRepository.class);
		when(repo.getByExternalId(any())).thenReturn(zone);

		parser = new BusinessParser(repo, null, null) {
			protected ZoneAndLocation getZoneAndLocation(double x, double y, Zone zone) {
				return new ZoneAndLocation(
						zone,
						new Location(
								new Point2D.Double(x,y),
								0,
								0
						)
				);
			}

		};
	}
	
	@Test
	public void parser() {
		
		CsvFile file = CsvFile.createFrom("src/test/resources/businesses.csv");

		Collection<BusinessBuilder> businesses = parser.parse(file, 42);
		
		assertEquals(100, businesses.size());
	}
	
	@Test
	public void parserWithMissingTime() {
		
		CsvFile file = CsvFile.createFrom("src/test/resources/businesses_without_time.csv");

		Collection<BusinessBuilder> businesses = parser.parse(file, 42);
		
		assertEquals(100, businesses.size());
	}

}
