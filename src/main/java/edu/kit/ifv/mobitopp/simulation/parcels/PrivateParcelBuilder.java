package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.simulation.parcels.demand.attributes.ValueProvider;
import edu.kit.ifv.mobitopp.simulation.person.PickUpParcelPerson;
import lombok.Setter;

public class PrivateParcelBuilder extends ParcelBuilder<PickUpParcelPerson> {

	@Setter private ValueProvider<ParcelDestinationType> destinationType;

	public PrivateParcelBuilder(PickUpParcelPerson agent, DeliveryResults results) {
		super(agent, results);
	}

	@Override
	public void notifyAgent() {
		//PickUpParcelPerson does not produce parcels
	}

	@Override
	protected IParcel doBuild() {
		ParcelDestinationType destination = this.destinationType.getValue();
		
		return new PrivateParcel(getAgent(),
								 destination,
								 destination.getZoneAndLocation(getAgent()),
								 getArrivalDate(),
								 getDistributionCenter().getValue(),
								 getSize().getValue(),
								 getResults());
	}


}
