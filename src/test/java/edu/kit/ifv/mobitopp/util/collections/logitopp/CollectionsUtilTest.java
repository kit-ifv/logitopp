package edu.kit.ifv.mobitopp.util.collections.logitopp;

import static edu.kit.ifv.mobitopp.util.collections.CollectionsUtil.groupBy;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.Test;

public class CollectionsUtilTest {

	@Test
	public void groupSimpleList() {
		List<Integer> list = List.of(0,1,2,3,4,5,6,7,8,9);
		BiPredicate<Integer, Integer> equals = (i,j) -> (i+j)%2 == 0;
		Collection<List<Integer>> result = groupBy(list, equals);
		
		System.out.println(result);
		verify(list, equals, result);
	}
	
	public static <T> void verify(List<T> items, BiPredicate<T,T> equals, Collection<List<T>> result) {
		assertTrue(items.stream().allMatch(i -> result.stream().anyMatch(l -> l.contains(i))));
		assertTrue(result.stream().allMatch(l -> items.containsAll(l)));
		assertEquals(items.size(), result.stream().mapToInt(List::size).sum());

		for (List<T> group : result) {
			for (T elem : group) {
				assertTrue(items.stream().allMatch(i -> group.contains(i) == equals.test(i, elem)));
			}
		}
	}
}
