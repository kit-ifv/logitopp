package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import lombok.Getter;
import lombok.Setter;

public class PrivateParcelBuilder extends ParcelBuilder<PickUpParcelPerson> {

	@Getter @Setter private ValueProvider<ParcelDestinationType> destinationType;

	public PrivateParcelBuilder(PickUpParcelPerson agent, DeliveryResults results) {
		super(agent, results);
	}

	@Override
	public IParcel doBuild() {
		ParcelDestinationType destination = this.destinationType.getValue();
		
		return new PrivateParcel(getAgent(),
								 destination,
								 destination.getZoneAndLocation(getAgent()),
								 getArrivalDate(),
								 getDistributionCenter().getValue(),
								 getSize().getValue(),
								 getVolume().getValue(),
								 getIsPickUp().getValue(),
								 getResults());
	}


}
