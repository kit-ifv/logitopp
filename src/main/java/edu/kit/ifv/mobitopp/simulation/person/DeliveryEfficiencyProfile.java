package edu.kit.ifv.mobitopp.simulation.person;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The Class DeliveryEfficiencyProfile provides estimated durations for
 * different activities performed by delivery persons.
 */
@AllArgsConstructor
public class DeliveryEfficiencyProfile {

	/**
	 * Gets the load duration.
	 *
	 * @return the load duration
	 */
	@Getter
	private int loadDuration;

	/**
	 * Gets the unload duration.
	 *
	 * @return the unload duration
	 */
	@Getter
	private int unloadDuration;

	/**
	 * Gets the estimated trip duration.
	 *
	 * @return the trip duration
	 */
	@Getter
	private int tripDuration;

	/**
	 * Gets the base delivery duration (independent of the amount of parcels).
	 *
	 * @return the base delivery duration
	 */
	@Getter
	private float deliveryDurBase;

	/**
	 * Gets the delivery duration per parcel.
	 *
	 * @return the delivery duration per parcel
	 */
	@Getter
	private float deliveryDurPerParcel;

}
