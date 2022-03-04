package edu.kit.ifv.mobitopp.simulation.demand;

import static edu.kit.ifv.mobitopp.simulation.demand.attributes.RandomDateSelectorTest.assertDayPrecision;
import static edu.kit.ifv.mobitopp.simulation.demand.attributes.RandomDateSelectorTest.assertIn;
import static edu.kit.ifv.mobitopp.simulation.demand.attributes.RandomDateSelectorTest.assertMinutePrecision;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcelBuilder;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultParcelOrderModelTest {
	
	//Destination
	private Zone surveyZone;
	private Zone nonSurveyZone;
	private Predicate<Zone> workZoneFilter;
	private PickUpParcelPerson workInsidePerson;
	private PickUpParcelPerson workOutsidePerson;
	private PickUpParcelPerson noWorkPerson;
	private Map<ParcelDestinationType, Double> onlyWorkShares = Map.of(HOME, 0.0, WORK, 1.0, PACK_STATION, 0.0);
	private Map<ParcelDestinationType, Double> onlyHomeShares = Map.of(HOME, 1.0, WORK, 0.0, PACK_STATION, 0.0);
	private Map<ParcelDestinationType, Double> onlyStationShares = Map.of(HOME, 0.0, WORK, 0.0, PACK_STATION, 1.0);
		
	//Distribution Centers
	private DistributionCenter centerA;
	private DistributionCenter centerB;
	private Map<DistributionCenter, Double> sharesCenterA;
	private Map<DistributionCenter, Double> sharesCenterB;
	private List<DistributionCenter> centers;
	
	private Random random;
	private DeliveryResults results;
	
	@BeforeEach
	public void setUp() {
		//Destination
		surveyZone = mock(Zone.class);
		nonSurveyZone = mock(Zone.class);
		
		workZoneFilter = zone -> (zone == surveyZone);
		
		Household insideHousehold = mock(Household.class);
		when(insideHousehold.homeZone()).thenReturn(surveyZone);
		when(insideHousehold.homeLocation()).thenReturn(new Location(new Point2D.Double(1,1), 0, 0));
		
		Household outsideHousehold = mock(Household.class);
		when(outsideHousehold.homeZone()).thenReturn(nonSurveyZone);
		when(outsideHousehold.homeLocation()).thenReturn(new Location(new Point2D.Double(100,100), 0, 0));
		
		workInsidePerson = mock(PickUpParcelPerson.class);
		when(workInsidePerson.household()).thenReturn(insideHousehold);
		when(workInsidePerson.fixedZoneFor(ActivityType.WORK)).thenReturn(surveyZone);
		when(workInsidePerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(true);
		
		workOutsidePerson = mock(PickUpParcelPerson.class);
		when(workOutsidePerson.household()).thenReturn(outsideHousehold);
		when(workOutsidePerson.fixedZoneFor(ActivityType.WORK)).thenReturn(nonSurveyZone);
		when(workOutsidePerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(true);
		
		noWorkPerson = mock(PickUpParcelPerson.class);
		when(noWorkPerson.fixedDestinationFor(ActivityType.PICK_UP_PARCEL)).thenReturn(new Location(new Point2D.Float(1,1), 0, 0));
		when(noWorkPerson.household()).thenReturn(insideHousehold);
		when(noWorkPerson.hasFixedZoneFor(ActivityType.PICK_UP_PARCEL)).thenReturn(true);
		when(noWorkPerson.fixedZoneFor(ActivityType.PICK_UP_PARCEL)).thenReturn(surveyZone);
		when(noWorkPerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(false);
	
		
		//Distribution Centers
		centerA = mock(DistributionCenter.class);
		when(centerA.getRelativeShare()).thenReturn(0.5);
		centerB = mock(DistributionCenter.class);
		when(centerB.getRelativeShare()).thenReturn(0.5);
		sharesCenterA = Map.of(centerA, 1.0, centerB, 0.0);
		sharesCenterB = Map.of(centerA, 0.0, centerB, 1.0);
		centers = List.of(centerA, centerB);
		
		random = new Random(42);
		when(noWorkPerson.getNextRandom()).thenReturn(random.nextDouble());
		when(workInsidePerson.getNextRandom()).thenReturn(random.nextDouble());
		when(workOutsidePerson.getNextRandom()).thenReturn(random.nextDouble());
		
		results = mock(DeliveryResults.class);
	}


	@Test
	public void noWorkPersonDefaultModel() {
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = PrivateParcelDemandModelBuilder.defaultPrivateParcelModel(centers, results);
		
		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, noWorkPerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(PrivateParcelBuilder p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertIn(p.getArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void workInsidePersonDefaultModel() {
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = PrivateParcelDemandModelBuilder.defaultPrivateParcelModel(centers, results);
		
		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, workInsidePerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(PrivateParcelBuilder p: order) {
				assertIn(p.getArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void workOutsidePersonDefaultModel() {
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = PrivateParcelDemandModelBuilder.defaultPrivateParcelModel(centers, results);
		
		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(PrivateParcelBuilder p: order) {
				assertIn(p.getArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void workOutsidePersonDefaultModelWithZoneFilter() {
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = PrivateParcelDemandModelBuilder.defaultPrivateParcelModel(centers, workZoneFilter, results);
		
		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(PrivateParcelBuilder p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertIn(p.getArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void customModelStepsNoWork() {
		PrivateParcelDemandModelBuilder builder = PrivateParcelDemandModelBuilder.forPrivateParcels(results);
		builder.useNormalDistributionNumberSelector(5.0, 2.0, 2, 7);
		
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = builder
																			.equalParcelDestinationSelection(workZoneFilter)
																			.customSharesDistributionCenterSelection(sharesCenterA)
																			.useDistributionCenterAsProducer()
																			.randomArrivalMinuteSelection(Time.start.plusDays(1), Time.start.plusDays(4))
																			.build();
				
		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(2 <= order.size() && order.size() <= 7);
			
			for(PrivateParcelBuilder p: order) {
				assertNotEquals(WORK, p.getDestinationType().getValue());
				assertEquals(centerA, p.getDistributionCenter().getValue());
				assertIn(p.getArrivalDate(), Time.start.plusDays(1), Time.start.plusDays(4));
				assertMinutePrecision(p.getArrivalDate());
			}
			
			List<Time> times = order.stream().map(p -> p.getArrivalDate()).collect(Collectors.toList());
		
			assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		}
		
	}
	
	@Test
	public void customModelStepsWorkInside() {
		PrivateParcelDemandModelBuilder builder = PrivateParcelDemandModelBuilder.forPrivateParcels(results);
		builder.useNormalDistributionNumberSelector(5.0, 2.0, 2, 7);
		
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = builder
				.shareBasedParcelDestinationSelection(onlyHomeShares, workZoneFilter)
				.customSharesDistributionCenterSelection(sharesCenterA)
				.useDistributionCenterAsProducer()
				.randomArrivalMinuteSelection(Time.start.plusDays(1), Time.start.plusDays(4))
				.build();

		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(2 <= order.size() && order.size() <= 7);
			
			for(PrivateParcelBuilder p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertNotEquals(PACK_STATION, p.getDestinationType());
				assertEquals(HOME, p.getDestinationType().getValue());
				assertEquals(centerA, p.getDistributionCenter().getValue());
				assertIn(p.getArrivalDate(), Time.start.plusDays(1), Time.start.plusDays(4));
				assertMinutePrecision(p.getArrivalDate());
			}
			
			List<Time> times = order.stream().map(p -> p.getArrivalDate()).collect(Collectors.toList());
		
			assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		}
		
	}
	
	@Test
	public void customModelStepsWorkOutside() {
		PrivateParcelDemandModelBuilder builder = PrivateParcelDemandModelBuilder.forPrivateParcels(results);
		builder.useNormalDistributionNumberSelector(5.0, 2.0, 2, 7);
				
		ParcelDemandModel<PickUpParcelPerson, PrivateParcelBuilder> model = 
			builder.equalParcelDestinationSelection(workZoneFilter)
				   .customSharesDistributionCenterSelection(sharesCenterA)
				   .useDistributionCenterAsProducer()
				   .randomArrivalMinuteSelection(Time.start.plusDays(1), Time.start.plusDays(4))
				   .build();
		
		List<Collection<PrivateParcelBuilder>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<PrivateParcelBuilder> order : orders) {
			assertTrue(2 <= order.size() && order.size() <= 7);
			
			for(PrivateParcelBuilder p: order) {
				assertNotEquals(WORK, p.getDestinationType().getValue());
				assertEquals(centerA, p.getDistributionCenter().getValue());
				assertIn(p.getArrivalDate(), Time.start.plusDays(1), Time.start.plusDays(4));
				assertMinutePrecision(p.getArrivalDate());
			}
			
			List<Time> times = order.stream().map(p -> p.getArrivalDate()).collect(Collectors.toList());
		
			assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		}
		
	}
	
	private List<Collection<PrivateParcelBuilder>> generateNParcels(ParcelDemandModel<PickUpParcelPerson,PrivateParcelBuilder> model, PickUpParcelPerson person, int n) {
		List<Collection<PrivateParcelBuilder>> parcels = new ArrayList<>();
		
		for(int i = 0; i < n; i++) {
			parcels.add(model.createParcelDemand(person).stream().map(p -> (PrivateParcelBuilder) p).collect(Collectors.toList()));
		}
		
		return parcels;
 	}
	
}