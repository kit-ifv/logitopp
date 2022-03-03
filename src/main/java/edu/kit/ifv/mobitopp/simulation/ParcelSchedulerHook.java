package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
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
	
	
	public void addParcel(ParcelBuilder<?> parcel) {
		Time time = parcel.getArrivalDate();
		
		if (this.parcels.containsKey(time)) {
			this.parcels.get(time).add(parcel);
		} else {
			this.parcels.put(time, new ArrayList<>()).add(parcel);
		}
	}

	@Override
	public void process(Time date) {
		
		if (this.parcels.containsKey(date)) {
			this.parcels.get(date).forEach(ParcelBuilder::doBuild);
			
			if (!keepSchedule) {
				this.parcels.remove(date);
			}
			
		}
	}
	

}
