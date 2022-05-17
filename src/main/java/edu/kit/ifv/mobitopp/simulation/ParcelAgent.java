package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;
import edu.kit.ifv.mobitopp.simulation.parcels.PrivateParcel;

/**
 * The Interface ParcelAgent.
 */
public interface ParcelAgent {
	
	/**
	 * Sets the planned production quantity.
	 *
	 * @param quantity the new planned production quantity
	 */
	public void setPlannedProductionQuantity(int quantity);
	
	/**
	 * Gets the planned production quantity.
	 *
	 * @return the planned production quantity
	 */
	public int getPlannedProductionQuantity();
	
	/**
	 * Adds the actual production quantity.
	 *
	 * @param quantity the quantity
	 */
	public void addActualProductionQuantity(int quantity);
	
	/**
	 * Gets the remaining production quantity.
	 *
	 * @return the remaining production quantity
	 */
	public int getRemainingProductionQuantity();

	
	
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
	public void addDelivered(PrivateParcel parcel);
		

}
