package edu.kit.ifv.mobitopp.simulation.parcels.model;

import edu.kit.ifv.mobitopp.simulation.parcels.DeliveryResults;
import edu.kit.ifv.mobitopp.simulation.parcels.business.Business;

public class BusinessParcelBuilder extends ParcelBuilder<Business> {

	public BusinessParcelBuilder(Business agent, DeliveryResults results) {
		super(agent, results);
	}


	@Override
	public void notifyAgent() {
		if (getProducer().isDetermined() && getProducer().getValue().equals(getAgent())) {
			getAgent().addProducts(1);
		}
	}

	@Override
	public IParcel doBuild() {
		return new BusinessParcel(getAgent().location(),
								  getAgent(),
								  getArrivalDate(),
								  getDistributionCenter().getValue(),
								  "Dummy",
								  getSize().getValue(),
								  getResults());
	}

}
