package edu.kit.ifv.mobitopp.simulation.distribution.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

public class ServiceArea {
	
	private final Collection<Zone> zones;
	
	public ServiceArea(Collection<Zone> zones) {
		this.zones = new ArrayList<>(zones);
	}
	
	public static ServiceArea empty() {
		return new ServiceArea(List.of());
	}
	
	public boolean canServe(IParcel parcel) {
		return this.zones.contains(parcel.getZone());
	}

	public int size() {
		return zones.size();
	}
	
	public boolean exists() {
		return !zones.isEmpty();
	}

	public Collection<Zone> getZones() {
		return new ArrayList<>(this.zones);
	}
}
