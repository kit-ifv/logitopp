package edu.kit.ifv.mobitopp.simulation.business;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.*;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;

public class BusinessParser {
	
	private final ZoneRepository zoneRepo;
	private final SimpleRoadNetwork roadNetwork;
	private final DeliveryResults results;

	public BusinessParser(SimulationContext context, DeliveryResults results) {
		this.zoneRepo = context.zoneRepository();
		this.results = results;
		this.roadNetwork = initRoadNetwork(context);
	}

	public BusinessParser(ZoneRepository zoneRepository, SimpleRoadNetwork roadNetwork, DeliveryResults results) {
		this.zoneRepo = zoneRepository;
		this.roadNetwork = roadNetwork;
		this.results = results;
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

	public Collection<BusinessBuilder> parse(CsvFile file, long seed) {
		Random rand = new Random(seed); 
		return file.stream().map(r -> parse(r, rand.nextLong())).collect(Collectors.toList());
	}
	
	public BusinessBuilder parse(Row row, long seed) {
		long id = Long.parseLong(row.get("id"));
		String name = row.get("name");
		
		Branch branch = Branch.fromInt(row.valueAsInteger("branch"));
		BuildingType building = BuildingType.fromInt(row.valueAsInteger("building"));
		
		int employees = row.valueAsInteger("employees");
		double area = row.valueAsDouble("area");		
		
		double x = row.valueAsDouble("loc_x");
		double y = row.valueAsDouble("loc_y");
		String zoneId = row.get("zone");
		Zone zone = zoneRepo.getByExternalId(zoneId);

		ZoneAndLocation location = getZoneAndLocation(x, y, zone);


		BusinessBuilder builder = new BusinessBuilder(seed)
				.id(id)
				.called(name)
				.with(branch)
				.with(building)
				.with(employees)
				.with(area)
				.at(location);

		for (DayOfWeek day : DayOfWeek.values()) {
			Optional<Pair<Time, Time>> interval = parseOpeningHours(day, row);
			
			if (interval.isPresent()) {
				builder.openBetween(day, interval.get());
			}
		}
		
		return builder;
	}

	protected ZoneAndLocation getZoneAndLocation(double x, double y, Zone zone) {
		Point2D.Double coordinate = new Point2D.Double(x, y);

		SimpleEdge edge = roadNetwork.zone(zone.getId()).nearestEdge(coordinate);
		double pos = edge.nearestPositionOnEdge(coordinate);

        return new ZoneAndLocation(zone, new Location(coordinate, edge.id(), pos));
	}

	private Optional<Pair<Time,Time>> parseOpeningHours(DayOfWeek day, Row row) {
		String column = "open:"+day.name();
		
		if (!row.containsAttribute(column)) {
			return Optional.empty();
		}
		
		
		String range = row.get(column);
		
		if (!range.strip().isEmpty()) {
			String[] parts = range.split(",");
			
			Time from = Time.start.plusDays(day.getTypeAsInt())
								  .plusHours(Integer.parseInt(parts[0]));
			
			Time to = Time.start.plusDays(day.getTypeAsInt())
					  			.plusHours(Integer.parseInt(parts[1]));
			
			return Optional.of(new Pair<>(from, to));
			
		} else {
			return Optional.empty();
		}
	}

}
