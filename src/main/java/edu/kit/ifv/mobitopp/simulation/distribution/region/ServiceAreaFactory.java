package edu.kit.ifv.mobitopp.simulation.distribution.region;

import static java.util.stream.Collectors.toList;

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
	
	public ServiceArea fullServiceArea() {
		return new ServiceArea(zoneRepository.getZones());
	}
	
	public ServiceArea serviceAreaByDistance(Zone center, float distance) {
		ZoneId centerId = center.getId();
		
		Collection<Zone> zones = 
			   zoneRepository.getZones()
							 .stream()
						 	 .filter(z -> impedance.getDistance(centerId, z.getId()) <= distance)
						 	 .collect(toList());
		
		return new ServiceArea(zones);
	}
	
	public ServiceArea noServiceArea() {
		return ServiceArea.empty();
	}
	
	public ServiceArea fromIntCode(Zone zone, int code) {
		if (code < 0) {
			return this.noServiceArea();
		}
		
		if (code == 0)  {
			return this.fullServiceArea();
		}
		
		return this.serviceAreaByDistance(zone, code);
	}

}
