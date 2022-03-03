package edu.kit.ifv.mobitopp.simulation.parcels.agents;

public interface NullParcelProducer extends ParcelAgent {

	@Override
	default int getCurrentProductionQuantity() {
		return 0;
	}
	
	@Override
	default int getPlannedProductionQuantity() {
		return 0;
	}
	
	@Override
	default int getRemainingProductionQuantity() {
		return 0;
	}
	
	@Override
	default void setPlannedProductionQuantity(int quantity) {
		// Do nothing, no parcel production
	}
	
}

