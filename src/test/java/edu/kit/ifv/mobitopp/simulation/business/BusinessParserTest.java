package edu.kit.ifv.mobitopp.simulation.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class BusinessParserTest {
	
	@Test
	public void parser() {
		
		CsvFile file = CsvFile.createFrom("src/test/resources/businesses.csv");
		
		Zone zone = mock(Zone.class);
		ZoneRepository repo = mock(ZoneRepository.class);
		when(repo.getByExternalId(any())).thenReturn(zone);
		
		BusinessParser parser = new BusinessParser(repo);
		Collection<BusinessBuilder> businesses = parser.parse(file, 42);
		
		assertEquals(100, businesses.size());
	}

}
