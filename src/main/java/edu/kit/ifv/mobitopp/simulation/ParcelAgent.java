package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.demand.DemandQuantity;
import edu.kit.ifv.mobitopp.simulation.distribution.policies.ParcelPolicyProvider;
import edu.kit.ifv.mobitopp.simulation.parcels.IParcel;

/**
 * The Interface ParcelAgent provides methods for interacting with entities that may produce and/or receive parcels.
 */
public interface ParcelAgent {

	/**
	 * Gets the agent's demand quantity.
	 *
	 * @return the demand quantity
	 */
	public DemandQuantity getDemandQuantity();

	/**
	 * Removes the given parcel from the agent's production set.
	 *
	 * @param parcel the parcel
	 */
	public void removeParcel(IParcel parcel);

	/**
	 * Adds the given parcel from the agent's production set.
	 *
	 * @param parcel the parcel
	 */
	public void addParcel(IParcel parcel);

	/**
	 * Gets the agent's policy provider.
	 *
	 * @return the policy provider
	 */
	public ParcelPolicyProvider getPolicyProvider();

	/**
	 * Adds the given parcel as delivered.
	 *
	 * @param parcel the delivered parcel
	 */
	public void addDelivered(IParcel parcel);
		
	/**
	 * Gets the zone and location of the agent.
	 *
	 * @return the zone and location
	 */
	public ZoneAndLocation getZoneAndLocation();
	
	/**
	 * Gets the category tag for statistics logging of the agent.
	 *
	 * @return the category tag
	 */
	public String carrierTag();

}
