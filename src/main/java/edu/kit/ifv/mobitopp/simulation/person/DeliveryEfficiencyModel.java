package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.parcels.tours.DistributionCenter;

/**
 * The Interface DeliveryEfficiencyModel
 * provides methods for creating {@link DeliveryEfficiencyProfile}s.
 */
public interface DeliveryEfficiencyModel {

	/**
	 * Selects a {@link DeliveryEfficiencyProfile} for the given {@link PickUpParcelPerson}.
	 *
	 * @param center the {@link DistributionCenter}
	 * @param person the person
	 * @return the {@link DeliveryEfficiencyProfile}
	 */
	public DeliveryEfficiencyProfile select(DistributionCenter center, Person person);
	
	/**
	 * Selects a loading duration.
	 *
	 * @param center the {@link DistributionCenter}
	 * @param person the person
	 * @return the loading duration in minutes
	 */
	public int selectLoadingDuration(DistributionCenter center, Person person);
	
	/**
	 * Selects an estimate trip duration.
	 *
	 * @param center the {@link DistributionCenter}
	 * @param person the person
	 * @return the estimate trip duration in minutes
	 */
	public int selectTripDuration(DistributionCenter center, Person person);
	
	/**
	 * Selects a base duration of a delivery.
	 *
	 * @param center the {@link DistributionCenter}
	 * @param person the person
	 * @return the base duration of a delivery in minutes
	 */
	public float selectBaseDeliveryDuration(DistributionCenter center, Person person);
	
	/**
	 * Selects the delivery duration per parcel.
	 *
	 * @param center the {@link DistributionCenter}
	 * @param person the person
	 * @return the delivery duration per parcel in minutes
	 */
	public float selectDeliveryDurationPerParcel(DistributionCenter center, Person person);
		
	/**
	 * Selects an  unloading duration.
	 *
	 * @param center the {@link DistributionCenter}
	 * @param person the person
	 * @return the unloading duration in minutes
	 */
	public int selectUnloadingDuration(DistributionCenter center, Person person);
}
