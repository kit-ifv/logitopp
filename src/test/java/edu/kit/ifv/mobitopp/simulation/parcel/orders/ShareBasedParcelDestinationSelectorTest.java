package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ParcelOrderStep;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ShareBasedParcelDestinationSelector;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class ShareBasedParcelDestinationSelectorTest {
	private Zone surveyZone;
	private Zone nonSurveyZone;
	
	private Predicate<Zone> workZoneFilter;
	
	private PickUpParcelPerson workInsidePerson;
	private PickUpParcelPerson workOutsidePerson;
	private PickUpParcelPerson noWorkPerson;
	
	private Map<ParcelDestinationType, Double> onlyWorkShares = Map.of(HOME, 0.0, WORK, 1.0, PACK_STATION, 0.0);
	private Map<ParcelDestinationType, Double> onlyHomeShares = Map.of(HOME, 1.0, WORK, 0.0, PACK_STATION, 0.0);
	private Map<ParcelDestinationType, Double> onlyStationShares = Map.of(HOME, 0.0, WORK, 0.0, PACK_STATION, 1.0);
	
		
	@BeforeEach
	public void setUp() {
		surveyZone = mock(Zone.class);
		nonSurveyZone = mock(Zone.class);
		
		workZoneFilter = zone -> (zone == surveyZone);
		
		workInsidePerson = mock(PickUpParcelPerson.class);
		when(workInsidePerson.fixedZoneFor(ActivityType.WORK)).thenReturn(surveyZone);
		when(workInsidePerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(true);
		
		workOutsidePerson = mock(PickUpParcelPerson.class);
		when(workOutsidePerson.fixedZoneFor(ActivityType.WORK)).thenReturn(nonSurveyZone);
		when(workOutsidePerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(true);
		
		noWorkPerson = mock(PickUpParcelPerson.class);
		when(noWorkPerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(false);
	}
	
	@Test
	public void equalSharesWorkingInside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertTrue(types.contains(HOME));
		assertTrue(types.contains(WORK));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void equalSharesWorkingOutside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void equalSharesNotWorking() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, noWorkPerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void customWorkSharesWorkingInside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyWorkShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertFalse(types.contains(HOME));
		assertTrue(types.contains(WORK));
		assertFalse(types.contains(PACK_STATION));
	}
	
	@Test
	public void customHomeSharesWorkingInside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyHomeShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertTrue(types.contains(HOME));
		assertFalse(types.contains(WORK));
		assertFalse(types.contains(PACK_STATION));
	}
	
	@Test
	public void customStationSharesWorkingInside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyStationShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertFalse(types.contains(HOME));
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void customHomeSharesWorkingOutside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyHomeShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
	}
	
	@Test
	public void customStationSharesWorkingOutside() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyStationShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void customHomeSharesNotWorking() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyHomeShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, noWorkPerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
	}
	
	@Test
	public void customStationSharesNotWorking() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyStationShares, workZoneFilter);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, noWorkPerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void equalSharesNoZoneFilter() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector();
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertTrue(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertTrue(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, noWorkPerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void customHomeSharesNoZoneFilter() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyHomeShares);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, noWorkPerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertTrue(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
	}
	
	@Test
	public void customStationSharesNoZoneFilter() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyStationShares);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, noWorkPerson, 10, rand);
		
		assertFalse(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertTrue(types.contains(PACK_STATION));
	}
	
	@Test
	public void customWorkSharesNoZoneFilter() {
		ParcelOrderStep<ParcelDestinationType> selector = new ShareBasedParcelDestinationSelector(onlyWorkShares);
		
		Random rand = new Random(42);
		
		List<ParcelDestinationType> types = selectNTypes(selector, workInsidePerson, 10, rand);
		
		assertTrue(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
		
		types = selectNTypes(selector, workOutsidePerson, 10, rand);
		
		assertTrue(types.contains(WORK));
		assertFalse(types.contains(HOME));
		assertFalse(types.contains(PACK_STATION));
	}
	
	private List<ParcelDestinationType> selectNTypes(ParcelOrderStep<ParcelDestinationType> selector, PickUpParcelPerson person, int n, Random rand) {
		List<ParcelDestinationType> types = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			ParcelBuilder builder = new ParcelBuilder();
			builder.setPerson(person);
			ParcelDestinationType type = selector.select(builder, emptyList(), 1, rand.nextDouble());
			types.add(type);
		}
		
		return types;
	}
}
