package edu.kit.ifv.mobitopp.simulation.parcels;

// TODO: Auto-generated Javadoc
/**
 * The Interface ParcelDeliveryPolicy provides methods deciding whether parcels can be delivered or should be updated.
 */
public interface ParcelDeliveryPolicy {

	/**
	 * Checks whether the parcel can be delivered.
	 * E.g. may check if the recipient or a neighbor is at home.
	 *
	 * @param parcel the parcel
	 * @return true, if it can be delivered
	 */
	public boolean canDeliver(Parcel parcel);
	
	/**
	 * Update the parcel delivery.
	 * E.g. if the delivery was attempted 3 times, the delivery can be sent to a pack station.
	 *
	 * @param parcel the parcel
	 * @return true, if successful
	 */
	public boolean updateParcelDelivery(Parcel parcel);
	
}
