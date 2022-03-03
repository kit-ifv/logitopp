package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.parcels.BaseParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.BusinessParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.util.collections.CollectionsUtil;

public class PrivateParcelTest {

	private BaseParcel parcel0_loc1;
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
	private BusinessParcel parcel16_business_loc1;
	private BusinessParcel parcel17_business_loc1;
	private BusinessParcel parcel18_business_loc2;
	private BusinessParcel parcel19_business_loc3;

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
	public void setUp() throws NoSuchFieldException, SecurityException {
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

		parcel0_loc1 = mock(BaseParcel.class);
		when(parcel0_loc1.getLocation()).thenReturn(loc1);
		when(parcel0_loc1.couldBeDeliveredWith(Mockito.any())).thenCallRealMethod();
		when(parcel0_loc1.canBeDeliveredTogether(Mockito.any())).thenCallRealMethod();

		parcel1_home_hh1_loc1 = mockParcel(1, HOME, loc1, p1_hh1);
		parcel2_home_hh1_loc1 = mockParcel(2, HOME, loc1, p2_hh1);
		parcel3_home_hh2_loc1 = mockParcel(3, HOME, loc1, p3_hh2);
		parcel4_home_hh3_loc2 = mockParcel(4, HOME, loc2, p4_hh3);
		parcel5_home_hh4_loc2 = mockParcel(5, HOME, loc2, p5_hh4);
		parcel6_home_hh4_loc2 = mockParcel(6, HOME, loc2, p6_hh4);
		parcel7_work_loc1 = mockParcel(7, WORK, loc1, p1_hh1);
		parcel8_work_loc2 = mockParcel(8, WORK, loc2, p2_hh1);
		parcel9_work_loc2 = mockParcel(9, WORK, loc2, p3_hh2);
		parcel10_work_loc3 = mockParcel(10, WORK, loc3, p4_hh3);
		parcel11_pack_loc1 = mockParcel(11, PACK_STATION, loc1, p3_hh2);
		parcel12_pack_loc1 = mockParcel(12, PACK_STATION, loc1, p1_hh1);
		parcel13_pack_loc2 = mockParcel(13, PACK_STATION, loc2, p6_hh4);
		parcel14_pack_loc2 = mockParcel(14, PACK_STATION, loc2, p4_hh3);
		parcel15_pack_loc3 = mockParcel(15, PACK_STATION, loc3, p2_hh1);
		parcel16_business_loc1 = mockBusinessParcel(16, loc1);
		parcel17_business_loc1 = mockBusinessParcel(17, loc1);
		parcel18_business_loc2 = mockBusinessParcel(18, loc2);
		parcel19_business_loc3 = mockBusinessParcel(19, loc3);

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

	private PrivateParcel mockParcel(int id, ParcelDestinationType dest, Location loc, PickUpParcelPerson person)
			throws NoSuchFieldException, SecurityException {
		PrivateParcel parcel = mock(PrivateParcel.class);
		when(parcel.getDestinationType()).thenReturn(dest);
		when(parcel.getLocation()).thenReturn(loc);
		when(parcel.getPerson()).thenReturn(person);
		when(parcel.getOId()).thenReturn(id);
		when(parcel.couldBeDeliveredWith(Mockito.any())).thenCallRealMethod();
		when(parcel.canBeDeliveredTogether(Mockito.any())).thenCallRealMethod();

		return parcel;
	}

	private BusinessParcel mockBusinessParcel(int id, Location loc) {
		BusinessParcel parcel = mock(BusinessParcel.class);
		when(parcel.getLocation()).thenReturn(loc);
		when(parcel.getOId()).thenReturn(id);
		when(parcel.couldBeDeliveredWith(Mockito.any())).thenCallRealMethod();
		when(parcel.canBeDeliveredTogether(Mockito.any())).thenCallRealMethod();

		return parcel;
	}

	@Test
	public void parcel0_uni_directional() {
		assertFalse(parcel0_loc1.couldBeDeliveredWith(null));

		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));

		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));

		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel7_work_loc1));

		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel10_work_loc3));

		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel12_pack_loc1));

		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel15_pack_loc3));

		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertTrue(parcel0_loc1.couldBeDeliveredWith(parcel17_business_loc1));

		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel0_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel0_bi_directional() {
		assertFalse(parcel0_loc1.canBeDeliveredTogether(null));

		assertTrue(parcel0_loc1.canBeDeliveredTogether(parcel0_loc1));

		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel1_home_hh1_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel2_home_hh1_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel3_home_hh2_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel4_home_hh3_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel5_home_hh4_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel6_home_hh4_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel7_work_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel8_work_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel9_work_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel10_work_loc3));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel11_pack_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel12_pack_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel13_pack_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel14_pack_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel15_pack_loc3));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel16_business_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel17_business_loc1));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel18_business_loc2));
		assertFalse(parcel0_loc1.canBeDeliveredTogether(parcel19_business_loc3));
	}

	@Test
	public void parcel1_home_hh1_loc1() {
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertTrue(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertTrue(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));

		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel1_home_hh1_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel2_home_hh1_loc1() {
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertTrue(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertTrue(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));

		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel2_home_hh1_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel3_home_hh2_loc1() {
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));

		assertTrue(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));

		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel3_home_hh2_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel4_home_hh3_loc2() {
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));

		assertTrue(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));

		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel4_home_hh3_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel5_home_hh4_loc2() {
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));

		assertTrue(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertTrue(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));

		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel5_home_hh4_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel6_home_hh4_loc2() {
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));

		assertTrue(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertTrue(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));

		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel6_home_hh4_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel7_work_loc1() {
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));

		assertTrue(parcel7_work_loc1.couldBeDeliveredWith(parcel7_work_loc1));

		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel15_pack_loc3));

		assertTrue(parcel7_work_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertTrue(parcel7_work_loc1.couldBeDeliveredWith(parcel17_business_loc1));

		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel7_work_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel8_work_loc2() {
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel7_work_loc1));

		assertTrue(parcel8_work_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertTrue(parcel8_work_loc2.couldBeDeliveredWith(parcel9_work_loc2));

		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel17_business_loc1));

		assertTrue(parcel8_work_loc2.couldBeDeliveredWith(parcel18_business_loc2));

		assertFalse(parcel8_work_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel9_work_loc2() {
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel7_work_loc1));

		assertTrue(parcel9_work_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertTrue(parcel9_work_loc2.couldBeDeliveredWith(parcel9_work_loc2));

		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel17_business_loc1));

		assertTrue(parcel9_work_loc2.couldBeDeliveredWith(parcel18_business_loc2));

		assertFalse(parcel9_work_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel10_work_loc3() {
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(null));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel9_work_loc2));

		assertTrue(parcel10_work_loc3.couldBeDeliveredWith(parcel10_work_loc3));

		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel10_work_loc3.couldBeDeliveredWith(parcel18_business_loc2));
		assertTrue(parcel10_work_loc3.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel11_pack_loc1() {
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel10_work_loc3));

		assertTrue(parcel11_pack_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertTrue(parcel11_pack_loc1.couldBeDeliveredWith(parcel12_pack_loc1));

		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel11_pack_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel12_pack_loc1() {
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel10_work_loc3));

		assertTrue(parcel12_pack_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertTrue(parcel12_pack_loc1.couldBeDeliveredWith(parcel12_pack_loc1));

		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel12_pack_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel13_pack_loc2() {
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel12_pack_loc1));

		assertTrue(parcel13_pack_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertTrue(parcel13_pack_loc2.couldBeDeliveredWith(parcel14_pack_loc2));

		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel13_pack_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel14_pack_loc2() {
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel12_pack_loc1));

		assertTrue(parcel14_pack_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertTrue(parcel14_pack_loc2.couldBeDeliveredWith(parcel14_pack_loc2));

		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel14_pack_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel15_pack_loc3() {
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(null));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel14_pack_loc2));

		assertTrue(parcel15_pack_loc3.couldBeDeliveredWith(parcel15_pack_loc3));

		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel15_pack_loc3.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel16_business_loc1() {
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));

		assertTrue(parcel16_business_loc1.couldBeDeliveredWith(parcel7_work_loc1));

		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel15_pack_loc3));

		assertTrue(parcel16_business_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertTrue(parcel16_business_loc1.couldBeDeliveredWith(parcel17_business_loc1));

		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel16_business_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel17_business_loc1() {
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(null));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel6_home_hh4_loc2));

		assertTrue(parcel17_business_loc1.couldBeDeliveredWith(parcel7_work_loc1));

		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel9_work_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel15_pack_loc3));

		assertTrue(parcel17_business_loc1.couldBeDeliveredWith(parcel16_business_loc1));
		assertTrue(parcel17_business_loc1.couldBeDeliveredWith(parcel17_business_loc1));

		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel18_business_loc2));
		assertFalse(parcel17_business_loc1.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel18_business_loc2() {
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(null));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel7_work_loc1));

		assertTrue(parcel18_business_loc2.couldBeDeliveredWith(parcel8_work_loc2));
		assertTrue(parcel18_business_loc2.couldBeDeliveredWith(parcel9_work_loc2));

		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel10_work_loc3));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel17_business_loc1));

		assertTrue(parcel18_business_loc2.couldBeDeliveredWith(parcel18_business_loc2));

		assertFalse(parcel18_business_loc2.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcel19_business_loc3() {
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(null));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel0_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel1_home_hh1_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel2_home_hh1_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel3_home_hh2_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel4_home_hh3_loc2));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel5_home_hh4_loc2));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel6_home_hh4_loc2));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel7_work_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel8_work_loc2));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel9_work_loc2));

		assertTrue(parcel19_business_loc3.couldBeDeliveredWith(parcel10_work_loc3));

		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel11_pack_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel12_pack_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel13_pack_loc2));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel14_pack_loc2));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel15_pack_loc3));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel16_business_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel17_business_loc1));
		assertFalse(parcel19_business_loc3.couldBeDeliveredWith(parcel18_business_loc2));

		assertTrue(parcel19_business_loc3.couldBeDeliveredWith(parcel19_business_loc3));
	}

	@Test
	public void parcelSymmetricCouldDeliver() {
		List<IParcel> list = List.of(parcel1_home_hh1_loc1, parcel2_home_hh1_loc1, parcel3_home_hh2_loc1,
				parcel4_home_hh3_loc2, parcel5_home_hh4_loc2, parcel6_home_hh4_loc2, parcel7_work_loc1,
				parcel8_work_loc2, parcel9_work_loc2, parcel10_work_loc3, parcel11_pack_loc1, parcel12_pack_loc1,
				parcel13_pack_loc2, parcel14_pack_loc2, parcel15_pack_loc3, parcel16_business_loc1,
				parcel17_business_loc1, parcel18_business_loc2, parcel19_business_loc3);

		for (IParcel parcel : list) {
			for (IParcel other : list) {
				assertEquals(parcel.couldBeDeliveredWith(other), other.couldBeDeliveredWith(parcel));
				assertEquals(parcel.canBeDeliveredTogether(other), parcel.couldBeDeliveredWith(other));
			}
		}
	}

	@Test
	public void parcelSymmetricCanDeliver() {
		List<IParcel> list = List.of(parcel0_loc1, parcel1_home_hh1_loc1, parcel2_home_hh1_loc1, parcel3_home_hh2_loc1,
				parcel4_home_hh3_loc2, parcel5_home_hh4_loc2, parcel6_home_hh4_loc2, parcel7_work_loc1,
				parcel8_work_loc2, parcel9_work_loc2, parcel10_work_loc3, parcel11_pack_loc1, parcel12_pack_loc1,
				parcel13_pack_loc2, parcel14_pack_loc2, parcel15_pack_loc3, parcel16_business_loc1,
				parcel17_business_loc1, parcel18_business_loc2, parcel19_business_loc3);

		for (IParcel parcel : list) {
			for (IParcel other : list) {
				assertEquals(parcel.canBeDeliveredTogether(other), parcel.canBeDeliveredTogether(other));
				boolean could1 = parcel.couldBeDeliveredWith(other);
				boolean could2 = other.couldBeDeliveredWith(parcel);
				boolean can = parcel.canBeDeliveredTogether(other);

				assertEquals(could1 && could2, can);
			}
		}
	}

	@Test
	public void groupDeliveries() {
		List<IParcel> list = List.of(parcel0_loc1, parcel1_home_hh1_loc1, parcel2_home_hh1_loc1, parcel3_home_hh2_loc1,
				parcel4_home_hh3_loc2, parcel5_home_hh4_loc2, parcel6_home_hh4_loc2, parcel7_work_loc1,
				parcel8_work_loc2, parcel9_work_loc2, parcel10_work_loc3, parcel11_pack_loc1, parcel12_pack_loc1,
				parcel13_pack_loc2, parcel14_pack_loc2, parcel15_pack_loc3, parcel16_business_loc1,
				parcel17_business_loc1, parcel18_business_loc2, parcel19_business_loc3);
		List<Integer> nums = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

		String res = CollectionsUtil.groupBy(nums, (i, j) -> list.get(i).canBeDeliveredTogether(list.get(j)))
				.toString();
		assertEquals("[[0], [1, 2], [3], [4], [5, 6], [7, 16, 17], [8, 9, 18], [10, 19], [11, 12], [13, 14], [15]]", res);
	}
}
