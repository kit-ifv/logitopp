package edu.kit.ifv.mobitopp.simulation.distribution;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.distribution.fleet.VehicleType;
import edu.kit.ifv.mobitopp.simulation.distribution.region.ServiceArea;
import edu.kit.ifv.mobitopp.simulation.distribution.region.ServiceAreaFactory;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.util.location.LocationProvider;

/**
 * The Class DistributionCenterParser is a csv parser for
 * {@link DistributionCenter}s.
 */
public class DistributionCenterParser {
	private final ZoneRepository zoneRepo;
	private final double scaleFactor;
	private final LocationProvider locationProvider;
	
	private final Map<String, CEPServiceProvider> serviceProviders;
	private final ServiceAreaFactory serviceAreaFactory;
	private final DeliveryResults result;

	private final Function<VehicleType, Integer> maxParcelCountPerVehicle;
	
	/**
	 * Instantiates a new distribution center parser.
	 *
	 * @param zoneRepo                 the zone repository for assigning depot location
	 * @param scaleFactor              the scale factor to scale the fleet
	 * @param locationProvider         a factory for creating {@link ZoneAndLocation} from x/y coordinate
	 * @param serviceAreaFactory       a factory to create service areas
	 * @param result                   the results logger
	 * @param maxParcelCountPerVehicle
	 */
	public DistributionCenterParser(ZoneRepository zoneRepo, double scaleFactor, LocationProvider locationProvider, ServiceAreaFactory serviceAreaFactory, DeliveryResults result, Function<VehicleType, Integer> maxParcelCountPerVehicle) {
		this.zoneRepo = zoneRepo;
		this.scaleFactor = scaleFactor;
		this.locationProvider = locationProvider;
		this.serviceAreaFactory = serviceAreaFactory;
		this.maxParcelCountPerVehicle = maxParcelCountPerVehicle;

		this.serviceProviders = new LinkedHashMap<>();
		this.result = result;
	}
	
	public DistributionCenterParser(ZoneRepository zoneRepo, double scaleFactor, ImpedanceIfc impedance, DeliveryResults result, LocationProvider locationProvider, Function<VehicleType, Integer> maxParcelCountPerVehicle) {
		this(zoneRepo, scaleFactor, locationProvider, new ServiceAreaFactory(zoneRepo, impedance), result, maxParcelCountPerVehicle);
	}
	
	public Collection<DistributionCenter> parse(File file) {
		return parse(CsvFile.createFrom(file));
	}
	

	/**
	 * Parses the given csv file as {@link DistributionCenter}s.
	 *
	 * @param file the csv file to be parsed
	 * @return the collection of parsed distribution centers
	 */
	public Collection<DistributionCenter> parse(CsvFile file) {
		System.out.println("\nService Areas:");
		return file.stream().map(this::parse).collect(Collectors.toList());
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
		double volume = row.valueAsDouble("volume");
		int attempts = row.valueAsInteger("attempts");

		double x = row.valueAsDouble("loc_x");
		double y = row.valueAsDouble("loc_y");

		String zoneId = row.get("zone");
		Zone zone = zoneRepo.getByExternalId(zoneId);

		ZoneAndLocation location = locationProvider.getZoneAndLocation(x, y, zone);
		
		int vehicleType = row.valueAsInteger("vehicle_type");
		VehicleType type = VehicleType.fromInt(vehicleType);
		
		int serviceAreaCode = row.valueAsInteger("service_area");
		ServiceArea serviceArea = serviceAreaFactory.fromIntCode(zone, serviceAreaCode);

		int maxParcelCount = maxParcelCountPerVehicle.apply(type);

		DistributionCenter center = new DistributionCenter(id, name, cepsp, zone, location.location(), scaleVehicles(vehicles),
				volume, attempts, type, serviceArea, result, maxParcelCount);
		addCenterToServiceProvider(center, cepsp);

		System.out.println(name + " (" + id + ") serves " + serviceArea.size() + " zones!");
		result.logServiceArea(center);

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

	protected double scaleVehicleVolume(double volume) {
		return volume * scaleFactor;
	}
	

	
	private void addCenterToServiceProvider(DistributionCenter center, String cepsp) {
		if (cepsp.equals("ALL")) {return;}
		
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
