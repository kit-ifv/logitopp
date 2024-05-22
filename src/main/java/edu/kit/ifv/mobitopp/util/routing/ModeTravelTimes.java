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

    private final Map<Mode, TravelTimeProvider<E>> modeTravelTimes = new LinkedHashMap<>();
    private final Supplier<TravelTimeProvider<E>> travelTimeFactory;
    private Time time = SimpleTime.start;

    public ModeTravelTimes(Supplier<TravelTimeProvider<E>> travelTimeFactory) {
        this.travelTimeFactory = travelTimeFactory;
    }

    private TravelTimeProvider<E> initModeCache(StandardMode mode) {
        TravelTimeProvider<E> provider = travelTimeFactory.get();
        modeTravelTimes.put(mode, provider);
        provider.setMode(mode);
        provider.setTime(time);
        return provider;
    }

    private TravelTimeProvider<E> getProvider(StandardMode mode) {
        if (modeTravelTimes.containsKey(mode)) {
            return modeTravelTimes.get(mode);
        }

        return initModeCache(mode);
    }


    public float getTravelTime(StandardMode mode, E origin, E destination) {
        return getProvider(mode).getTravelTime(origin, destination);
    }

    public float getTravelTime(StandardMode mode, ZoneAndLocation origin, E destination) {
        return getProvider(mode).getTravelTime(origin, destination);
    }

    public float getTravelTime(StandardMode mode, E origin, ZoneAndLocation destination) {
        return getProvider(mode).getTravelTime(origin, destination);
    }

    public float getTravelTime(StandardMode mode, ZoneAndLocation origin, ZoneAndLocation destination) {
        return getProvider(mode).getTravelTime(origin, destination);
    }

    public void setTime(Time newTime) {
        this.time = newTime;
        modeTravelTimes.values().forEach(c -> c.setTime(newTime));
    }


}
