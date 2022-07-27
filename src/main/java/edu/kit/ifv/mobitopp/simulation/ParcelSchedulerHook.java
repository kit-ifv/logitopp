package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.parcels.ParcelBuilder;
import edu.kit.ifv.mobitopp.time.Time;

public class ParcelSchedulerHook implements Hook {
	
	private final Map<Time, Collection<ParcelBuilder<?>>> parcels;
	private final boolean keepSchedule;
	
	public ParcelSchedulerHook(boolean keepSchedule) {
		this.parcels = new HashMap<>();
		this.keepSchedule = keepSchedule;
	}
	
	public void register(DemandSimulatorPassenger simulator) {
		simulator.addBeforeTimeSliceHook(this);
	}
	
	public void addParcels(ParcelBuilder<?> ... parcels) {
		Arrays.asList(parcels).forEach(this::addParcel);
	}
		
	public void addParcel(ParcelBuilder<?> parcel) {
		Time time = parcel.getArrivalDate();
		
		if (this.parcels.containsKey(time)) {
			this.parcels.get(time).add(parcel);
		} else {
			ArrayList<ParcelBuilder<?>> list = new ArrayList<>();
			list.add(parcel);
			this.parcels.put(time, list);
		}
	}

	@Override
	public void process(Time date) {
		
		if (this.parcels.containsKey(date)) {
			this.parcels.get(date).forEach(ParcelBuilder::get);
			
			if (!keepSchedule) {
				this.parcels.remove(date);
			}
			
		}
	}
	
	public void flushAllParcels() {
		for (Time time : this.parcels.keySet()) {
			process(time);
		}
	}
	

}
