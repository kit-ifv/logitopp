package edu.kit.ifv.mobitopp.util.location;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

public interface LocationProvider {

    public ZoneAndLocation getZoneAndLocation(double x, double y, Zone zone);

}
