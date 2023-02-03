package edu.kit.ifv.mobitopp.simulation.distribution;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class ServiceAreaFactory {
	
	private final ImpedanceIfc impedance;
	private final ZoneRepository zoneRepository;
	
	public ServiceAreaFactory(ZoneRepository zoneRepository, ImpedanceIfc impedance) {
		this.impedance = impedance;
		this.zoneRepository = zoneRepository;
	}
	
	public Collection<Zone> fullServiceArea() {
		return new ArrayList<>(zoneRepository.getZones());
	}
	
	public Collection<Zone> serviceAreaByDistance(Zone center, float distance) {
		ZoneId centerId = center.getId();
		
		return fullServiceArea().stream()
						 		.filter(z -> impedance.getDistance(centerId, z.getId()) <= distance)
						 		.collect(toList());
	}

}
