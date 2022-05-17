package edu.kit.ifv.mobitopp.simulation.distribution;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class DistributionCenterParser {

	private final ZoneRepository zoneRepo;
	final private double scaleFactor;
	
	public DistributionCenterParser(ZoneRepository zoneRepo, double scaleFactor) {
		this.zoneRepo = zoneRepo;
		this.scaleFactor = scaleFactor;
	}
	
	public Collection<DistributionCenter> parse(CsvFile file) {
		return file.stream().map(r -> parse(r)).collect(Collectors.toList());
	}
	
	public DistributionCenter parse(Row row) {
		String name = row.get("name");
		String organisation = row.get("organisation");
		
		int employees = row.valueAsInteger("employees");
		double share = row.valueAsDouble("share");		
		int attempts = row.valueAsInteger("attempts");
		
		double x = row.valueAsDouble("loc_x");
		double y = row.valueAsDouble("loc_y");
		Location location = new Location(new Point2D.Double(x, y), 0, 0.5);
		
		String zoneId = row.get("zone");		
		Zone zone = zoneRepo.getByExternalId(zoneId);
		
		return new DistributionCenter(name, organisation, zone, location, scaleEmployees(employees), share, attempts);
	}
	
	protected int scaleEmployees(int numOfEmployees) {
		return (int) max(1, ceil(numOfEmployees * scaleFactor));
	}
}
