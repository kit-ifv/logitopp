package edu.kit.ifv.mobitopp.util.routing;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModeTravelTimes<E> {

    private final Map<Mode, CachedTravelTime<E>> modeTravelTimes = new LinkedHashMap<>();
    private final Supplier<TravelTimeProvider<E>> travelTimeFactory;
    private Time time = SimpleTime.start;

    public ModeTravelTimes(Supplier<TravelTimeProvider<E>> travelTimeFactory) {
        this.travelTimeFactory = travelTimeFactory;
    }

    private CachedTravelTime<E> initModeCache(StandardMode mode) {
        CachedTravelTime<E> cache = new CachedTravelTime<>(travelTimeFactory.get());
        modeTravelTimes.put(mode, cache);
        cache.setMode(mode);
        cache.setTime(time);
        return cache;
    }

    private CachedTravelTime<E> getCache(StandardMode mode) {
        if (modeTravelTimes.containsKey(mode)) {
            return modeTravelTimes.get(mode);
        }

        return initModeCache(mode);
    }


    public float getTravelTime(StandardMode mode, E origin, E destination) {
        return getCache(mode).getTravelTime(origin, destination);
    }

    public float getTravelTime(StandardMode mode, ZoneAndLocation origin, E destination) {
        return getCache(mode).getTravelTime(origin, destination);
    }

    public float getTravelTime(StandardMode mode, E origin, ZoneAndLocation destination) {
        return getCache(mode).getTravelTime(origin, destination);
    }

    public float getTravelTime(StandardMode mode, ZoneAndLocation origin, ZoneAndLocation destination) {
        return getCache(mode).getTravelTime(origin, destination);
    }

    public void setTime(Time newTime) {
        this.time = newTime;
        modeTravelTimes.values().forEach(c -> c.setTime(newTime));
    }


}
