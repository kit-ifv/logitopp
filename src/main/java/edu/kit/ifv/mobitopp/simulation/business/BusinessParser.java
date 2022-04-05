package edu.kit.ifv.mobitopp.simulation.business;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class BusinessParser {
	
	private final ZoneRepository zoneRepo;
	
	public BusinessParser(ZoneRepository zoneRepo) {
		this.zoneRepo = zoneRepo;
	}
	
	public Collection<BusinessBuilder> parse(CsvFile file, long seed) {
		Random rand = new Random(seed); 
		return file.stream().map(r -> parse(r, rand.nextLong())).collect(Collectors.toList());
	}
	
	public BusinessBuilder parse(Row row, long seed) {
		String name = row.get("name");
		Branch branch = Branch.fromInt(row.valueAsInteger("branch"));
		int employees = row.valueAsInteger("employees");
		double area = row.valueAsDouble("area");		
		
		double x = row.valueAsDouble("loc_x");
		double y = row.valueAsDouble("loc_y");
		String zoneId = row.get("zone");
		
		ZoneAndLocation location = new ZoneAndLocation(zoneRepo.getByExternalId(zoneId), new Location(new Point2D.Double(x, y), 0, 0));
		
		BusinessBuilder builder = new BusinessBuilder(seed)
				.called(name)
				.with(area)
				.with(branch)
				.with(employees)
				.at(location);

		for (AreaFunction function : AreaFunction.values()) {
			builder.with(parseAreaFunction(function, row), function);
		}
		
		return builder;
	}
		
	private double parseAreaFunction(AreaFunction function, Row row) {
		String column = "function:"+function.asInt();
		
		if (row.containsAttribute(column)) {
			return row.valueAsDouble(column);
		} else {
			return 0.0;
		}
	}

}
