package edu.kit.ifv.mobitopp.simulation.distribution.tours.coordinated;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.time.Time;

public interface TransferTimeModel {

	public int estimateTransferTimeMinutes(DistributionCenter hub, VehicleType from, VehicleType to, Time time);
}
