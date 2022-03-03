package edu.kit.ifv.mobitopp.simulation.distribution.policies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.distribution.policies.DeliveryAttemptsPolicy;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelDeliveryPolicy;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.RecipientType;
import edu.kit.ifv.mobitopp.simulation.parcels.ParcelDestinationType;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;
import edu.kit.ifv.mobitopp.time.Time;

public class DeliveryAttemptsTest {
	private DeliveryAttemptsPolicy attemptsPolicy;
	private ParcelDeliveryPolicy<PrivateParcel> delagate;
	private PrivateParcel parcel1;
	private PrivateParcel parcel2;
	private PrivateParcel parcel3;

	@BeforeEach
	public void setUp() {
		delagate = (ParcelDeliveryPolicy<PrivateParcel>) mock(ParcelDeliveryPolicy.class);
		when(delagate.canDeliver(any(), any())).thenReturn(Optional.of(RecipientType.PERSONAL));
		when(delagate.updateParcelDelivery(any(), any())).thenReturn(false);
		
		parcel1 = mock(PrivateParcel.class);
		when(parcel1.getDeliveryAttempts()).thenReturn(1);
		when(parcel1.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		
		parcel2 = mock(PrivateParcel.class);
		when(parcel2.getDeliveryAttempts()).thenReturn(2);
		when(parcel2.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		
		parcel3 = mock(PrivateParcel.class);
		when(parcel3.getDeliveryAttempts()).thenReturn(3);
		when(parcel3.getDestinationType()).thenReturn(ParcelDestinationType.HOME);
		
				
		attemptsPolicy = new DeliveryAttemptsPolicy(delagate, 3);
	}
	
	@Test
	public void delegateCanDeliver() {
		Optional<RecipientType> res = attemptsPolicy.canDeliver(parcel1, Time.start);
		verify(delagate, times(1)).canDeliver(parcel1, Time.start);
		assertEquals(RecipientType.PERSONAL, res.get());
		
		res = attemptsPolicy.canDeliver(parcel2, Time.start.plusHours(42));
		verify(delagate, times(1)).canDeliver(parcel2, Time.start.plusHours(42));
		assertEquals(RecipientType.PERSONAL, res.get());
		
		res = attemptsPolicy.canDeliver(parcel3, Time.future);
		verify(delagate, times(1)).canDeliver(parcel3, Time.future);
		assertEquals(RecipientType.PERSONAL, res.get());
	}
	
	@Test
	public void noParcelUpdate() {
		boolean res = attemptsPolicy.updateParcelDelivery(parcel1, Time.start);
		verify(delagate, times(1)).updateParcelDelivery(parcel1, Time.start);
		verify(parcel1, times(0)).setDestinationType(any());
//		verify(parcel1, times(0)).setDeliveryService(any());
		verify(parcel1, times(0)).setDistributionCenter(any());
		verify(parcel1, times(0)).setPlannedArrivalDate(any());
		assertEquals(res, false);
		
		res = attemptsPolicy.updateParcelDelivery(parcel2, Time.start);
		verify(delagate, times(1)).updateParcelDelivery(parcel2, Time.start);
		verify(parcel2, times(0)).setDestinationType(any());
//		verify(parcel2, times(0)).setDeliveryService(any());
		verify(parcel2, times(0)).setDistributionCenter(any());
		verify(parcel2, times(0)).setPlannedArrivalDate(any());
		assertEquals(res, false);
	}
	
	@Test
	public void parcelUpdate() {
		boolean res = attemptsPolicy.updateParcelDelivery(parcel3, Time.start);
		verify(delagate, times(1)).updateParcelDelivery(parcel3, Time.start);
		verify(parcel3, times(1)).setDestinationType(ParcelDestinationType.PACK_STATION);
//		verify(parcel3, times(0)).setDeliveryService(any());
		verify(parcel3, times(0)).setDistributionCenter(any());
		verify(parcel3, times(0)).setPlannedArrivalDate(any());
		assertEquals(res, true);

	}
}
