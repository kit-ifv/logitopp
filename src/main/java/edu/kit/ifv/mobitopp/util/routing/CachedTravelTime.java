package edu.kit.ifv.mobitopp.util.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.time.Time;

public class CachedTravelTime<E> implements TravelTimeProvider<E> {
	
	private final TravelTimeProvider<E> travelTime;
	private final Map<E, Map<E, Float>> cache;
	private final Map<ZoneAndLocation, Map<E, Float>> egressCache;
	private final Map<E, Map<ZoneAndLocation, Float>> accessCache;
	private final Map<ZoneAndLocation, Map<ZoneAndLocation, Float>> otherCache;
	
	public CachedTravelTime(TravelTimeProvider<E> travelTime) {
		this.travelTime = travelTime;
		this.cache = new HashMap<>();
		this.egressCache = new HashMap<>();
		this.accessCache = new HashMap<>();
		this.otherCache = new HashMap<>();
	}

	public float getTravelTime(E origin, E destination) {
		return applyCache(origin, destination, cache, travelTime::getTravelTime);
	}

	public float getTravelTime(ZoneAndLocation origin, E destination) {
		return applyCache(origin, destination, egressCache, travelTime::getTravelTime);
	}

	public float getTravelTime(E origin, ZoneAndLocation destination) {
		return applyCache(origin, destination, accessCache, travelTime::getTravelTime);
	}

	public float getTravelTime(ZoneAndLocation origin, ZoneAndLocation destination) {
		return applyCache(origin, destination, otherCache, travelTime::getTravelTime);
	}
	
	private <O,D> float applyCache(O origin, D destination, Map<O, Map<D, Float>> cache, BiFunction<O, D, Float> travelTimeEval) {
		if (!cache.containsKey(origin)) {
			cache.put(origin, new HashMap<>());
		}
		
		Map<D, Float> destMap = cache.get(origin);
		if (!destMap.containsKey(destination)) {
			destMap.put(destination, travelTimeEval.apply(origin, destination));
		}
		
		return destMap.get(destination);
	}

	@Override
	public boolean setMode(StandardMode mode) {
		boolean modeChanged = travelTime.setMode(mode);
		if (modeChanged) {
			clearCaches();
		}
		return modeChanged;
	}

	@Override
	public boolean setTime(Time time) {
		boolean timeChanged = travelTime.setTime(time);
		if (timeChanged) {
			clearCaches();
		}
		return timeChanged;
	}
	
	private void clearCaches() {
		cache.clear();
		accessCache.clear();
		egressCache.clear();
		otherCache.clear();
	}
	
	

}
