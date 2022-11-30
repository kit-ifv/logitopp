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

/**
 * The Class DistributionCenterParser is a csv parser for
 * {@link DistributionCenter}s.
 */
public class DistributionCenterParser {
	private final ZoneRepository zoneRepo;
	final private double scaleFactor;

	/**
	 * Instantiates a new distribution center parser.
	 *
	 * @param zoneRepo    the zone repository for assigning depot location
	 * @param scaleFactor the scale factor to scale the fleet
	 */
	public DistributionCenterParser(ZoneRepository zoneRepo, double scaleFactor) {
		this.zoneRepo = zoneRepo;
		this.scaleFactor = scaleFactor;
	}

	/**
	 * Parses the given csv file as {@link DistributionCenter}s.
	 *
	 * @param file the csv file to be parsed
	 * @return the collection of parsed distribution centers
	 */
	public Collection<DistributionCenter> parse(CsvFile file) {
		return file.stream().map(r -> parse(r)).collect(Collectors.toList());
	}

	/**
	 * Parses the given csv row as {@link DistributionCenter}. Reads 'id', 'name',
	 * 'organisation', the number of 'employees' the private and business market
	 * shares 'share_private' and 'share_business', the number of deliver
	 * 'attempts', the x and y coordinates 'loc_x' and 'loc_y', and the 'zone'.
	 *
	 * @param row the row
	 * @return the distribution center
	 */
	public DistributionCenter parse(Row row) {
		int id = row.valueAsInteger("id");
		String name = row.get("name");
		String organisation = row.get("organisation");

		int employees = row.valueAsInteger("employees");
		double sharePrivate = row.valueAsDouble("share_private");
		double shareBusiness = row.valueAsDouble("share_business");
		int attempts = row.valueAsInteger("attempts");

		double x = row.valueAsDouble("loc_x");
		double y = row.valueAsDouble("loc_y");
		Location location = new Location(new Point2D.Double(x, y), 0, 0.5);

		String zoneId = row.get("zone");
		Zone zone = zoneRepo.getByExternalId(zoneId);

		return new DistributionCenter(id, name, organisation, zone, location, scaleEmployees(employees), sharePrivate,
				shareBusiness, attempts);
	}

	/**
	 * Scale the number of employees using this parser's scale factor.
	 *
	 * @param numOfEmployees the num of employees
	 * @return the int
	 */
	protected int scaleEmployees(int numOfEmployees) {
		return (int) max(1, ceil(numOfEmployees * scaleFactor));
	}
}
