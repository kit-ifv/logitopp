package edu.kit.ifv.mobitopp.simulation.business;

import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.*;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.collections.Pair;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.util.location.LocationProvider;

public class BusinessParser {
	
	private final ZoneRepository zoneRepo;
	private final LocationProvider locationProvider;

	public BusinessParser(SimulationContext context, LocationProvider locationProvider) {
		this.zoneRepo = context.zoneRepository();
		this.locationProvider = locationProvider;
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

		ZoneAndLocation location = locationProvider.getZoneAndLocation(x, y, zone);


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

            interval.ifPresent(timeTimePair -> builder.openBetween(day, timeTimePair));
		}
		
		return builder;
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
