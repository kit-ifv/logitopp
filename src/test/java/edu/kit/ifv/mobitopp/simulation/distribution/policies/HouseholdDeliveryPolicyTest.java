package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class HouseholdDeliveryPolicyTest {
	private HouseholdDeliveryPolicy householdPolicy;
	private ParcelDeliveryPolicy<PrivateParcel> delagate;
	private PrivateParcel parcel1;
	private PrivateParcel parcel2;
	private PrivateParcel parcel3;
	private PrivateParcel parcel4;
	private PrivateParcel parcel5;
	private PickUpParcelPerson person1;
	private PickUpParcelPerson person2;
	private PickUpParcelPerson person3;
	private PickUpParcelPerson person4;
	private Household household1;
	private Household household2;
	private Household household3;
	private ActivityIfc home;
	private ActivityIfc work;

	

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {	
		home = mock(ActivityIfc.class);
		when(home.activityType()).thenReturn(ActivityType.HOME);
		work = mock(ActivityIfc.class);
		when(work.activityType()).thenReturn(ActivityType.WORK);
		
		person1 = mock(PickUpParcelPerson.class);
		when(person1.currentActivity()).thenReturn(home);
		person2 = mock(PickUpParcelPerson.class);
		when(person2.currentActivity()).thenReturn(work);
		person3 = mock(PickUpParcelPerson.class);
		when(person3.currentActivity()).thenReturn(work);
		person4 = mock(PickUpParcelPerson.class);
		when(person4.currentActivity()).thenReturn(home);
		
		household1 = mock(Household.class);
		when(household1.persons()).thenReturn(Stream.of(person1));
		when(person1.household()).thenReturn(household1);
		household2 = mock(Household.class);
		when(household2.persons()).thenReturn(Stream.of(person2));
		when(person2.household()).thenReturn(household2);
		household3 = mock(Household.class);
		when(household3.persons()).thenReturn(Stream.of(person3, person4));
		when(person3.household()).thenReturn(household3);
		when(person4.household()).thenReturn(household3);
		
		
		parcel1 = mock(PrivateParcel.class);
		when(parcel1.getDeliveryAttempts()).thenReturn(1);
		when(parcel1.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		when(parcel1.getPerson()).thenReturn(person1);
		
		parcel2 = mock(PrivateParcel.class);
		when(parcel2.getDeliveryAttempts()).thenReturn(2);
		when(parcel2.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		when(parcel2.getPerson()).thenReturn(person2);
		
		parcel3 = mock(PrivateParcel.class);
		when(parcel3.getDeliveryAttempts()).thenReturn(1);
		when(parcel3.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		when(parcel3.getPerson()).thenReturn(person3);
		
		parcel4 = mock(PrivateParcel.class);
		when(parcel4.getDeliveryAttempts()).thenReturn(1);
		when(parcel4.getDestinationType()).thenReturn(ParcelDestinationType.WORK);
		when(parcel4.getPerson()).thenReturn(person1);
		
		delagate = (ParcelDeliveryPolicy<PrivateParcel>) mock(ParcelDeliveryPolicy.class);
		when(delagate.canDeliver(any(), any())).thenReturn(Optional.empty());
		when(delagate.canDeliver(eq(parcel5), any())).thenReturn(Optional.of(RecipientType.BUSINESS));
		when(delagate.updateParcelDelivery(any(), any())).thenReturn(false);
				
		householdPolicy = new HouseholdDeliveryPolicy(delagate);
	}
	
	@Test
	public void delegateUpdateParcel() {
		boolean res = householdPolicy.updateParcelDelivery(parcel1, Time.start);
		verify(delagate, times(1)).updateParcelDelivery(parcel1, Time.start);
		assertEquals(false, res);

		res = householdPolicy.updateParcelDelivery(parcel2, Time.start);
		verify(delagate, times(1)).updateParcelDelivery(parcel2, Time.start);
		assertEquals(false, res);
		
		res = householdPolicy.updateParcelDelivery(parcel3, Time.start);
		verify(delagate, times(1)).updateParcelDelivery(parcel3, Time.start);
		assertEquals(false, res);
	}
	
	@Test
	public void singlePersonHome() {
		Optional<RecipientType> res = householdPolicy.canDeliver(parcel1, Time.start);
		verify(delagate, times(1)).canDeliver(parcel1, Time.start);
		assertEquals(RecipientType.HOUSEHOLDMEMBER, res.get());
	}
	
	@Test
	public void singlePersonNotHome() {
		Optional<RecipientType> res = householdPolicy.canDeliver(parcel2, Time.start);
		verify(delagate, times(1)).canDeliver(parcel2, Time.start);
		assertTrue(res.isEmpty());
	}
	
	@Test
	public void otherHhMemberHome() {
		Optional<RecipientType> res = householdPolicy.canDeliver(parcel3, Time.start);
		verify(delagate, times(1)).canDeliver(parcel3, Time.start);
		assertEquals(RecipientType.HOUSEHOLDMEMBER, res.get());
	}
	
	@Test
	public void workParcel() {
		Optional<RecipientType> res = householdPolicy.canDeliver(parcel4, Time.start);
		verify(delagate, times(1)).canDeliver(parcel4, Time.start);
		assertTrue(res.isEmpty());
	}
	
	@Test 
	public void alreadyDelivered() {
		Optional<RecipientType> res = householdPolicy.canDeliver(parcel5, Time.start);
		verify(delagate, times(1)).canDeliver(parcel5, Time.start);
		assertEquals(RecipientType.BUSINESS, res.get());
	}
}
