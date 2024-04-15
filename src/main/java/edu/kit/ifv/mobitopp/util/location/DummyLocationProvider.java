package edu.kit.ifv.mobitopp.util.location;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

import java.awt.geom.Point2D;

public class DummyLocationProvider implements LocationProvider {

    @Override
    public ZoneAndLocation getZoneAndLocation(double x, double y, Zone zone) {
        Point2D coordinate = new Point2D.Double(x, y);
        Location location = new Location(coordinate, 0, 0.0);
        return new ZoneAndLocation(zone, location);
    }
}
