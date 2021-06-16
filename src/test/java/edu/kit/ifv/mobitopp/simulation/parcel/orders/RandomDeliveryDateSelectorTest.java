package edu.kit.ifv.mobitopp.simulation.parcel.orders;

import static edu.kit.ifv.mobitopp.simulation.parcels.orders.RandomDeliveryDateSelector.HOUR_PRECISION;
import static edu.kit.ifv.mobitopp.simulation.parcels.orders.RandomDeliveryDateSelector.MINUTE_PRECISION;
import static edu.kit.ifv.mobitopp.simulation.parcels.orders.RandomDeliveryDateSelector.SECOND_PRECISION;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.parcels.orders.ParcelOrderStep;
import edu.kit.ifv.mobitopp.simulation.parcels.orders.RandomDeliveryDateSelector;
import edu.kit.ifv.mobitopp.time.Time;

public class RandomDeliveryDateSelectorTest {
	
	private static final Time MONDAY = Time.start;
	private static final Time WEDNESDAY = Time.start.plusDays(2);
	private static final Time FRIDAY = Time.start.plusDays(4);
	private static final Time SUNDAY = MONDAY.plusDays(6);

	@Test
	public void defaultSelector() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector();
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertDayPrecision(t);
			assertIn(t, MONDAY, SUNDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));		
	}
	
	@Test
	public void hourPrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(HOUR_PRECISION);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			
			assertHourPrecision(t);
			assertIn(t, MONDAY, SUNDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
		
	}	
	
	@Test
	public void minutePrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(MINUTE_PRECISION);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertMinutePrecision(t);
			assertIn(t, MONDAY, SUNDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		
	}
	
	@Test
	public void secondPrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(SECOND_PRECISION);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertIn(t, MONDAY, SUNDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getSecond).anyMatch(i -> i != 0));
		
	}
	
	@Test
	public void customIntervalDayPrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(WEDNESDAY, FRIDAY);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertDayPrecision(t);
			assertIn(t, WEDNESDAY, FRIDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));		
	}
	
	@Test
	public void customIntervalHourPrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(WEDNESDAY, FRIDAY, HOUR_PRECISION);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertHourPrecision(t);
			assertIn(t, WEDNESDAY, FRIDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
		
	}
	
	@Test
	public void customIntervalMinutePrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(WEDNESDAY, FRIDAY, MINUTE_PRECISION);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertMinutePrecision(t);
			assertIn(t, WEDNESDAY, FRIDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		
	}
	
	@Test
	public void customIntervalSecondPrecision() {
		ParcelOrderStep<Time> selector = new RandomDeliveryDateSelector(WEDNESDAY, FRIDAY, SECOND_PRECISION);
		
		Random rand = new Random(42);
		List<Time> times = seletNDates(selector, 100, rand);
		
		for(Time t : times) {
			assertIn(t, WEDNESDAY, FRIDAY);
		}
		
		assertTrue(times.stream().mapToInt(Time::getDay).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getHour).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getMinute).anyMatch(i -> i != 0));
		assertTrue(times.stream().mapToInt(Time::getSecond).anyMatch(i -> i != 0));
		
	}
	
	public static void assertDayPrecision(Time time) {
		assertEquals(0, time.getHour());
		assertEquals(0, time.getMinute());
		assertEquals(0, time.getSecond());
	}
	
	public static void assertHourPrecision(Time time) {
		assertEquals(0, time.getMinute());
		assertEquals(0, time.getSecond());
	}
	
	public static void assertMinutePrecision(Time time) {
		assertEquals(0, time.getSecond());
	}
	
	public static void assertIn(Time time, Time fromIncl, Time toExcl) {
		assertTrue(fromIncl.isBeforeOrEqualTo(time));
		assertTrue(time.isBefore(toExcl));
	}
	
	private List<Time> seletNDates(ParcelOrderStep<Time> selector, int n, Random rand) {
		List<Time> times = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			times.add(selector.select(null, null, 1, rand.nextDouble()));
		}
		
		return times;
	}

}
