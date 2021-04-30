package edu.kit.ifv.mobitopp.simulation.parcels.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.parcels.Parcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class BaseDeliveryPolicyTest {

	private ParcelDeliveryPolicy policy;
	private Parcel parcelHome;
	private Parcel parcelWork;
	private Parcel parcelPackStation;
	
	private PickUpParcelPerson person;
	private ActivityIfc activity;
	
	@BeforeEach
	public void setUp() {
		policy = new BaseDeliveryPolicy();
		
		parcelHome = mock(Parcel.class);
		parcelWork = mock(Parcel.class);
		parcelPackStation = mock(Parcel.class);
		
		when(parcelHome.getDestinationType()).thenReturn(HOME);
		when(parcelWork.getDestinationType()).thenReturn(WORK);
		when(parcelPackStation.getDestinationType()).thenReturn(PACK_STATION);
		
		person = Mockito.mock(PickUpParcelPerson.class);
		when(parcelHome.getPerson()).thenReturn(person);
		when(parcelWork.getPerson()).thenReturn(person);
		when(parcelPackStation.getPerson()).thenReturn(person);
		
		activity = mock(ActivityIfc.class);
		when(person.currentActivity()).thenReturn(activity);
	}
	
	private void personIsHome() {
		when(activity.activityType()).thenReturn(ActivityType.HOME);
	}
	
	private void personIsWorking() {
		when(activity.activityType()).thenReturn(ActivityType.WORK);
	}
	
	private void personIsElsewhere() {
		when(activity.activityType()).thenReturn(ActivityType.LEISURE);
	}

	
	@Test
	public void canDeliverHomeWhileHome() {
		personIsHome();
		
		assertTrue(policy.canDeliver(parcelHome));
	}
	
	@Test
	public void canDeliverHomeWhileWorking() {
		personIsWorking();
		
		assertTrue(!policy.canDeliver(parcelHome));
	}
	
	@Test
	public void canDeliverHomeWhileElsewhere() {
		personIsElsewhere();
		
		assertTrue(!policy.canDeliver(parcelHome));
	}
	
	@Test
	public void canDeliverWorkWhileHome() {
		personIsHome();
		
		assertTrue(!policy.canDeliver(parcelWork));
	}
	
	@Test
	public void canDeliverWorkWhileWorking() {
		personIsWorking();
		
		assertTrue(policy.canDeliver(parcelWork));
	}
	
	@Test
	public void canDeliverWorkWhileElsewhere() {
		personIsElsewhere();
		
		assertTrue(!policy.canDeliver(parcelWork));
	}
	
	@Test
	public void canDeliverPackstationWhileHome() {
		personIsHome();
		
		assertTrue(policy.canDeliver(parcelPackStation));
	}
	
	@Test
	public void canDeliverPackstationWhileWorking() {
		personIsWorking();
		
		assertTrue(policy.canDeliver(parcelPackStation));
	}
	
	@Test
	public void canDeliverPackstationWhileElsewhere() {
		personIsElsewhere();
		
		assertTrue(policy.canDeliver(parcelPackStation));
	}
	
	@Test
	public void updateParcel() {
		assertFalse(policy.updateParcelDelivery(parcelHome));
		Mockito.verifyZeroInteractions(parcelHome);
		
		assertFalse(policy.updateParcelDelivery(parcelWork));
		Mockito.verifyZeroInteractions(parcelWork);
		
		assertFalse(policy.updateParcelDelivery(parcelPackStation));
		Mockito.verifyZeroInteractions(parcelPackStation);
	}


}