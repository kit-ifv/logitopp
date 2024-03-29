package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class NormalDistributedNumberOfParcelsSelectorTest {

	@Test
	public void negativeMinCap() {
		assertThrows(IllegalArgumentException.class,
			() -> {
					new NormalDistributedNumberOfParcelsSelector<String>(1.0, 0.5, -2, 4);
				}
		);
	}
	
	@Test
	public void maxCapLessThanMinCap() {
		assertThrows(IllegalArgumentException.class,
			() -> {
					new NormalDistributedNumberOfParcelsSelector<String>(1.0, 0.5, 7, 4);
				}
		);
	}
	
	@Test
	public void minCapZero() {
		ParcelQuantityModel<String> selector =
			new NormalDistributedNumberOfParcelsSelector<>(5.0, 3.0, 10);
		
		Random rand = new Random(42);
		List<Integer> numbers = selectNNumbers(selector, 100, rand);
		
		for (Integer i : numbers) {
			assertTrue(0 <= i && i <= 10);
		}
	}
	
	@Test
	public void customCaps() {
		ParcelQuantityModel<String> selector =
			new NormalDistributedNumberOfParcelsSelector<>(5.0, 3.0, 4, 8);
		
		Random rand = new Random(42);
		List<Integer> numbers = selectNNumbers(selector, 100, rand);
		
		for (Integer i : numbers) {
			assertTrue(4 <= i && i <= 8);
		}
	}
	
	private List<Integer> selectNNumbers(ParcelQuantityModel<String> selector, int n, Random rand) {
		List<Integer> numbers = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			numbers.add(selector.select(null, rand.nextDouble()));
		}
		
		return numbers;
	}
}
