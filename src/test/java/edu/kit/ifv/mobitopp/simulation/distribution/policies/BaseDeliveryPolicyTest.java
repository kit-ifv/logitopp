package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.HOME;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.PACK_STATION;
import static edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType.WORK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;

public class BaseDeliveryPolicyTest {

	private ParcelDeliveryPolicy<PrivateParcel> policy;
	private PrivateParcel parcelHome;
	private PrivateParcel parcelWork;
	private PrivateParcel parcelPackStation;
	
	private PickUpParcelPerson person;
	private ActivityIfc activity;
	
	@BeforeEach
	public void setUp() {
		policy = new BaseDeliveryPolicy();
		
		parcelHome = mock(PrivateParcel.class);
		parcelWork = mock(PrivateParcel.class);
		parcelPackStation = mock(PrivateParcel.class);
		
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
		
		assertTrue(policy.canDeliver(parcelHome, null).isPresent());
	}
	
	@Test
	public void canDeliverHomeWhileWorking() {
		personIsWorking();
		
		assertTrue(policy.canDeliver(parcelHome, null).isEmpty());
	}
	
	@Test
	public void canDeliverHomeWhileElsewhere() {
		personIsElsewhere();
		
		assertTrue(policy.canDeliver(parcelHome, null).isEmpty());
	}
	
	@Test
	public void canDeliverWorkWhileHome() {
		personIsHome();
		
		assertTrue(policy.canDeliver(parcelWork, null).isEmpty());
	}
	
	@Test
	public void canDeliverWorkWhileWorking() {
		personIsWorking();
		
		assertTrue(policy.canDeliver(parcelWork, null).isPresent());
	}
	
	@Test
	public void canDeliverWorkWhileElsewhere() {
		personIsElsewhere();
		
		assertTrue(policy.canDeliver(parcelWork, null).isEmpty());
	}
	
	@Test
	public void canDeliverPackstationWhileHome() {
		personIsHome();
		
		assertTrue(policy.canDeliver(parcelPackStation, null).isPresent());
	}
	
	@Test
	public void canDeliverPackstationWhileWorking() {
		personIsWorking();
		
		assertTrue(policy.canDeliver(parcelPackStation, null).isPresent());
	}
	
	@Test
	public void canDeliverPackstationWhileElsewhere() {
		personIsElsewhere();
		
		assertTrue(policy.canDeliver(parcelPackStation, null).isPresent());
	}
	
	@Test
	public void updateParcel() {
		assertFalse(policy.updateParcelDelivery(parcelHome, null));
		Mockito.verifyZeroInteractions(parcelHome);
		
		assertFalse(policy.updateParcelDelivery(parcelWork, null));
		Mockito.verifyZeroInteractions(parcelWork);
		
		assertFalse(policy.updateParcelDelivery(parcelPackStation, null));
		Mockito.verifyZeroInteractions(parcelPackStation);
	}


}
