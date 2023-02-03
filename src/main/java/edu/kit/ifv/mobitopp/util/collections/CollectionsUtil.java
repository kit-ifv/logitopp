package edu.kit.ifv.mobitopp.util.collections;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public final class CollectionsUtil {
	
	public static <T> Collection<List<T>> groupBy(List<T> items, BiPredicate<T,T> equals) {
		
		final Map<T,List<T>> map = new LinkedHashMap<>();
		
		items.forEach(i -> {
			map.get(
				map.keySet()
				   .stream()
				   .filter(s -> equals.test(s, i))
				   .findFirst()
				   .orElseGet(() -> {map.put(i, new ArrayList<>()); return i;})
			).add(i);
		});
		
		List<List<T>> list = map.values().stream().collect(toList());
		
		Collections.sort(
				list,
				
				new Comparator<List<T>>() {
					@Override
					public int compare(List<T> o1, List<T> o2) {
						return Integer.compare(items.indexOf(o1.get(0)), items.indexOf(o2.get(0)));
					}
				}
		);
		
		return list;
	}
	
	public static <T> List<List<T>> partition(List<T> list, int maxSize) {
		if (list.size() <= maxSize) {
			return List.of(list);
		}
		
		int numParts = (int) Math.ceil(list.size() / (1.0*maxSize));
		
		List<List<T>> partitions = new ArrayList<>(numParts);
		for (int i=0; i < numParts; i++) {
			partitions.add(new ArrayList<>(maxSize));
		}
		
		int i = 0;
		for (T t : list) {
			partitions.get(i % numParts).add(t);
			i++;
		}
		
		return partitions;
	};
	
}
