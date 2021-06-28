package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class ParcelBuilder {
	
	private Time plannedArrivalDate;
	private DistributionCenter distributionCenter;
	private String deliveryService;
	
	private PickUpParcelPerson person;
	private ParcelDestinationType destinationType;
	
	private Opportunity opportunity;

	public ZoneAndLocation getDestinationAsLocation() {
		if (destinationType != null && person != null) {
			return destinationType.getZoneAndLocation(person);
		} else {
			throw new IllegalStateException("Cannot determine destination without a destination.");
		}
	}
	
	public PrivateParcel buildPrivateParcel(DeliveryResults results) {
		return new PrivateParcel(person, destinationType, getDestinationAsLocation(), plannedArrivalDate, distributionCenter, deliveryService, results);
	}
	
	public BusinessParcel buildBusinessParcel(ZoneRepository zoneRepo, DeliveryResults results) {
		Zone zone = zoneRepo.getZoneById(opportunity.zone());
		ZoneAndLocation location = new ZoneAndLocation(zone, opportunity.location());
		return new BusinessParcel(location, opportunity, plannedArrivalDate, distributionCenter, deliveryService, results);
	}
}
