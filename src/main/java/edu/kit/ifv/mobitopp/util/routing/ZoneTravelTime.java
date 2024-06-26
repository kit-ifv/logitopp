package edu.kit.ifv.mobitopp.util.routing;

import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.time.Time;

public class ZoneTravelTime<E> implements TravelTimeProvider<E> {
	
	private final Function<E, Zone> embedding;
	private final ImpedanceIfc impedance;
	private StandardMode mode;
	private Time time;
	
	public ZoneTravelTime(Function<E, Zone> mapping, ImpedanceIfc impedance, StandardMode mode, Time time) {
		this.embedding = mapping;
		this.impedance = impedance;
		this.time = time;
		this.mode = mode;
	}

	@Override
	public float getTravelTime(E origin, E destination) {
		return impedance.getTravelTime(embedding.apply(origin).getId(), embedding.apply(destination).getId(), mode, time);
	}

	@Override
	public float getTravelTime(ZoneAndLocation origin, E destination) {
		return impedance.getTravelTime(origin.zone().getId(), embedding.apply(destination).getId(), mode, time);
	}

	@Override
	public float getTravelTime(E origin, ZoneAndLocation destination) {
		return impedance.getTravelTime(embedding.apply(origin).getId(), destination.zone().getId(), mode, time);
	}

	@Override
	public float getTravelTime(ZoneAndLocation origin, ZoneAndLocation destination) {
		return impedance.getTravelTime(origin.zone().getId(), destination.zone().getId(), mode, time);
	}

	public boolean setMode(StandardMode mode) {
		boolean changed = !this.mode.equals(mode);
		this.mode = mode;
		return changed;
	}

	public boolean setTime(Time time) {
		boolean changed = !this.time.equals(time);
		this.time = time;
		return changed;
	}

}
