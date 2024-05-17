package edu.kit.ifv.mobitopp.simulation.distribution.tours.chains;

import edu.kit.ifv.mobitopp.simulation.distribution.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.time.Time;

public class StaticTransferTimeModel implements TransferTimeModel {

	@Override
	public int estimateTransferTimeMinutes(DistributionCenter hub, VehicleType from, VehicleType to, Time time) {
		if (from == VehicleType.OTHER) {
			return 0;
		}
		if (from == VehicleType.TRAM && from == to) {
			return 0;
		}
		return 5;
	}

}
