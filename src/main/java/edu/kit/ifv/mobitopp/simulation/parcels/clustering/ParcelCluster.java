package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import lombok.Getter;

@Getter
public class ParcelCluster {
	
	private final List<IParcel> parcels;
	private final ZoneAndLocation zoneAndLocation;
	
	public ParcelCluster(List<IParcel> parcels, DeliveryClusteringStrategy clustering) {
		this.parcels = parcels;
		this.zoneAndLocation = clustering.getStopLocation(parcels);
	}

}
