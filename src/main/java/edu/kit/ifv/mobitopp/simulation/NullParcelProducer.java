package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

/**
 * The NullParcelProducer is a {@link ParcelAgent} with no parcel production.
 */
public interface NullParcelProducer extends ParcelAgent {
		
	/**
	 * Adds the given parcel to the production plan.
	 * Throws {@link UnsupportedOperationException} as no parcels should be produced.
	 * 
	 * @param parcel the parcel to be added
	 */
	@Override
	default void addParcel(IParcel parcel) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore does not hold parcels!");
	}
	
	/**
	 * Removes the parcel from the production lpan.
	 * Throws {@link UnsupportedOperationException} as no parcels should be produced.
	 *
	 * @param parcel the parcel to be removed
	 */
	@Override
	default void removeParcel(IParcel parcel) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore does not hold parcels!");
	}
	
	/**
	 * Gets the {@link ParcelPolicyProvider policy provider}.
	 * Throws {@link UnsupportedOperationException} as no parcels are produced.
	 *
	 * @return the policy provider
	 */
	@Override
	default ParcelPolicyProvider getPolicyProvider() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore has no delivery policy!");
	}
	
	/**
	 * Adds the given parcel to the record of delivered parcels.
	 * Throws {@link UnsupportedOperationException} as no parcels are produced.
	 *
	 * @param parcel the parcel to be recorded as delivered
	 */
	@Override
	default void addDelivered(IParcel parcel) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a NullParcelProducer and therefore does not hold parcels!");
	}
	
}

