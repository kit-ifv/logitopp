package edu.kit.ifv.mobitopp.simulation.demand.quantity;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class RandomNumberOfParcelsSelectorTest {
	
	@Test
	public void negativeMin() {
		assertThrows(IllegalArgumentException.class,
			() -> {
					new RandomNumberOfParcelsSelector<String>(-2, 4, 0.8);
				}
		);
	}
	
	@Test
	public void maxLessThanMin() {
		assertThrows(IllegalArgumentException.class,
			() -> {
					new RandomNumberOfParcelsSelector<String>(7, 4, 0.8);
				}
		);
	}
	
	@Test
	public void percentOutOfBounds() {
		assertThrows(IllegalArgumentException.class,
			() -> {
					new RandomNumberOfParcelsSelector<String>(2, 4, 1.8);
				}
		);
	}
	
	@Test
	public void customBounds() {
		ParcelQuantityModel<String> selector = new RandomNumberOfParcelsSelector<String>(4, 8, 1.0);

		Random rand = new Random(42);
		List<Integer> numbers = selectNNumbers(selector, 100, rand);

		for (Integer i : numbers) {
			assertTrue(4 <= i && i <= 8);
		}
	}
	
	@Test
	public void customPercentage() {
		ParcelQuantityModel<String> selector = new RandomNumberOfParcelsSelector<String>(4, 8, 0.6);

		Random rand = new Random(42);
		List<Integer> numbers = selectNNumbers(selector, 500, rand);

		double absolute = (double) numbers.stream().filter(i -> i!=0).count();
		double share = absolute / numbers.size();
		
		assertEquals(0.6, share, 0.01);
	}
	
	
	
	
	
	private List<Integer> selectNNumbers(ParcelQuantityModel<String> selector, int n, Random rand) {
		List<Integer> numbers = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			numbers.add(selector.select(null, rand.nextDouble()));
		}
		
		return numbers;
	}

}
