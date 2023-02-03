package edu.kit.ifv.mobitopp.simulation.distribution;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.fleet.VehicleType;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

/**
 * The Class DistributionCenterParser is a csv parser for
 * {@link DistributionCenter}s.
 */
public class DistributionCenterParser {
	private final ZoneRepository zoneRepo;
	private final double scaleFactor;
	private final ImpedanceIfc impedance;
	
	private final Map<String, CEPServiceProvider> serviceProviders;

	
	/**
	 * Instantiates a new distribution center parser.
	 *
	 * @param zoneRepo    the zone repository for assigning depot location
	 * @param scaleFactor the scale factor to scale the fleet
	 */
	public DistributionCenterParser(ZoneRepository zoneRepo, double scaleFactor, ImpedanceIfc impedance) {
		this.zoneRepo = zoneRepo;
		this.scaleFactor = scaleFactor;
		this.impedance = impedance;
		
		this.serviceProviders = new LinkedHashMap<>();
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
	 * 'cepsp', the number of 'vehicles' the private and business market
	 * shares 'share_private' and 'share_business', the number of deliver
	 * 'attempts', the x and y coordinates 'loc_x' and 'loc_y', and the 'zone'.
	 *
	 * @param row the row
	 * @return the distribution center
	 */
	public DistributionCenter parse(Row row) {
		int id = row.valueAsInteger("id");
		String name = row.get("name");
		String cepsp = row.get("cepsp");

		int vehicles = row.valueAsInteger("vehicles");
		int attempts = row.valueAsInteger("attempts");

		double x = row.valueAsDouble("loc_x");
		double y = row.valueAsDouble("loc_y");
		Location location = new Location(new Point2D.Double(x, y), 0, 0.5);

		String zoneId = row.get("zone");
		Zone zone = zoneRepo.getByExternalId(zoneId);
		
		int vehicleType = row.valueAsInteger("vehicle_type");
		VehicleType type = VehicleType.fromInt(vehicleType);

		DistributionCenter center = new DistributionCenter(id, name, cepsp, zone, location, scaleVehicles(vehicles), attempts, type, impedance);
		addCenterToServiceProvider(center, cepsp);
		return center;
	}
	

	
	/**
	 * Scale the number of vehicles using this parser's scale factor.
	 *
	 * @param numOfVehicles the num of vehicles
	 * @return the scaled number of vehicles
	 */
	protected int scaleVehicles(int numOfVehicles) {
		return (int) max(1, ceil(numOfVehicles * scaleFactor));
	}
	

	
	private void addCenterToServiceProvider(DistributionCenter center, String cepsp) {
		
		if (serviceProviders.containsKey(cepsp)) {
			serviceProviders.get(cepsp).addDistributionCenter(center);
			
		} else {
			CEPServiceProvider provider = new CEPServiceProvider(cepsp);
			serviceProviders.put(cepsp, provider);
			provider.addDistributionCenter(center);
		}
	}
	
	
	public Collection<CEPServiceProvider> getServiceProviders() {
		return this.serviceProviders.values();
	}
}
