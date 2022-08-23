package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

/**
 * The Interface ParcelAgent.
 */
public interface ParcelAgent {

	public DemandQuantity getDemandQuantity();

	/**
	 * Removes the parcel.
	 *
	 * @param parcel the parcel
	 */
	public void removeParcel(IParcel parcel);

	/**
	 * Adds the parcel.
	 *
	 * @param parcel the parcel
	 */
	public void addParcel(IParcel parcel);

	/**
	 * Gets the policy provider.
	 *
	 * @return the policy provider
	 */
	public ParcelPolicyProvider getPolicyProvider();

	/**
	 * Adds the delivered.
	 *
	 * @param parcel the parcel
	 */
	public void addDelivered(IParcel parcel);
		
	public ZoneAndLocation getZoneAndLocation();

}
