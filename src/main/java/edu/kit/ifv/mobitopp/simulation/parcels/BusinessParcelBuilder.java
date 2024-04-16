package edu.kit.ifv.mobitopp.simulation.parcels;

import edu.kit.ifv.mobitopp.simulation.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.business.Business;

public class BusinessParcelBuilder extends ParcelBuilder<Business> {

	public BusinessParcelBuilder(Business agent, DeliveryResults results) {
		super(agent, results);
	}


	@Override
	public IParcel doBuild() {
		return new BusinessParcel(getAgent().location(),
								  getAgent(),
								  getConsumer().getValue(),
								  getArrivalDate(),
								  getProducer().getValue(),
								  getSize().getValue(),
								  getVolume().getValue(),
								  getIsPickUp().getValue(),
								  getResults());
	}

}
