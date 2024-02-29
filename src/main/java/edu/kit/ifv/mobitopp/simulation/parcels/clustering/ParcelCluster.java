package edu.kit.ifv.mobitopp.simulation.parcels.clustering;

import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import lombok.Getter;

@Getter
public class ParcelCluster {
	private final ZoneAndLocation zoneAndLocation;
	private final List<IParcel> parcels;
	
	
	public ParcelCluster(List<IParcel> parcels, DeliveryClusteringStrategy clustering) {
		this.parcels = parcels;
		this.zoneAndLocation = clustering.getStopLocation(parcels);
	}
	
	@Override
	public String toString() {
		return zoneAndLocation.zone().getId().getExternalId() + ":[" + parcels.stream().map(p -> p.getOId() + "").collect(Collectors.joining(", ")) + "]";
	}

}
