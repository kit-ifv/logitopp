package edu.kit.ifv.mobitopp.simulation.distribution.chains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.distribution.CEPServiceProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.DepotOperations;
import edu.kit.ifv.mobitopp.simulation.distribution.DepotRelationsParser;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenterParser;
import edu.kit.ifv.mobitopp.simulation.distribution.dispatch.TimeWindowDispatchStrategy;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.distribution.region.ServiceArea;
import edu.kit.ifv.mobitopp.simulation.distribution.region.ServiceAreaFactory;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.Connection;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTable;
import edu.kit.ifv.mobitopp.simulation.distribution.timetable.TimeTableParser;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.TourPlanningStrategy;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated.CostFunction;
import edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated.StaticTransferTimeModel;
import edu.kit.ifv.mobitopp.time.Time;

public class TestChainTimeTableIntegration {
	
	private static final File DC_FILE 			= new File("src/test/resources/chains/distribution_centers.csv");
	private static final File HUB_FILE 			= new File("src/test/resources/chains/intermed_hubs.csv");
	private static final File RELATION_FILE 	= new File("src/test/resources/chains/depot_relations.csv");
	private static final File TIMETABLE_FILE 	= new File("src/test/resources/chains/time_table.csv");
	
	private ZoneRepository zoneRepo;
	private ImpedanceIfc impedance;
	private DeliveryResults results;
	private TourPlanningStrategy tourChainStrategy;
	private ParcelPolicyProvider policy;
	private List<Zone> zones;
	private ServiceAreaFactory serviceAreaFactory;

	@BeforeEach
	public void setUp() {
		zoneRepo = mock(ZoneRepository.class);
		results = mock(DeliveryResults.class);
		tourChainStrategy = mock(TourPlanningStrategy.class);
		policy = mock(ParcelPolicyProvider.class);
		
		impedance = mock(ImpedanceIfc.class);	
		when(impedance.getDistance(any(), any())).thenReturn(4.2f);
		when(impedance.getTravelTime(any(), any(), eq(StandardMode.TRUCK), any())).thenReturn(4.2f,  3.0f, 6.2f, 1.9f, 7.2f);
		when(impedance.getTravelTime(any(), any(), eq(StandardMode.PUBLICTRANSPORT), any())).thenReturn(14.1f,  13.0f, 16.6f, 13.5f, 17.3f);
		when(impedance.getTravelTime(any(), any(), eq(StandardMode.BIKE), any())).thenReturn(20.1f,  23.0f, 26.6f, 23.5f, 27.3f);
		
		Zone z328 = mock(Zone.class);
		when(z328.getId()).thenReturn(new ZoneId("328", 0));
		
		Zone z4136 = mock(Zone.class);
		when(z4136.getId()).thenReturn(new ZoneId("4136", 0));
		
		Zone z1028 = mock(Zone.class);
		when(z1028.getId()).thenReturn(new ZoneId("1028", 0));
		
		Zone z3835 = mock(Zone.class);
		when(z3835.getId()).thenReturn(new ZoneId("3835", 0));
		
		Zone z2019 = mock(Zone.class);
		when(z2019.getId()).thenReturn(new ZoneId("2019", 0));
		
		Zone z6413 = mock(Zone.class);
		when(z6413.getId()).thenReturn(new ZoneId("6413", 0));
		
		Zone z3954 = mock(Zone.class);
		when(z3954.getId()).thenReturn(new ZoneId("3954", 0));
		
		Zone z526 = mock(Zone.class);
		when(z526.getId()).thenReturn(new ZoneId("526", 0));
		
		Zone z415 = mock(Zone.class);
		when(z415.getId()).thenReturn(new ZoneId("415", 0));
		
		Zone z3918 = mock(Zone.class);
		when(z3918.getId()).thenReturn(new ZoneId("3918", 0));
		
		Zone z1824 = mock(Zone.class);
		when(z1824.getId()).thenReturn(new ZoneId("1824", 0));
		
		Zone z4133 = mock(Zone.class);
		when(z4133.getId()).thenReturn(new ZoneId("4133", 0));
		
		Zone z3826 = mock(Zone.class);
		when(z3826.getId()).thenReturn(new ZoneId("3826", 0));
 
		zones = List.of(z328, z4136, z1028, z3835, z2019, z6413, z3954, z526, z415, z3918, z1824, z4133, z3826);
		Map<String, Zone> zoneMap = new LinkedHashMap<>();
		zones.forEach(z -> zoneMap.put(z.getId().getExternalId(), z));
		
		when(zoneRepo.getByExternalId(Mockito.anyString())).thenAnswer(input -> zoneMap.get(input.getArgument(0)));
		
		createServiceAreaFactory();
	}
	
	private void createServiceAreaFactory() {
		serviceAreaFactory =
			new ServiceAreaFactory(zoneRepo, impedance) {
				@Override
				public ServiceArea fromIntCode(Zone zone, int code) {
					if (code >= 0) {
						return new ServiceArea(zones);
					} else {
						return ServiceArea.empty();
					}
				}
			};
	}
		
	@Test
	public void combineTimeTableAndChains() {
	
		DistributionCenterParser parser = new DistributionCenterParser(zoneRepo , 1.0, serviceAreaFactory);
		
		Collection<DistributionCenter> depots = new ArrayList<>();
		depots.addAll(parser.parse(DC_FILE));
		
		Collection<CEPServiceProvider> serviceProviders = new ArrayList<>(parser.getServiceProviders());
		depots.addAll(parser.parse(HUB_FILE));
		
		DepotRelationsParser relationParser = new DepotRelationsParser(depots);
		relationParser.parseRelations(RELATION_FILE);
		
		depots.forEach(d -> {
			new DepotOperations(
					tourChainStrategy,
					policy,
					new TimeWindowDispatchStrategy(),
					d,
					results,
					impedance);
		});
		
		Map<Integer, DistributionCenter> depotMap = new LinkedHashMap<>();
		depots.forEach(d -> depotMap.put(d.getId(), d));
		
		TimeTable timeTable = new TimeTableParser(depots).parse(TIMETABLE_FILE);
		
		
		Stream<Connection> conns = timeTable.getConnectionsOnDay(depotMap.get(12), depotMap.get(9), Time.start);
		System.out.println(conns.collect(Collectors.toList()));
		
		
		
		for (int k : depotMap.keySet()) {
			Collection<TransportChain> chains = depotMap.get(k).getRegionalStructure().getDeliveryChains();
			
			for (TransportChain chain : chains) {
				int maxTrips = chain.maxNumberOfTripsOnDayAfter(timeTable, Time.start);
				
				if (chain.getVehicleTypes().contains(VehicleType.TRAM)) {
					
					if (chain.size() < 3) {continue;}
					
					System.out.println("\n\n");
					System.out.println(chain + ": " + chain.getVehicleTypes());
					for (int i=1; i <= maxTrips; i++) {
						
						TimedTransportChain timedChain = new TimedTransportChainBuilder(chain, new CostFunction(impedance), new StaticTransferTimeModel())
															.useDurationsFromStats(timeTable, impedance, Time.start)
															.defaultDeparture(Time.start.plusHours(7).plusMinutes(45))
															.build();
						System.out.println("\n" + i + ":");
						System.out.println(timedChain);
						timedChain.bookConnections();
						
						assertEquals(maxTrips-i, chain.maxNumberOfTripsOnDayAfter(timeTable, Time.start));
					}

				} else {
					maxTrips /= (chain.lastMileVehicle().equals(VehicleType.BIKE)) ? 3 : 1;
					assertEquals(chain.last().getFleet().size(), maxTrips, ""+chain+": " + chain.getVehicleTypes());
				}
				
				
				
				
			}
		}
		
	}

}
