package edu.kit.ifv.mobitopp.simulation.distribution.tours;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ParcelActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.clustering.LinkDeliveryClustering;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class DistributionCenterTest  {
	
	private DistributionCenter distributionCenter;
	private List<Household> households;
	private List<PickUpParcelPerson> persons;
	private List<IParcel> parcels;
	private Random rand;
	
	@BeforeEach
	public void setUp() {
		rand = new Random(42);
		
		households = List.of(
				household(0, 0,  1, 0.2),
				household(1, 0, -1, 0.4),
				household(2, 0,  1, 0.3),
				household(3, 1,  2, 0.5),
				household(4, 2,  3, 0.1),
				household(5, 2, -3, 0.9),
				household(6, 2,  4, 0.3)
		);
		
		persons = List.of(
				person(0, households.get(0)),
				person(1, households.get(0)),
				person(2, households.get(1)),
				person(3, households.get(2)),
				person(4, households.get(2)),
				person(5, households.get(3)),
				person(6, households.get(4)),
				person(7, households.get(5)),
				person(8, households.get(5)),
				person(9, households.get(6))
		);
		
		parcels = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			parcels.add(parcel());
		}
		
		
		distributionCenter = new DistributionCenter("test", "org", mock(Zone.class), mock(Location.class), 100, 0.8, 0.5, 3);
		parcels.forEach(distributionCenter::addParcel);
		
//		distributionCenter = mock(DistributionCenter.class);
//		when(distributionCenter.getAvailableParcels(Mockito.any())).thenReturn(parcels);
//		when(distributionCenter.getDeliveryActivities(Mockito.any())).thenCallRealMethod();
//		doCallRealMethod().when(distributionCenter).setClusteringStrategy(Mockito.any());
		
		distributionCenter.setClusteringStrategy(new LinkDeliveryClustering());
	}
	
	
	@Test
	public void groupParcels() {
		List<ParcelActivityBuilder> deliveries = distributionCenter.getDeliveryActivities(Time.future);
		verify(deliveries);
		
	}
	
	private void verify(List<ParcelActivityBuilder> deliveries) {
		assertTrue(deliveries.stream().allMatch(d -> !d.getParcels().isEmpty()));
		
		for (ParcelActivityBuilder d : deliveries) {
			assertEquals(1, d.getParcels().stream().map(IParcel::getLocation).map(Location::roadAccessEdgeId).map(Math::abs).distinct().count());
			
			assertEquals( Math.abs(d.getLocation().roadAccessEdgeId()), Math.abs(d.getParcels().get(0).getLocation().roadAccessEdgeId()) );
			System.out.println(d.getLocation());
			
//			if (d.getParcels().stream().anyMatch(p -> p instanceof PrivateParcel)) {
//				assertTrue(d.getParcels().stream().allMatch(p -> p instanceof PrivateParcel));
//				assertEquals(1, d.getParcels().stream().map(p -> ((PrivateParcel) p).getDestinationType()).distinct().count());
//				
//				if (d.getParcels().stream().anyMatch(p -> ((PrivateParcel) p).getDestinationType() == ParcelDestinationType.HOME )) {
//					assertEquals(1, d.getParcels().stream().map(p -> ((PrivateParcel) p).getPerson().household()).distinct().count());
//				}
//				
//			} else {
//				assertTrue(d.getParcels().stream().allMatch(p -> !(p instanceof PrivateParcel) ));
//			}
		
		}
		
		
	}
	
	
	
	private Household household(int id, int loc, int link, double pos) {
		Household h = mock(Household.class);
		when(h.getOid()).thenReturn(id);
		when(h.homeLocation()).thenReturn(new Location(new Point(loc, 0), link, pos));
		
		return h;
	}
	
	private PickUpParcelPerson person(int id, Household household) {
		PickUpParcelPerson p = mock(PickUpParcelPerson.class);
		when(p.household()).thenReturn(household);
		when(p.getOid()).thenReturn(id);
		
		return p;
	}

	private IParcel parcel() {		
		PrivateParcel p = mock(PrivateParcel.class);
		when(p.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		when(p.getPerson()).thenReturn(persons.get(rand.nextInt(persons.size())));
		when(p.getPlannedArrivalDate()).thenReturn(Time.future);

		Location l = p.getPerson().household().homeLocation();
		when(p.getLocation()).thenReturn(l);

		Zone z = mock(Zone.class);
		when(p.getZoneAndLocation()).thenReturn(new ZoneAndLocation(z, l));
		
		return p;
	}
}
