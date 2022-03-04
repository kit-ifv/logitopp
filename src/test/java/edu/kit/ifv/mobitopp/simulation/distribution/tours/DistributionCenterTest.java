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
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.activityschedule.DeliveryActivityBuilder;
import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.parcels.BaseParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
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
				household(0, 0),
				household(1, 0),
				household(2, 1),
				household(3, 1),
				household(4, 2),
				household(5, 2),
				household(6, 2)
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
		
		distributionCenter = mock(DistributionCenter.class);
		when(distributionCenter.getAvailableParcels(Mockito.any())).thenReturn(parcels);
		when(distributionCenter.getDeliveryActivities(Mockito.any())).thenCallRealMethod();
	}
	
	
	@Test
	public void groupParcels() {
		List<DeliveryActivityBuilder> deliveries = distributionCenter.getDeliveryActivities(Time.start);
		verify(deliveries);
		
	}
	
	private void verify(List<DeliveryActivityBuilder> deliveries) {
		assertTrue(deliveries.stream().allMatch(d -> !d.getParcels().isEmpty()));
		
		for (DeliveryActivityBuilder d : deliveries) {
			assertEquals(1, d.getParcels().stream().map(IParcel::getLocation).distinct().count());
			
			if (d.getParcels().stream().anyMatch(p -> p instanceof PrivateParcel)) {
				assertTrue(d.getParcels().stream().allMatch(p -> p instanceof PrivateParcel));
				assertEquals(1, d.getParcels().stream().map(p -> ((PrivateParcel) p).getDestinationType()).distinct().count());
				
				if (d.getParcels().stream().anyMatch(p -> ((PrivateParcel) p).getDestinationType() == ParcelDestinationType.HOME )) {
					assertEquals(1, d.getParcels().stream().map(p -> ((PrivateParcel) p).getPerson().household()).distinct().count());
				}
				
			} else {
				assertTrue(d.getParcels().stream().allMatch(p -> !(p instanceof PrivateParcel) ));
			}
		
		}
		
		
	}
	
	
	
	private Household household(int id, int loc) {
		Household h = mock(Household.class);
		when(h.getOid()).thenReturn(id);
		when(h.homeLocation()).thenReturn(new Location(new Point(loc, 0), 0, 0));
		
		return h;
	}
	
	private PickUpParcelPerson person(int id, Household household) {
		PickUpParcelPerson p = mock(PickUpParcelPerson.class);
		when(p.household()).thenReturn(household);
		when(p.getOid()).thenReturn(id);
		
		return p;
	}

	private IParcel parcel() {		
		IParcel parcel;
		
		if(rand.nextBoolean()) {
			PrivateParcel p = mock(PrivateParcel.class);
			when(p.getDestinationType()).thenReturn(ParcelDestinationType.values()[rand.nextInt(3)]);
			when(p.getPerson()).thenReturn(persons.get(rand.nextInt(persons.size())));
			
			if (p.getDestinationType() == ParcelDestinationType.HOME) {
				Location l = p.getPerson().household().homeLocation();
				when(p.getLocation()).thenReturn(l);
			} else {
				when(p.getLocation()).thenReturn(new Location(new Point(rand.nextInt(3), 0), 0, 0));
			}
			
			parcel = p;
		} else {
			parcel = mock(BaseParcel.class);
			when(parcel.getLocation()).thenReturn(new Location(new Point(rand.nextInt(3), 0), 0, 0));
		}
		
		return parcel;
	}
}
