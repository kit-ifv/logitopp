package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static edu.kit.ifv.mobitopp.simulation.parcel.orders.RandomDeliveryDateSelectorTest.assertDayPrecision;
import static edu.kit.ifv.mobitopp.simulation.parcel.orders.RandomDeliveryDateSelectorTest.assertIn;
import static edu.kit.ifv.mobitopp.simulation.parcel.orders.RandomDeliveryDateSelectorTest.assertMinutePrecision;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static edu.kit.ifv.mobitopp.simulation.parcels.orders.RandomDeliveryDateSelector.MINUTE_PRECISION;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.DefaultParcelOrderModel;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.NormalDistributedNumberOfParcelsSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ParcelOrderModel;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.RandomDeliveryDateSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ShareBasedDeliveryServiceSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ShareBasedDistributionCenterSelector;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.ShareBasedParcelDestinationSelector;
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
	
	//Delivery Service
	private String serviceA;
	private String serviceB;
	private Map<String, Double> sharesServiceA;
	private Map<String, Double> sharesServiceB;
	private List<String> services;
	
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
		
		workInsidePerson = mock(PickUpParcelPerson.class);
		when(workInsidePerson.fixedZoneFor(ActivityType.WORK)).thenReturn(surveyZone);
		when(workInsidePerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(true);
		
		workOutsidePerson = mock(PickUpParcelPerson.class);
		when(workOutsidePerson.fixedZoneFor(ActivityType.WORK)).thenReturn(nonSurveyZone);
		when(workOutsidePerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(true);
		
		noWorkPerson = mock(PickUpParcelPerson.class);
		when(noWorkPerson.hasFixedZoneFor(ActivityType.WORK)).thenReturn(false);
		
		
		//Deliery Services
		serviceA = "ServiceA";
		serviceB = "ServiceB";
		sharesServiceA = Map.of(serviceA, 1.0, serviceB, 0.0);
		sharesServiceB = Map.of(serviceA, 0.0, serviceB, 1.0);
		services = List.of(serviceA, serviceB);
		
		
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
		ParcelOrderModel model = new DefaultParcelOrderModel(centers);
		
		List<Collection<Parcel>> orders = generateNParcels(model, noWorkPerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(Parcel p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertEquals("Dummy Delivery Service", p.getDeliveryService());
				assertIn(p.getPlannedArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getPlannedArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void workInsidePersonDefaultModel() {
		ParcelOrderModel model = new DefaultParcelOrderModel(centers);
		
		List<Collection<Parcel>> orders = generateNParcels(model, workInsidePerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(Parcel p: order) {
				assertEquals("Dummy Delivery Service", p.getDeliveryService());
				assertIn(p.getPlannedArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getPlannedArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void workOutsidePersonDefaultModel() {
		ParcelOrderModel model = new DefaultParcelOrderModel(centers);
		
		List<Collection<Parcel>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(Parcel p: order) {
				assertEquals("Dummy Delivery Service", p.getDeliveryService());
				assertIn(p.getPlannedArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getPlannedArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void workOutsidePersonDefaultModelWithZoneFilter() {
		ParcelOrderModel model = new DefaultParcelOrderModel(centers, workZoneFilter);
		
		List<Collection<Parcel>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(0 <= order.size() && order.size() <= 10);
			
			for(Parcel p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertEquals("Dummy Delivery Service", p.getDeliveryService());
				assertIn(p.getPlannedArrivalDate(), Time.start, Time.start.plusDays(6));
				assertDayPrecision(p.getPlannedArrivalDate());
			}
			
		}
		
	}
	
	@Test
	public void customModelStepsNoWork() {
		ParcelOrderModel model = new DefaultParcelOrderModel(
			new NormalDistributedNumberOfParcelsSelector(5.0, 2.0, 2, 7),
			new ShareBasedParcelDestinationSelector(workZoneFilter),
			new ShareBasedDistributionCenterSelector(sharesCenterA),
			new ShareBasedDeliveryServiceSelector(sharesServiceB),
			new RandomDeliveryDateSelector(Time.start.plusDays(1), Time.start.plusDays(4), MINUTE_PRECISION)
		);
		
		List<Collection<Parcel>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(2 <= order.size() && order.size() <= 7);
			
			for(Parcel p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertEquals("ServiceB", p.getDeliveryService());
				assertEquals(centerA, p.getDistributionCenter());
				assertIn(p.getPlannedArrivalDate(), Time.start.plusDays(1), Time.start.plusDays(4));
				assertMinutePrecision(p.getPlannedArrivalDate());
			}
			
			List<Time> times = order.stream().map(p -> p.getPlannedArrivalDate()).collect(Collectors.toList());
		
			assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		}
		
	}
	
	@Test
	public void customModelStepsWorkInside() {
		ParcelOrderModel model = new DefaultParcelOrderModel(
			new NormalDistributedNumberOfParcelsSelector(5.0, 2.0, 2, 7),
			new ShareBasedParcelDestinationSelector(onlyHomeShares, workZoneFilter),
			new ShareBasedDistributionCenterSelector(sharesCenterA),
			new ShareBasedDeliveryServiceSelector(sharesServiceB),
			new RandomDeliveryDateSelector(Time.start.plusDays(1), Time.start.plusDays(4), MINUTE_PRECISION)
		);
		
		List<Collection<Parcel>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(2 <= order.size() && order.size() <= 7);
			
			for(Parcel p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertNotEquals(PACK_STATION, p.getDestinationType());
				assertEquals(HOME, p.getDestinationType());
				assertEquals("ServiceB", p.getDeliveryService());
				assertEquals(centerA, p.getDistributionCenter());
				assertIn(p.getPlannedArrivalDate(), Time.start.plusDays(1), Time.start.plusDays(4));
				assertMinutePrecision(p.getPlannedArrivalDate());
			}
			
			List<Time> times = order.stream().map(p -> p.getPlannedArrivalDate()).collect(Collectors.toList());
		
			assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		}
		
	}
	
	@Test
	public void customModelStepsWorkOutside() {
		ParcelOrderModel model = new DefaultParcelOrderModel(
			new NormalDistributedNumberOfParcelsSelector(5.0, 2.0, 2, 7),
			new ShareBasedParcelDestinationSelector(workZoneFilter),
			new ShareBasedDistributionCenterSelector(sharesCenterA),
			new ShareBasedDeliveryServiceSelector(sharesServiceB),
			new RandomDeliveryDateSelector(Time.start.plusDays(1), Time.start.plusDays(4), MINUTE_PRECISION)
		);
		
		List<Collection<Parcel>> orders = generateNParcels(model, workOutsidePerson, 10);
		
		for(Collection<Parcel> order : orders) {
			assertTrue(2 <= order.size() && order.size() <= 7);
			
			for(Parcel p: order) {
				assertNotEquals(WORK, p.getDestinationType());
				assertEquals("ServiceB", p.getDeliveryService());
				assertEquals(centerA, p.getDistributionCenter());
				assertIn(p.getPlannedArrivalDate(), Time.start.plusDays(1), Time.start.plusDays(4));
				assertMinutePrecision(p.getPlannedArrivalDate());
			}
			
			List<Time> times = order.stream().map(p -> p.getPlannedArrivalDate()).collect(Collectors.toList());
		
			assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
			assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		}
		
	}
	
	private List<Collection<Parcel>> generateNParcels(ParcelOrderModel model, PickUpParcelPerson person, int n) {
		List<Collection<Parcel>> parcels = new ArrayList<>();
		
		for(int i = 0; i < n; i++) {
			parcels.add(model.createParcelOrders(person, results));
		}
		
		return parcels;
 	}
	
}
