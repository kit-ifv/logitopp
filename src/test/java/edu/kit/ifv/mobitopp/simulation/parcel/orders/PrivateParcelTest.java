package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.parcels.BaseParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class PrivateParcelTest {

	private BaseParcel parcel0;
	private PrivateParcel parcel1_home_hh1_loc1;
	private PrivateParcel parcel2_home_hh1_loc1;
	private PrivateParcel parcel3_home_hh2_loc1;
	private PrivateParcel parcel4_home_hh3_loc2;
	private PrivateParcel parcel5_home_hh4_loc2;
	private PrivateParcel parcel6_home_hh4_loc2;
	private PrivateParcel parcel7_work_loc1;
	private PrivateParcel parcel8_work_loc2;
	private PrivateParcel parcel9_work_loc2;
	private PrivateParcel parcel10_work_loc3;
	private PrivateParcel parcel11_pack_loc1;
	private PrivateParcel parcel12_pack_loc1;
	private PrivateParcel parcel13_pack_loc2;
	private PrivateParcel parcel14_pack_loc2;
	private PrivateParcel parcel15_pack_loc3;
	
	private PickUpParcelPerson p1_hh1;
	private PickUpParcelPerson p2_hh1;
	private PickUpParcelPerson p3_hh2;
	private PickUpParcelPerson p4_hh3;
	private PickUpParcelPerson p5_hh4;
	private PickUpParcelPerson p6_hh4;
	
	private Household hh1;
	private Household hh2;
	private Household hh3;
	private Household hh4;
	
	private Location loc1;
	private Location loc2;
	private Location loc3;
	
	@BeforeEach
	public void setUp() {
		loc1 = new Location(new Point(1, 1), 0, 0);
		loc2 = new Location(new Point(2, 2), 0, 0);
		loc3 = new Location(new Point(3, 3), 0, 0);
		
		hh1 = mockHousehold(1, loc1);
		hh2 = mockHousehold(2, loc1);
		hh3 = mockHousehold(3, loc2);
		hh4 = mockHousehold(4, loc2);
		
		p1_hh1 = mockPerson(hh1);
		p2_hh1 = mockPerson(hh1);
		p3_hh2 = mockPerson(hh2);
		p4_hh3 = mockPerson(hh3);
		p5_hh4 = mockPerson(hh4);
		p6_hh4 = mockPerson(hh4);
		
		parcel0 = mock(BaseParcel.class);
		when(parcel0.getLocation()).thenReturn(loc1);
				
		parcel1_home_hh1_loc1 = mockParcel(HOME, loc1, p1_hh1);
		parcel2_home_hh1_loc1 = mockParcel(HOME, loc1, p2_hh1);
		parcel3_home_hh2_loc1 = mockParcel(HOME, loc1, p3_hh2);
		parcel4_home_hh3_loc2 = mockParcel(HOME, loc2, p4_hh3);
		parcel5_home_hh4_loc2 = mockParcel(HOME, loc2, p5_hh4);
		parcel6_home_hh4_loc2 = mockParcel(HOME, loc2, p6_hh4);
		parcel7_work_loc1 = mockParcel(WORK, loc1, p1_hh1);
		parcel8_work_loc2 = mockParcel(WORK, loc2, p2_hh1);
		parcel9_work_loc2 = mockParcel(WORK, loc2, p3_hh2);
		parcel10_work_loc3 = mockParcel(WORK, loc3, p4_hh3);
		parcel11_pack_loc1 = mockParcel(PACK_STATION, loc1, p3_hh2);
		parcel12_pack_loc1 = mockParcel(PACK_STATION, loc1, p1_hh1);
		parcel13_pack_loc2 = mockParcel(PACK_STATION, loc2, p6_hh4);
		parcel14_pack_loc2 = mockParcel(PACK_STATION, loc2, p4_hh3);
		parcel15_pack_loc3 = mockParcel(PACK_STATION, loc3, p2_hh1);
		
	}
	
	private Household mockHousehold(int id, Location loc) {
		Household household = mock(Household.class);
		when(household.getOid()).thenReturn(id);
		when(household.homeLocation()).thenReturn(loc);
		
		return household;
	}
	
	private PickUpParcelPerson mockPerson(Household household) {
		PickUpParcelPerson person = mock(PickUpParcelPerson.class);
		
		when(person.household()).thenReturn(household);
		
		return person;
	}
	
	private PrivateParcel mockParcel(ParcelDestinationType dest, Location loc, PickUpParcelPerson person) {
		PrivateParcel parcel = mock(PrivateParcel.class);
		when(parcel.getDestinationType()).thenReturn(dest);
		when(parcel.getLocation()).thenReturn(loc);
		when(parcel.getPerson()).thenReturn(person);
		when(parcel.canBeDeliveredTogether(Mockito.any())).thenCallRealMethod();
		
		return parcel;
	}
	
	@Test
	public void parcel1_home_hh1_loc1() {
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(null));
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel0));
		assertTrue(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertTrue(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel7_work_loc1));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel1_home_hh1_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel2_home_hh1_loc1() {
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(null));
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel0));
		assertTrue(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertTrue(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel7_work_loc1));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel2_home_hh1_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel3_home_hh2_loc1() {
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(null));
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel0));
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		
		assertTrue(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));	
		
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel7_work_loc1));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel3_home_hh2_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel4_home_hh3_loc2() {
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));
		
		assertTrue(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel7_work_loc1));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel4_home_hh3_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel5_home_hh4_loc2() {
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		
		assertTrue(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertTrue(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));	
		
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel7_work_loc1));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel5_home_hh4_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel6_home_hh4_loc2() {
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		
		assertTrue(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertTrue(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel7_work_loc1));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel6_home_hh4_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel7_work_loc1() {
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(null));
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel0));
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		
		assertTrue(parcel7_work_loc1.canBeDeliveredTogether(parcel7_work_loc1));	
		
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel8_work_loc2));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel9_work_loc2));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel7_work_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel8_work_loc2() {
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel7_work_loc1));	
		
		assertTrue(parcel8_work_loc2.canBeDeliveredTogether(parcel8_work_loc2));				
		assertTrue(parcel8_work_loc2.canBeDeliveredTogether(parcel9_work_loc2));
		
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel8_work_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel9_work_loc2() {
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel7_work_loc1));
		
		assertTrue(parcel9_work_loc2.canBeDeliveredTogether(parcel8_work_loc2));	
		assertTrue(parcel9_work_loc2.canBeDeliveredTogether(parcel9_work_loc2));
		
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel10_work_loc3));				
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel9_work_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel10_work_loc3() {
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(null));
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel0));
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel8_work_loc2));	
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel9_work_loc2));
		
		assertTrue(parcel10_work_loc3.canBeDeliveredTogether(parcel10_work_loc3));	
		
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel12_pack_loc1));				
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel10_work_loc3.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel11_pack_loc1() {
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(null));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel0));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel8_work_loc2));	
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel9_work_loc2));
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel10_work_loc3));	
		
		assertTrue(parcel11_pack_loc1.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertTrue(parcel11_pack_loc1.canBeDeliveredTogether(parcel12_pack_loc1));	
		
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel11_pack_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel12_pack_loc1() {
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(null));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel0));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel8_work_loc2));	
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel9_work_loc2));
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel10_work_loc3));	
		
		assertTrue(parcel12_pack_loc1.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertTrue(parcel12_pack_loc1.canBeDeliveredTogether(parcel12_pack_loc1));	
		
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel14_pack_loc2));				
		assertFalse(parcel12_pack_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel13_pack_loc2() {
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel8_work_loc2));	
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel9_work_loc2));
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel10_work_loc3));	
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel12_pack_loc1));	
		
		assertTrue(parcel13_pack_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertTrue(parcel13_pack_loc2.canBeDeliveredTogether(parcel14_pack_loc2));	
		
		assertFalse(parcel13_pack_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel14_pack_loc2() {
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(null));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel0));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel8_work_loc2));	
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel9_work_loc2));
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel10_work_loc3));	
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel12_pack_loc1));	
		
		assertTrue(parcel14_pack_loc2.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertTrue(parcel14_pack_loc2.canBeDeliveredTogether(parcel14_pack_loc2));	
		
		assertFalse(parcel14_pack_loc2.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
	@Test
	public void parcel15_pack_loc3() {
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(null));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel0));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel1_home_hh1_loc1));	
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel3_home_hh2_loc1));		
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel5_home_hh4_loc2));				
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel8_work_loc2));	
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel9_work_loc2));
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel10_work_loc3));	
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel11_pack_loc1));				
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel12_pack_loc1));	
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel13_pack_loc2));				
		assertFalse(parcel15_pack_loc3.canBeDeliveredTogether(parcel14_pack_loc2));	
		assertTrue(parcel15_pack_loc3.canBeDeliveredTogether(parcel15_pack_loc3));
	}
	
}
