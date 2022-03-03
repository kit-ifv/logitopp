package edu.kit.ifv.mobitopp.simulation;

public interface NullParcelProducer extends ParcelAgent {

	@Override
	default int getRemainingProductionQuantity() {
		return 0;
	}
	
	@Override
	default void setPlannedProductionQuantity(int quantity) {
		// Do nothing, no parcel production
	}
	
	@Override
	default void addActualProductionQuantity(int quantity) {
		// Do nothing, no parcel production
	}
	
}

