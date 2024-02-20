package edu.kit.ifv.mobitopp.util.routing;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.time.Time;

public interface TravelTimeProvider<E> {
	
	public float getTravelTime(E origin, E destination);

	public float getTravelTime(ZoneAndLocation origin, E destination);

	public float getTravelTime(E origin, ZoneAndLocation destination);

	public float getTravelTime(ZoneAndLocation origin, ZoneAndLocation destination);
	
	
	public boolean setMode(StandardMode mode); //TODO check where called
	public boolean setTime(Time time);

}
