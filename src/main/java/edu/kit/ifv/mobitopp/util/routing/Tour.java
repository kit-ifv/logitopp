package edu.kit.ifv.mobitopp.util.routing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import lombok.Getter;

public class Tour<E> implements Iterable<E> {
	
	@Getter private final List<E> elements;
	@Getter private float travelTime;
	private final TravelTimeProvider<E> travelTimeProvider;
	
	private int start = 0;
	
	public Tour(CachedTravelTime<E> dijkstra) {
		this(List.of(), 0.0f, dijkstra);
	}
	
	public Tour(List<E> elements, float travelTime, CachedTravelTime<E> dijkstra) {
		this.elements = new ArrayList<>(elements);
		this.travelTimeProvider = dijkstra;
		this.travelTime = travelTime;
	}
	
	public int size() {
		return elements.size();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<E> iterator() {
		return IntStream.range(0, size())
						 .mapToObj(i -> elements.get( (i + start) % size() ))
						 .iterator();
	}
	
	public E getModulo(int index) {
		return elements.get(indexModulo(index));
	}
	
	public E getModuloFromStart(int index) {
		return getModulo(index+start);
	}
	
	public int indexModulo(int index) {
		if (index < 0) { return indexModulo(index + size()); }
		return index % size();
	}
	
	
	
	
	public float selectMinInsertionStart(ZoneAndLocation hub) {
		start = findMinInsertionIndex(hub);
		return getInsertionCost(hub, start);
	}
	
	public void insertAtMinPosition(E element) {
		int index = findMinInsertionIndex(element);
		float delta = getInsertionCost(element, index);
		
		elements.add(index, element);
		travelTime += delta; //TODO Check > 0?
	}
	
	public float minInsertionCost(E elem) {
		if(isEmpty()) {return 0.0f;}
		return getInsertionCost(elem, findMinInsertionIndex(elem));
	}
	
	public int findMinInsertionIndex(E elem) {
		if(isEmpty()) {return 0;}
		return findMinInsertionIndex(elem, this::getInsertionCost);
	}
	
	public int findMinInsertionIndex(ZoneAndLocation loc) {
		if(isEmpty()) {return 0;}
		return findMinInsertionIndex(loc, this::getInsertionCost);
	}
	
	public float getInsertionCost(ZoneAndLocation loc, int index) {
		if(isEmpty()) {return 0.0f;}
		E prev = getModulo(index-1);
		E next = getModulo(index);
		return getInsertionCost(prev, loc, next);
	}
	
	private float getInsertionCost(E prev, ZoneAndLocation hub, E next) {
		if(isEmpty()) {return 0.0f;}
		return travelTimeProvider.getTravelTime(prev, hub) + travelTimeProvider.getTravelTime(hub, next) - travelTimeProvider.getTravelTime(prev, next);
	}
	
	private float getInsertionCost(E element, int index) {
		if(isEmpty()) {return 0.0f;}
		E prev = getModulo(index-1);
		E next = getModulo(index);
		return getInsertionCost(prev, element, next);
	}
	
	private float getInsertionCost(E prev, E element, E next) {
		if(isEmpty()) {return 0.0f;}
		return travelTimeProvider.getTravelTime(prev, element) + travelTimeProvider.getTravelTime(element, next) - travelTimeProvider.getTravelTime(prev, next);
	}
	
	interface CostEval<E, T> {
		public float eval(E prev, T element, E next);
	}
	
	private <T> int findMinInsertionIndex(T toInsert, CostEval<E, T> costEval) {
		float insertionCost = Float.MAX_VALUE;
		int index = 0;
		
		for (int i=0; i < size(); i++) {
			E prev = elements.get(i);
			E next = getModulo(i+1); //TODO check in test
			
			float cost = costEval.eval(prev, toInsert, next);
			
			if (cost < insertionCost) {
				insertionCost = cost;
				index = i+1;
			}
			
		}
		
		return index;
	}
	
	
	
	
	public float remove(E element) {
		return removeAtPosition(elements.indexOf(element));
	}
	
	public float removeAtPosition(int index) {
		float delta = getRemovalCost(index);
		
		travelTime += delta;
		elements.remove(index);
		
		return delta;
	}
	
	public int findMinRemovalIndex() {
		return findMinRemovalIndex(this::getRemovalCost);
	}
	
	public float getRemovalCost(E element) {
		return getRemovalCost(elements.indexOf(element));
	}
	
	public float getRemovalCost(int index) {
		E prev = getModulo(index-1);
		E toRemove = getModulo(index);
		E next = getModulo(index+1);
		return getRemovalCost(prev, toRemove, next);
	}
	
	public float getRemovalCost(E prev, E element, E next) {
		return travelTimeProvider.getTravelTime(prev, next) - travelTimeProvider.getTravelTime(prev, element) - travelTimeProvider.getTravelTime(element, next);
	}
	
	private int findMinRemovalIndex(CostEval<E, E> costEval) {
		float removalCost = Float.MAX_VALUE;
		int index = 0;
		
		for (int i=0; i < size(); i++) {
			E prev = elements.get(i);
			E toRemove = getModulo(i+1);
			E next = getModulo(i+2);
			
			float cost = costEval.eval(prev, toRemove, next);
			
			if (cost < removalCost) {
				removalCost = cost;
				index = i+1;
			}
			
		}
		
		return indexModulo(index);
	}
	
	@Override
	public String toString() {
		return elements.toString();
	}

}
