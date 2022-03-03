package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilteredNumberOfParcelsSelectorTest {
	
	private FilteredNumberOfParcelsSelector<String> filterSelector;
	private ParcelQuantityModel<String> otherSelector;
	private Predicate<String> filter;
	
	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {
		this.filter = s -> s.length() % 2 == 0;
		
		this.otherSelector = mock(ParcelQuantityModel.class);
		when(this.otherSelector.select(anyString(), anyDouble())).thenAnswer(i -> ((String) i.getArguments()[0]).length());

		this.filterSelector = new FilteredNumberOfParcelsSelector<>(otherSelector, filter);
	}
	
	@Test
	public void selectFilterMatch() {
		String recipient = "this is an even string";
		double randomNumber = 4.2;
		
		int result = this.filterSelector.select(recipient, randomNumber);
	
		verify(this.otherSelector, times(1)).select(recipient, randomNumber);
		assertEquals(result, recipient.length());
	}
	
	@Test
	public void selectFilterNoMatch() {
		String recipient = "this is an odd string";
		double randomNumber = 1.7;
		
		int result = this.filterSelector.select(recipient, randomNumber);
	
		verify(this.otherSelector, times(0)).select(anyString(), anyDouble());
		assertEquals(result, 0);
	}

}
