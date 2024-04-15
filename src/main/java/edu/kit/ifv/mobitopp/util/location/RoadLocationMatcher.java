package edu.kit.ifv.mobitopp.util.location;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.*;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;

import java.awt.geom.Point2D;
import java.io.File;

public class RoadLocationMatcher implements LocationProvider {
    private final SimpleRoadNetwork roadNetwork;
    private final DeliveryResults results;

    public RoadLocationMatcher(SimulationContext context, DeliveryResults results) {
        this.results = results;
        this.roadNetwork = initRoadNetwork(context);
    }

    private SimpleRoadNetwork initRoadNetwork(SimulationContext context) {
        WrittenConfiguration configuration = context.configuration();

        String carSystem = configuration.getVisumToMobitopp().getCarTransportSystemCode();
        String individualWalkSystem = configuration.getVisumToMobitopp().getIndividualWalkTransportSystemCode();
        String publicTransportWalkSystem = configuration.getVisumToMobitopp().getPtWalkTransportSystemCode();
        StandardNetfileLanguages builder = StandardNetfileLanguages
                .builder()
                .carSystem(carSystem)
                .individualWalkSystem(individualWalkSystem)
                .publicTransportWalkSystem(publicTransportWalkSystem)
                .build();
        LanguageFactory factory = StandardNetfileLanguages::english;
        NetfileLanguage language = factory.createFrom(builder);

        File visumFile = Convert.asFile(configuration.getVisumFile());
        String carSystemCode = configuration.getVisumToMobitopp().getCarTransportSystemCode();
        VisumNetwork visum = new VisumNetworkReader(language).readNetwork(visumFile, carSystemCode);

        visum.nodes.values().forEach(results::logNode);

        visum.links.links.values().forEach(link -> {
            results.logEdge(link.linkA);
            results.logEdge(link.linkB);
        });

        return new SimpleRoadNetwork(visum, visum.transportSystems.getBy(carSystemCode));
    }

    public ZoneAndLocation getZoneAndLocation(double x, double y, Zone zone) {
        Point2D.Double coordinate = new Point2D.Double(x, y);

        SimpleEdge edge = roadNetwork.zone(zone.getId()).nearestEdge(coordinate);
        double pos = edge.nearestPositionOnEdge(coordinate);

        return new ZoneAndLocation(zone, new Location(coordinate, edge.id(), pos));
    }

}
