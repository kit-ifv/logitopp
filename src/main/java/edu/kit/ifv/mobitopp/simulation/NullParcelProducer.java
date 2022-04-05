package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;

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
	
	@Override
	default int getPlannedProductionQuantity() {
		return 0;
	}
	
	@Override
	default void addParcel(IParcel parcel) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore does not hold parcels!");
	}
	
	@Override
	default void removeParcel(IParcel parcel) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore does not hold parcels!");
	}
	
	@Override
	default ParcelPolicyProvider getPolicyProvider() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore has no delivery policy!");
	}
	
	@Override
	default void addDelivered(PrivateParcel parcel) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore does not hold parcels!");
	}
	
}

